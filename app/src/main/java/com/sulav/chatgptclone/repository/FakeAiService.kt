package com.sulav.chatgptclone.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

class FakeAiService : AiService {

    private val samples = listOf(
        "Jetpack Compose makes UI code concise and declarative.",
        "Kotlin coroutines simplify background work on Android.",
        "Remember to scope ViewModel jobs to clear them on Clear().",
        "Room provides compile-time SQL checks and Flow streams.",
        "Use Hilt for painless dependency injection in large apps.",
        "StateFlow is perfect for UI state; prefer it over LiveData.",
        "Consider paging when lists grow beyond a few hundred rows.",
        "Accessibility is as important as pixel-perfect UI.",
        "GraphQL reduces over-fetching compared to REST APIs.",
        "WebSocket enables true real-time chat updates.",
        "Accompanist libraries fill the gaps in Compose ecosystem.",
        "WorkManager is the go-to API for guaranteed background work.",
        "ProGuard/R8 keep APK size lean—don’t skip config.",
        "Compose Preview speeds up design iterations enormously.",
        "Use detekt & ktlint to keep code quality consistent.",
        "Remember to request runtime permissions at the moment of need.",
        "TTS can enhance UX for visually impaired users.",
        "SSE is lighter than WebSocket when only server pushes events.",
        "Always debounce text inputs before hitting the network.",
        "CDN brings static content physically closer to the user."
    )

    override fun sendAndStreamReply(prompt: String): Flow<String> = flow {
        /* 0️⃣ simulate thinking latency */
        delay(400)

        /* 1️⃣ compose synthetic answer */
        val body   = samples.random()

        /* 2️⃣ split into ~5-char chunks to mimic token streaming */
        val tokens = body.chunked(5)

        tokens.forEach { chunk ->
            emit(chunk)
            delay(Random.nextLong(120, 260))   // variable cadence
        }
    }
}
