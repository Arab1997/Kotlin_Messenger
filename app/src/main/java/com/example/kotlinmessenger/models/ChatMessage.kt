package com.example.kotlinmessenger.models

import android.os.Parcelable


class ChatMessage(val id: String, val text: String, val fromId: String, val toId: String, val timestamp: Long)
{
    constructor() : this("", "", "","",-1 )
}