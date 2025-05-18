package com.sulav.chatgptclone.repository

import com.sulav.chatgptclone.BuildConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okio.BufferedSource
import java.io.IOException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealAiService @Inject constructor() : AiService {

    private val apiKey = BuildConfig.OPENAI_API_KEY
    private val client = OkHttpClient()

    override fun sendAndStreamReply(prompt: String): Flow<String> = flow {
        try {
            val reqBody =
                Json.encodeToString(
                    buildJsonObject {
                        put("model", "gpt-4.1-nano")
                        putJsonArray("messages") {
                            addJsonObject {
                                put("role", "user"); put("content", prompt)
                            }
                        }
                        put("stream", true)
                        put("max_tokens", 100)  // Limit response length to 100 tokens
                        put("temperature", 0.3) // Lower temperature for more concise responses
                    }
                ).toRequestBody("application/json".toMediaType())

            val req = Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer $apiKey")
                .post(reqBody)
                .build()

            client.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) {
                    throw IOException("Unexpected response ${resp.code}: ${resp.message}")
                }

                val src: BufferedSource = resp.body?.source()
                    ?: throw IOException("Response body is null")

                while (!src.exhausted()) {
                    val line = src.readUtf8Line() ?: continue
                    if (line.startsWith("data: ")) {
                        val json = line.removePrefix("data: ").trim()
                        if (json == "[DONE]") break

                        try {
                            val jsonElement = Json.parseToJsonElement(json)
                            val delta = jsonElement.jsonObject["choices"]
                                ?.jsonArray?.get(0)  // Fixed: access first element of array
                                ?.jsonObject?.get("delta")
                                ?.jsonObject?.get("content")
                                ?.jsonPrimitive?.contentOrNull

                            if (!delta.isNullOrBlank()) emit(delta)
                        } catch (e: Exception) {
                            // Skip malformed JSON
                        }
                    }
                }
            }
        } catch (e: Exception) {
            // You might want to emit an error message or rethrow
            throw e
        }
    }
}
