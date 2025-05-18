package com.sulav.chatgptclone.di

import android.content.Context
import androidx.room.Room
import com.sulav.chatgptclone.data.AppSettings
import com.sulav.chatgptclone.data.local.AppDatabase
import com.sulav.chatgptclone.data.local.ConversationDao
import com.sulav.chatgptclone.repository.AiService
import com.sulav.chatgptclone.repository.ConfigurableAiService
import com.sulav.chatgptclone.repository.ConversationRepository
import com.sulav.chatgptclone.repository.ConversationRepositoryImpl
import com.sulav.chatgptclone.repository.FakeAiService
import com.sulav.chatgptclone.repository.RealAiService
import com.sulav.chatgptclone.utils.TextToSpeechHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "chat.db").build()

    @Provides
    fun provideConversationDao(db: AppDatabase): ConversationDao = db.conversationDao()

    @Provides
    @Singleton
    fun provideFake(): FakeAiService = FakeAiService()
    @Provides
    @Singleton
    fun provideReal(): RealAiService = RealAiService()

    @Provides
    @Singleton
    fun provideAiService(
        fake: FakeAiService,
        real: RealAiService,
        settings: AppSettings
    ): AiService = ConfigurableAiService(fake, real, settings)

    @Provides
    @Singleton
    fun provideRepository(
        dao: ConversationDao,
        ai: AiService
    ): ConversationRepository = ConversationRepositoryImpl(dao, ai)

    @Provides
    @Singleton
    fun provideTts(@ApplicationContext ctx: Context) = TextToSpeechHelper(ctx)

}