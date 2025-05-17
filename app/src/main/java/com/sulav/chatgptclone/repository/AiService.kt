package com.sulav.chatgptclone.repository

import kotlinx.coroutines.flow.Flow

interface AiService {
    fun sendAndStreamReply(prompt: String): Flow<String>
}
