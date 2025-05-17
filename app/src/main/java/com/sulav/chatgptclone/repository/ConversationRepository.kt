package com.sulav.chatgptclone.repository

import com.sulav.chatgptclone.model.Conversation
import kotlinx.coroutines.flow.Flow

interface ConversationRepository {
    val conversations: Flow<List<Conversation>>
    suspend fun sendMessage(conversationId: Long, text: String)
    suspend fun startNewConversation(firstUserMsg: String): Long
}
