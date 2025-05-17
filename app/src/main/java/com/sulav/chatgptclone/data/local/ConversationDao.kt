package com.sulav.chatgptclone.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {
    @Insert suspend fun insertConversation(entity: ConversationEntity): Long
    @Insert suspend fun insertMessage(entity: MessageEntity)

    @Query("SELECT * FROM ConversationEntity")
    fun getAllConversations(): Flow<List<ConversationEntity>>
}
