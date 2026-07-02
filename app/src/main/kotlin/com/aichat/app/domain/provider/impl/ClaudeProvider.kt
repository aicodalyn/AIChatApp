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
import io.ktor.client.request.header
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

class ClaudeProvider : AiProvider {
    override val type = ProviderType.CLAUDE
    override val displayName = "Anthropic Claude"
    override val defaultBaseUrl = "https://api.anthropic.com"
    override val defaultModel = "claude-sonnet-4-20250514"
    override val capabilities = ProviderCapabilities(
        supportsStreaming = true,
        supportsVision = true,
        supportsSystemPrompt = true,
        supportsFunctionCalling = true,
        supportsTemperature = true,
        maxContextTokens = 200_000,
        supportedFileTypes = setOf("image/jpeg", "image/png", "image/gif", "image/webp", "application/pdf"),
        needsApiKey = true,
        supportsModelListing = true,
        supportsStopSequences = true,
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
        val url = "${baseUrl.trimEnd('/')}/v1/messages"
        val systemMessages = messages.filter { it.role == MessageRole.SYSTEM }
        val nonSystemMessages = messages.filter { it.role != MessageRole.SYSTEM }

        val messagesArray = JsonArray()
        nonSystemMessages.forEach { msg ->
            messagesArray.add(JsonObject().apply {
                addProperty("role", when (msg.role) {
                    MessageRole.USER -> "user"
                    MessageRole.ASSISTANT -> "assistant"
                    MessageRole.TOOL -> "user"
                    MessageRole.SYSTEM -> "user"
                })
                if (msg.images.isNotEmpty()) {
                    val contentArray = JsonArray()
                    contentArray.add(JsonObject().apply {
                        addProperty("type", "text")
                        addProperty("text", msg.content)
                    })
                    msg.images.forEach { imageUrl ->
                        contentArray.add(JsonObject().apply {
                            addProperty("type", "image")
                            add("source", JsonObject().apply {
                                addProperty("type", "base64")
                                addProperty("media_type", "image/jpeg")
                                addProperty("data", imageUrl.removePrefix("data:image/jpeg;base64,"))
                            })
                        })
                    }
                    add("content", contentArray)
                } else {
                    addProperty("content", msg.content)
                }
            })
        }

        val requestBody = JsonObject().apply {
            addProperty("model", model)
            add("messages", messagesArray)
            addProperty("stream", true)
            addProperty("max_tokens", maxTokens ?: 4096)
            if (temperature > 0) addProperty("temperature", temperature)
            if (systemMessages.isNotEmpty()) {
                addProperty("system", systemMessages.first().content)
            }
        }

        try {
            val response = httpClient.post(url) {
                contentType(ContentType.Application.Json)
                header("x-api-key", apiKey)
                header("anthropic-version", "2023-06-01")
                header("HTTP-Referer", "https://aichat.app")
                setBody(requestBody.toString())
            }

            if (!response.status.isSuccess()) {
                val errorBody = response.bodyAsText()
                val statusCode = response.status.value
                val errorMessage = try {
                    val errorJson = JsonParser.parseString(errorBody).asJsonObject
                    errorJson.getAsJsonObject("error")?.get("message")?.asString
                        ?: "Request failed with status $statusCode"
                } catch (_: Exception) {
                    "Request failed with status $statusCode"
                }
                throw ProviderException(
                    providerType = type,
                    statusCode = statusCode,
                    message = errorMessage,
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
                    val eventType = json.get("type")?.asString
                    when (eventType) {
                        "content_block_delta" -> {
                            val delta = json.getAsJsonObject("delta")
                            val text = delta?.get("text")?.asString ?: ""
                            if (text.isNotEmpty()) {
                                emit(StreamEvent(delta = text))
                            }
                        }
                        "message_stop" -> {
                            emit(StreamEvent(isDone = true))
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
            ModelInfo("claude-sonnet-4-20250514", "Claude Sonnet 4", type, 200_000),
            ModelInfo("claude-3-5-sonnet-20241022", "Claude 3.5 Sonnet", type, 200_000),
            ModelInfo("claude-3-5-haiku-20241022", "Claude 3.5 Haiku", type, 200_000),
            ModelInfo("claude-3-opus-20240229", "Claude 3 Opus", type, 200_000),
        )
    }

    override suspend fun validateConnection(apiKey: String, baseUrl: String): Boolean {
        return try {
            val url = "${baseUrl.trimEnd('/')}/v1/messages"
            val requestBody = JsonObject().apply {
                addProperty("model", defaultModel)
                add("messages", JsonArray().apply {
                    add(JsonObject().apply {
                        addProperty("role", "user")
                        addProperty("content", "Hi")
                    })
                })
                addProperty("max_tokens", 10)
            }
            val response = httpClient.post(url) {
                contentType(ContentType.Application.Json)
                header("x-api-key", apiKey)
                header("anthropic-version", "2023-06-01")
                setBody(requestBody.toString())
            }
            response.status.isSuccess()
        } catch (_: Exception) {
            false
        }
    }
}
