package com.sulav.chatgptclone.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ConversationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val title: String = "New Chat"
)
