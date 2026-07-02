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
import io.ktor.client.request.get
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

class OllamaProvider : AiProvider {
    override val type = ProviderType.OLLAMA
    override val displayName = "Ollama"
    override val defaultBaseUrl = "http://localhost:11434"
    override val defaultModel = "llama3.2"
    override val capabilities = ProviderCapabilities(
        supportsStreaming = true,
        supportsVision = true,
        supportsSystemPrompt = true,
        supportsFunctionCalling = false,
        supportsTemperature = true,
        maxContextTokens = 128_000,
        supportedFileTypes = setOf("image/jpeg", "image/png", "image/webp"),
        needsApiKey = false,
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
        val url = "${baseUrl.trimEnd('/')}/api/chat"

        val messagesArray = JsonArray()
        messages.forEach { msg ->
            messagesArray.add(JsonObject().apply {
                addProperty("role", when (msg.role) {
                    MessageRole.USER -> "user"
                    MessageRole.ASSISTANT -> "assistant"
                    MessageRole.SYSTEM -> "system"
                    MessageRole.TOOL -> "user"
                })
                addProperty("content", msg.content)
            })
        }

        val requestBody = JsonObject().apply {
            addProperty("model", model)
            add("messages", messagesArray)
            addProperty("stream", true)
        }

        try {
            val response = httpClient.post(url) {
                contentType(ContentType.Application.Json)
                setBody(requestBody.toString())
            }

            if (!response.status.isSuccess()) {
                throw ProviderException(
                    providerType = type,
                    statusCode = response.status.value,
                    message = "Request failed with status ${response.status.value}",
                    isRetryable = true,
                )
            }

            val reader = response.bodyAsText().bufferedReader()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                try {
                    val json = JsonParser.parseString(line).asJsonObject
                    val content = json.getAsJsonObject("message")?.get("content")?.asString ?: ""
                    val done = json.get("done")?.asBoolean ?: false
                    if (content.isNotEmpty()) {
                        emit(StreamEvent(delta = content))
                    }
                    if (done) {
                        emit(StreamEvent(isDone = true))
                        break
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
        return try {
            val url = "${baseUrl.trimEnd('/')}/api/tags"
            val response = httpClient.get(url)
            if (response.status.isSuccess()) {
                val body = response.bodyAsText()
                val json = JsonParser.parseString(body).asJsonObject
                val models = json.getAsJsonArray("models") ?: return emptyList()
                models.map { element ->
                    val obj = element.asJsonObject
                    ModelInfo(
                        id = obj.get("name")?.asString ?: "",
                        name = obj.get("name")?.asString ?: "",
                        providerType = type,
                    )
                }
            } else {
                emptyList()
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    override suspend fun validateConnection(apiKey: String, baseUrl: String): Boolean {
        return try {
            val response = httpClient.get("${baseUrl.trimEnd('/')}/api/tags")
            response.status.isSuccess()
        } catch (_: Exception) {
            false
        }
    }
}
