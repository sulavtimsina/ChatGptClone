package com.sulav.chatgptclone.repository

import com.sulav.chatgptclone.data.local.ConversationDao
import com.sulav.chatgptclone.data.local.ConversationEntity
import com.sulav.chatgptclone.data.local.MessageEntity
import com.sulav.chatgptclone.model.Conversation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ConversationRepositoryImpl @Inject constructor(
    private val dao: ConversationDao,
    private val ai: AiService
) : ConversationRepository {

    override val conversations: Flow<List<Conversation>> =
        dao.getAllConversations().map { list ->
            list.map { Conversation(it.id, it.title) }
        }

    override suspend fun sendMessage(conversationId: Long, text: String) {
        dao.insertMessage(
            MessageEntity(conversationId = conversationId, role = "user", content = text)
        )
        // TODO: handle AI reply (ignore for stub)
    }

    override suspend fun startNewConversation(firstUserMsg: String): Long {
        val id = dao.insertConversation(ConversationEntity())
        sendMessage(id, firstUserMsg)
        return id
    }
}
