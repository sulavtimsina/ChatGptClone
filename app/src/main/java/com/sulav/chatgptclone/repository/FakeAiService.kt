package com.sulav.chatgptclone.repository

import com.sulav.chatgptclone.model.Message
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

class FakeAiService : AiService {

    private val samples = listOf(
        "**Jetpack Compose** makes UI code *concise* and **declarative**.",
        "Use `launch {}` inside a **ViewModelScope** to tie coroutines to UI.",
        "- Room\n- DataStore\n- WorkManager\n\nAll integrate nicely with *Flow*.",
        "Need syntax highlighting?  Use ```kotlin\nval x = 42\n``` in markdown.",
        "### Three Hilt tips\n1. Qualifiers\n2. AssistedInject\n3. EntryPoints",
        "Accessibility is as important as UI polish — *always* add content‑descriptions.",
        "> GraphQL reduces over‑fetching compared to REST APIs.",
        "**WebSocket** enables real‑time chat; **SSE** is lighter for server‑push only.",
        "![compose logo](https://developer.android.com/images/jetpack/compose-tutorial/compose-logo.png)",
        "`StateFlow` is perfect for UI state; prefer it over `LiveData` in new code.",
        "Use detekt & ktlint to keep code quality consistent.",
        "Remember to request **runtime permissions** *at the moment of need*.",
        "WorkManager is the go‑to API for **guaranteed** background work.",
        "Compose Preview speeds up design iterations enormously.",
        "`ProGuard/R8` keep APK size lean—don’t skip config.",
        "TTS can enhance UX for visually impaired users.",
        "Always debounce text inputs before hitting the network.",
        "CDN brings static content physically *closer* to the user.",
        "Accompanist libraries fill the gaps in Compose ecosystem.",
        "Consider paging when lists grow beyond a few hundred rows."
    )

    override fun streamReplyWithHistory(history: List<Message>): Flow<String> =
        sendAndStreamReply()

    private fun sendAndStreamReply(): Flow<String> = flow {
        /* 0️⃣ simulate thinking latency */
        delay(400)

        /* 1️⃣ compose synthetic answer */
        val body = samples.random()

        /* 2️⃣ split into ~5-char chunks to mimic token streaming */
        val tokens = body.chunked(5)

        tokens.forEach { chunk ->
            emit(chunk)
            delay(Random.nextLong(120, 260))   // variable cadence
        }
    }
}
