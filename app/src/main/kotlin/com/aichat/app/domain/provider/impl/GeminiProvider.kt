package com.aichat.app.domain.provider.impl

import com.aichat.app.domain.model.ChatMessage
import com.aichat.app.domain.model.MessageRole
import com.aichat.app.domain.model.ModelInfo
import com.aichat.app.domain.model.ProviderCapabilities
import com.aichat.app.domain.model.ProviderException
import com.aichat.app.domain.model.ProviderType
import com.aichat.app.domain.model.StreamEvent
import com.aichat.app.domain.provider.AiProvider
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GeminiProvider : AiProvider {
    override val type = ProviderType.GEMINI
    override val displayName = "Google Gemini"
    override val defaultBaseUrl = "https://generativelanguage.googleapis.com"
    override val defaultModel = "gemini-2.0-flash"
    override val capabilities = ProviderCapabilities(
        supportsStreaming = true,
        supportsVision = true,
        supportsSystemPrompt = true,
        supportsFunctionCalling = true,
        supportsTemperature = true,
        supportsImageGeneration = true,
        supportsAudio = true,
        supportsEmbeddings = true,
        maxContextTokens = 1_000_000,
        supportedFileTypes = setOf("image/jpeg", "image/png", "image/webp", "image/gif", "application/pdf"),
        needsApiKey = true,
        supportsModelListing = true,
        supportsTopP = true,
    )

    lateinit var httpClient: HttpClient

    override fun sendMessage(
        messages: List<ChatMessage>,
        model: String,
        apiKey: String,
        baseUrl: String,
        temperature: Double,
        maxTokens: Int?,
    ): Flow<StreamEvent> = flow {
        val url = "${baseUrl.trimEnd('/')}/v1beta/models/$model:streamGenerateContent?alt=sse&key=$apiKey"

        val contents = JsonArray()
        messages.forEach { msg ->
            if (msg.role != MessageRole.SYSTEM) {
                contents.add(JsonObject().apply {
                    addProperty("role", if (msg.role == MessageRole.ASSISTANT) "model" else "user")
                    add("parts", JsonArray().apply {
                        add(JsonObject().apply {
                            addProperty("text", msg.content)
                        })
                    })
                })
            }
        }

        val requestBody = JsonObject().apply {
            add("contents", contents)
        }

        try {
            val response = httpClient.post(url) {
                contentType(ContentType.Application.Json)
                setBody(requestBody.toString())
            }

            if (!response.status.isSuccess()) {
                val errorBody = response.bodyAsText()
                val statusCode = response.status.value
                throw ProviderException(
                    providerType = type,
                    statusCode = statusCode,
                    message = "Request failed with status $statusCode: $errorBody",
                    isRetryable = statusCode == 429 || statusCode == 503 || statusCode >= 500,
                )
            }

            val reader = response.bodyAsText().bufferedReader()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val currentLine = line ?: continue
                if (!currentLine.startsWith("data: ")) continue
                val data = currentLine.removePrefix("data: ").trim()
                if (data == "[DONE]") {
                    emit(StreamEvent(isDone = true))
                    break
                }
                try {
                    val json = JsonParser.parseString(data).asJsonObject
                    val candidates = json.getAsJsonArray("candidates")
                    if (candidates != null && candidates.size() > 0) {
                        val candidate = candidates[0].asJsonObject
                        val content = candidate.getAsJsonObject("content")
                        val parts = content?.getAsJsonArray("parts")
                        if (parts != null && parts.size() > 0) {
                            val text = parts[0].asJsonObject.get("text")?.asString ?: ""
                            if (text.isNotEmpty()) {
                                emit(StreamEvent(delta = text))
                            }
                        }
                        val finishReason = candidate.get("finishReason")?.asString
                        if (finishReason != null) {
                            emit(StreamEvent(isDone = true, finishReason = finishReason))
                            break
                        }
                    }
                } catch (_: Exception) {}
            }
        } catch (e: ProviderException) {
            throw e
        } catch (e: Exception) {
            throw ProviderException(
                providerType = type,
                message = e.message ?: "Unknown error",
                isRetryable = true,
            )
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getModels(apiKey: String, baseUrl: String): List<ModelInfo> {
        return listOf(
            ModelInfo("gemini-2.0-flash", "Gemini 2.0 Flash", type, 1_000_000),
            ModelInfo("gemini-2.0-pro", "Gemini 2.0 Pro", type, 2_000_000),
            ModelInfo("gemini-1.5-pro", "Gemini 1.5 Pro", type, 2_000_000),
            ModelInfo("gemini-1.5-flash", "Gemini 1.5 Flash", type, 1_000_000),
        )
    }

    override suspend fun validateConnection(apiKey: String, baseUrl: String): Boolean {
        return try {
            val url = "${baseUrl.trimEnd('/')}/v1beta/models?key=$apiKey"
            val response = httpClient.post(url) {
                contentType(ContentType.Application.Json)
            }
            response.status.isSuccess()
        } catch (_: Exception) {
            false
        }
    }
}
