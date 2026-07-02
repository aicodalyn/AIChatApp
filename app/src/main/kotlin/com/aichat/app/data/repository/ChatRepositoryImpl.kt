package com.aichat.app.data.repository

import com.aichat.app.data.remote.streaming.SseParser
import com.aichat.app.domain.model.ChatMessage
import com.aichat.app.domain.model.MessageRole
import com.aichat.app.domain.model.ModelInfo
import com.aichat.app.domain.model.ProviderException
import com.aichat.app.domain.model.ProviderProfile
import com.aichat.app.domain.model.StreamEvent
import com.aichat.app.domain.model.UsageInfo
import com.aichat.app.domain.provider.ProviderRegistry
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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val providerRegistry: ProviderRegistry,
) : com.aichat.app.domain.repository.ChatRepository {

    override fun sendMessage(
        messages: List<ChatMessage>,
        profile: ProviderProfile,
        apiKey: String,
    ): Flow<StreamEvent> = flow {
        val provider = providerRegistry.get(profile.type)
        val sseParser = SseParser()

        val messagesArray = JsonArray()
        profile.parameters.systemPrompt?.let { systemPrompt ->
            messagesArray.add(JsonObject().apply {
                addProperty("role", "system")
                addProperty("content", systemPrompt)
            })
        }

        messages.forEach { msg ->
            messagesArray.add(JsonObject().apply {
                addProperty("role", when (msg.role) {
                    MessageRole.USER -> "user"
                    MessageRole.ASSISTANT -> "assistant"
                    MessageRole.SYSTEM -> "system"
                    MessageRole.TOOL -> "tool"
                })
                if (msg.images.isNotEmpty()) {
                    val contentArray = JsonArray()
                    contentArray.add(JsonObject().apply {
                        addProperty("type", "text")
                        addProperty("text", msg.content)
                    })
                    msg.images.forEach { imageUrl ->
                        contentArray.add(JsonObject().apply {
                            addProperty("type", "image_url")
                            add("image_url", JsonObject().apply {
                                addProperty("url", imageUrl)
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
            addProperty("model", profile.model)
            add("messages", messagesArray)
            addProperty("stream", true)
            if (profile.parameters.temperature > 0) {
                addProperty("temperature", profile.parameters.temperature)
            }
            profile.parameters.topP?.let { addProperty("top_p", it) }
            profile.parameters.maxTokens?.let { addProperty("max_tokens", it) }
            profile.parameters.frequencyPenalty?.let { addProperty("frequency_penalty", it) }
            profile.parameters.presencePenalty?.let { addProperty("presence_penalty", it) }
        }

        val baseUrl = profile.baseUrl.trimEnd('/')
        val url = "$baseUrl/v1/chat/completions"

        try {
            val response = httpClient.post(url) {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $apiKey")
                header("HTTP-Referer", "https://aichat.app")
                header("X-Title", "AI Chat")
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
                    providerType = profile.type,
                    statusCode = statusCode,
                    message = errorMessage,
                    isRetryable = statusCode == 429 || statusCode == 503 || statusCode >= 500,
                )
            }

            val reader = response.bodyAsText().bufferedReader()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val currentLine = line ?: continue
                val event = sseParser.parseLine(currentLine)
                if (event != null) {
                    emit(event)
                    if (event.isDone) break
                }
            }
        } catch (e: ProviderException) {
            throw e
        } catch (e: Exception) {
            throw ProviderException(
                providerType = profile.type,
                message = e.message ?: "Unknown error",
                isRetryable = true,
            )
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getModels(profile: ProviderProfile, apiKey: String): List<ModelInfo> {
        return try {
            val provider = providerRegistry.get(profile.type)
            provider.getModels(apiKey, profile.baseUrl)
        } catch (_: Exception) {
            emptyList()
        }
    }

    override suspend fun validateConnection(profile: ProviderProfile, apiKey: String): Boolean {
        return try {
            val provider = providerRegistry.get(profile.type)
            provider.validateConnection(apiKey, profile.baseUrl)
        } catch (_: Exception) {
            false
        }
    }
}
