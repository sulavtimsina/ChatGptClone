package com.sulav.chatgptclone.repository

import com.sulav.chatgptclone.data.local.ConversationDao
import com.sulav.chatgptclone.data.local.ConversationEntity
import com.sulav.chatgptclone.data.local.MessageEntity
import com.sulav.chatgptclone.model.Conversation
import com.sulav.chatgptclone.model.Message
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val MAX_CTX = 10

@Singleton
class ConversationRepositoryImpl @Inject constructor(
    private val dao: ConversationDao,
    private val ai: AiService,
    private val io: CoroutineDispatcher = Dispatchers.IO
) : ConversationRepository {

    /* ------------ mapping helpers ------------- */
    private fun ConversationEntity.toDomain() = Conversation(id, title)
    private fun MessageEntity.toDomain() =
        Message(id, Message.Role.valueOf(role.uppercase()), content)

    /* ------------ public ------------- */
    override val conversations: Flow<List<Conversation>> =
        dao.getAllConversations().map { list -> list.map { it.toDomain() } }

    override fun messages(conversationId: Long): Flow<List<Message>> =
        dao.getMessages(conversationId).map { list -> list.map { it.toDomain() } }

    override suspend fun startConversation(firstUserMsg: String): Long = withContext(io) {
        val convId = dao.insertConversation(ConversationEntity(title = "New Chat"))
        send(convId, firstUserMsg)
        convId
    }

    override suspend fun send(conversationId: Long, userText: String) = withContext(io) {
        /* 1. insert USER message */
        dao.insertMessage(
            MessageEntity(
                conversationId = conversationId,
                role = "user",
                content = userText
            )
        )

        /* 2. insert placeholder assistant row -> returns id */
        val assistantId = dao.insertMessage(
            MessageEntity(
                conversationId = conversationId,
                role = "assistant",
                content = ""         // will fill while streaming
            )
        )

        /* 3.  ---- gather context (oldest → newest) ---- */
        val history = dao.getMessages(conversationId)
            .map { list -> list.map { it.toDomain() }.reversed() } // oldest first
            .first()
            .takeLast(MAX_CTX)

        /* 4. stream tokens */
        val builder = StringBuilder()
        try {
            ai.streamReplyWithHistory(history).collect { token ->
                builder.append(token)
                dao.updateMessage(
                    MessageEntity(
                        id = assistantId,
                        conversationId = conversationId,
                        role = "assistant",
                        content = builder.toString()
                    )
                )
            }
        } catch (e: IOException) {
            dao.updateMessage(
                MessageEntity(
                    id = assistantId,
                    conversationId = conversationId,
                    role = "assistant",
                    content = "⚠️ Network error, retry later."
                )
            )
            throw e
        }

        /* 4. if first assistant answer, auto-title conversation */
        if (builder.isNotBlank()) {
            val firstWords = builder.lines().first().take(40)
            dao.updateTitle(conversationId, firstWords)
        }
    }
}
