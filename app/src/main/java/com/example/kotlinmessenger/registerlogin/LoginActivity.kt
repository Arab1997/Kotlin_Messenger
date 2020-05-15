package com.example.kotlinmessenger.registerlogin

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinmessenger.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        button_login.setOnClickListener {
            val email = email_login.text.toString()
            val password = password_login.text.toString()


            Log.d("Login", "Attempt  login  with email/pw:$email/***")
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener()
        }


        back_to_register.setOnClickListener {
            finish()
            /* Log.d("MainActivity", "Try  to show register activity")

             //launch the login activity somehow
             val intent = Intent(this, MainActivity::class.java)
             startActivity(intent)*/
        }
    }
}