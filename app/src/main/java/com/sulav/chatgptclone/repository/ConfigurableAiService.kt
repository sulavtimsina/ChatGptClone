package com.sulav.chatgptclone.repository

import com.sulav.chatgptclone.data.AppSettings
import kotlinx.coroutines.flow.*

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigurableAiService @Inject constructor(
    private val fake: FakeAiService,
    private val real: RealAiService,
    private val settings: AppSettings
) : AiService {

    override fun sendAndStreamReply(prompt: String): Flow<String> =
        settings.useRealAi.take(1).flatMapLatest { useReal ->
            if (useReal) real.sendAndStreamReply(prompt)
            else fake.sendAndStreamReply(prompt)
        }
}
