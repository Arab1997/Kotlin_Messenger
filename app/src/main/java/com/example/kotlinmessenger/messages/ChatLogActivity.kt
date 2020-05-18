package com.example.kotlinmessenger.messages

import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.activity_new_message.*

class ChatLogActivity : AppCompatActivity() {
    companion object {
        val TAG = "Chatlog"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

        // supportActionBar?.title = "Chat Log"
        //supportActionBar?.title = username
        supportActionBar?.title = user.username

        setupDummyData()
        listenForMessages()


        sendbtn_chat_log.setOnClickListener {
            Log.d(TAG, "Attempt to send message...")
            performSendMessage()

        }
    }

    private fun listenForMessages() {
        val ref = FirebaseDatabase.getInstance().getReference("/messages")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val  chatMessage = p0.getValue(ChatMessage::class.java)
                Log.d(TAG,chatMessage?.text)
            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
    }

    class ChatMessage(
        val id: String,
        val text: String,
        val fromId: String,
        val toId: String,
        val timestampp: Long
    ){
        constructor() : this("","","","", -1)
    }


    private fun performSendMessage() {
        val reference = FirebaseDatabase.getInstance().getReference("/message").push()

        val text = editText_chat_log.text.toString()

        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user.uid

        if (fromId == null) return

        val chatMessage =
            ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis() / 1000)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved our chat message: ${reference.key}")
            }
    }

    private fun setupDummyData() {
        val adapter = GroupAdapter<ViewHolder>()
        adapter.add(ChatItem("FROM MESSAGE"))
        adapter.add(ChatToItem("TO MESSA\nGES"))
        adapter.add(ChatItem("FROM MESSAGE"))
        adapter.add(ChatToItem("TO MESSA\nGES"))
        adapter.add(ChatItem("FROM MESSAGE"))
        adapter.add(ChatToItem("TO MESSA\nGES"))
        adapter.add(ChatItem("FROM MESSAGE"))
        adapter.add(ChatToItem("TO MESSA\nGES"))
        adapter.add(ChatItem("FROM MESSAGE"))
        adapter.add(ChatToItem("TO MESSA\nGES"))

        recyclerview_chat_log.adapter = adapter
    }
}

class ChatItem(val text: String) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        //  viewHolder.itemView.text_from_row.text = text

    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }


}

class ChatToItem(val text: String) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
//  viewHolder.itemView.text_to_row.text = text

    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}