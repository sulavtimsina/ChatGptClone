package com.sulav.chatgptclone.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {

    /* ---------- conversation ---------- */
    @Insert suspend fun insertConversation(entity: ConversationEntity): Long

    @Query("UPDATE ConversationEntity SET title = :title WHERE id = :id")
    suspend fun updateTitle(id: Long, title: String)

    @Query("SELECT * FROM ConversationEntity ORDER BY id DESC")
    fun getAllConversations(): Flow<List<ConversationEntity>>

    /* ---------- messages -------------- */
    @Insert suspend fun insertMessage(entity: MessageEntity): Long

    @Update suspend fun updateMessage(entity: MessageEntity)

    @Query(
        "SELECT * FROM MessageEntity " +
                "WHERE conversationId = :conversationId " +
                "ORDER BY id DESC"
    )
    fun getMessages(conversationId: Long): Flow<List<MessageEntity>>
}
