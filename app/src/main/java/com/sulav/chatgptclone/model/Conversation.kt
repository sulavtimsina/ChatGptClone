package com.sulav.chatgptclone.model

data class Conversation(
    val id: Long = 0,
    val title: String,
    val messages: List<Message> = emptyList()
)
