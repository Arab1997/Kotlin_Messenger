package com.example.kotlinmessenger.registerlogin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.kotlinmessenger.messages.LatestMessagesActivity
import com.example.kotlinmessenger.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        button_register.setOnClickListener {
            performRegister()
        }

        already_have_an_account.setOnClickListener {
            Log.d("MainActivity", "Try to show login activity")

            //launch the login activity somehow
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }


        select_photo.setOnClickListener {

            Log.d("MainActivity", "Try to show photo selector")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }


    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            //protected and check what the selected image was...
            Log.d("MainActivity", "Photo was selected")

            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            select_photo.setImageBitmap(bitmap)

            button_register.alpha = 0f

            /*val bitmapDrawable = BitmapDrawable(bitmap)
            button_register.setBackgroundDrawable(bitmapDrawable)*/
        }
    }


    private fun performRegister() {
        val email = email_edittext_reg.text.toString()
        val password = password_edittext_reg.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter text in email/pw", Toast.LENGTH_SHORT).show()
            return

        }
        Log.d("MainActivity", "Email is: " + email)
        Log.d("MainActivity", "Password is:$password")

        //Firebase
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                //else if successful
                Log.d(
                    "Main",
                    "Successfully created user with uid: ${it.result?.user?.uid}"
                ) /* it.result.user.uid    shunaqa edi*/

                uploadImageToFirebaseStorage()
            }
            .addOnFailureListener {
                Log.d("Main", "Failed to create: ${it.message}")
                Toast.makeText(this, "Failed to create: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d(
                    "Register",
                    "Successfully uploaded image: ${it.metadata?.path}"
                )

                ref.downloadUrl.addOnSuccessListener {
                    it.toString()
                    Log.d(
                        "RegisterActivity",
                        "File Location: $it"
                    )

                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {
                //do some logging here
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users$uid")
        val user = User(
            uid,
            username_edittext_reg.text.toString(),
            profileImageUrl
        )
        ref.setValue(user)
            .addOnSuccessListener {
                //  Log.d(TAG, "Finally we saved the user to Firebase db")
                Log.d("test", "Finally we saved the user to Firebase db")


                val intent = Intent(this, LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                // Log.d(TAG, "Failed to set value to database: ${it.message}")
                Log.d("TAG", "Failed to set value to database: ${it.message}")
            }
    }
}

class User(val uid: String, val username: String, val profileImageUrl: String){
    constructor() : this("","","")
}

