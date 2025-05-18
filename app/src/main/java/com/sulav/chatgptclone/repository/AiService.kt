package com.sulav.chatgptclone.repository

import com.sulav.chatgptclone.model.Message
import kotlinx.coroutines.flow.Flow

interface AiService {
    fun streamReplyWithHistory(history: List<Message>): Flow<String>
}
