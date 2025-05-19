package com.sulav.chatgptclone.repository

import com.sulav.chatgptclone.data.AppSettings
import com.sulav.chatgptclone.model.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.take
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigurableAiService @Inject constructor(
    private val fake: FakeAiService,
    private val real: RealAiService,
    private val settings: AppSettings
) : AiService {

    override fun streamReplyWithHistory(history: List<Message>): Flow<String> =
        settings.useRealAi.take(1).flatMapLatest { useReal ->
            if (useReal) real.streamReplyWithHistory(history)
            else fake.streamReplyWithHistory(history)
        }
}
