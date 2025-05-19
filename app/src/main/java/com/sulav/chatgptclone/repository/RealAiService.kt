package com.sulav.chatgptclone.repository

import android.R.attr.apiKey
import com.sulav.chatgptclone.BuildConfig
import com.sulav.chatgptclone.model.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.BufferedSource
import java.io.IOException
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealAiService @Inject constructor() : AiService {

    //    TODO: I will provide this OPENAI_API_KEY in a separate file in dropbox, you can either put it in local.properties file or here directly.
    private val apiKey = BuildConfig.OPENAI_API_KEY
    private val client = OkHttpClient()
    private val json    = Json { ignoreUnknownKeys = true }

    override fun streamReplyWithHistory(history: List<Message>): Flow<String> = flow {
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

        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(reqBody)
            .build()

        client.newCall(request).execute().use { resp ->
            if (!resp.isSuccessful) throw IOException("HTTP ${resp.code} ${resp.message}")

            val src: BufferedSource = resp.body!!.source()

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
