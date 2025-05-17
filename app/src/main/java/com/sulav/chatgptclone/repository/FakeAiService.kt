package com.sulav.chatgptclone.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeAiService : AiService {
    override fun sendAndStreamReply(prompt: String): Flow<String> = flow {
        delay(500)              // fake thinking
        emit("Hello! (fake)")   // first token
        delay(500)
        emit(" 👋")             // second token
    }
}
