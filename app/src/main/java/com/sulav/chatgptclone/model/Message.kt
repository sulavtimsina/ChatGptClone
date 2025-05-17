package com.sulav.chatgptclone.model

data class Message(
    val id: Long = 0,
    val role: Role,
    val content: String
) {
    enum class Role { USER, ASSISTANT }
}
