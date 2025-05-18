package com.sulav.chatgptclone.repository

import com.sulav.chatgptclone.BuildConfig
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.*
import okhttp3.*
import okio.BufferedSource
import com.sulav.chatgptclone.model.Message
import java.io.IOException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.text.Charsets.UTF_8
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

@Singleton
class RealAiService @Inject constructor() : AiService {

    private val apiKey = BuildConfig.OPENAI_API_KEY
    private val client = OkHttpClient()
    private val json    = Json { ignoreUnknownKeys = true }

    override fun streamReplyWithHistory(history: List<Message>): Flow<String> = flow {
        /* 1️⃣ Build OpenAI chat request JSON */
        val messagesJson = buildJsonArray {
            history.forEach {
                addJsonObject {
                    put("role", it.role.name.lowercase(Locale.US))   // "user" | "assistant"
                    put("content", it.content)
                }
            }
        }

        val bodyJson = buildJsonObject {
            put("model", "gpt-4.1-nano")
            put("stream", true)
            put("max_tokens", 150)
            put("temperature", 0.7)
            put("messages", messagesJson)
        }

        val reqBody = json.encodeToString(JsonObject.serializer(), bodyJson)
            .toRequestBody("application/json".toMediaType())

        /* 2️⃣ Build and execute HTTP request */
        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(reqBody)
            .build()

        client.newCall(request).execute().use { resp ->
            if (!resp.isSuccessful) throw IOException("HTTP ${resp.code} ${resp.message}")

            val src: BufferedSource = resp.body!!.source()

            /* 3️⃣ Read SSE lines -> emit content tokens */
            while (!src.exhausted()) {
                val line = src.readUtf8Line() ?: continue
                if (!line.startsWith("data: ")) continue           // skip keep-alive
                val payload = line.removePrefix("data: ").trim()
                if (payload == "[DONE]") break

                val obj = json.parseToJsonElement(payload)
                if (obj is JsonObject) {
                    obj["choices"]?.jsonArray
                        ?.firstOrNull()?.jsonObject
                        ?.get("delta")?.jsonObject
                        ?.get("content")?.jsonPrimitive?.contentOrNull
                        ?.let { if (it.isNotBlank()) emit(it) }
                }
            }
        }
    }
}
