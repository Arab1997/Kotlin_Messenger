package com.example.kotlinmessenger.models

import android.os.Parcelable


class ChatMessage(val id: String, val username: String, val profileImageUrl: String): Parcelable
{
    constructor() : this("", "", "")
}