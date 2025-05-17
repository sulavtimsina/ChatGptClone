package com.sulav.chatgptclone.repository

import com.sulav.chatgptclone.model.Conversation
import com.sulav.chatgptclone.model.Message
import kotlinx.coroutines.flow.Flow

interface ConversationRepository {
    val conversations: Flow<List<Conversation>>
    fun messages(conversationId: Long): Flow<List<Message>>

    /** create ⟶ send first user msg ⟶ stream AI  */
    suspend fun startConversation(firstUserMsg: String): Long

    /** append user msg to existing conv & stream AI */
    suspend fun send(conversationId: Long, userText: String)
}
