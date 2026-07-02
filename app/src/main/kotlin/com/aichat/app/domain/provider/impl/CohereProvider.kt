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

class CohereProvider : AiProvider {
    override val type = ProviderType.COHERE
    override val displayName = "Cohere"
    override val defaultBaseUrl = "https://api.cohere.com"
    override val defaultModel = "command-r-plus"
    override val capabilities = ProviderCapabilities(
        supportsStreaming = true,
        supportsVision = false,
        supportsSystemPrompt = true,
        supportsFunctionCalling = true,
        supportsTemperature = true,
        maxContextTokens = 128_000,
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
        val url = "${baseUrl.trimEnd('/')}/v1/chat"
        val chatHistory = JsonArray()
        val preamble = StringBuilder()

        messages.forEach { msg ->
            when (msg.role) {
                MessageRole.SYSTEM -> preamble.appendLine(msg.content)
                MessageRole.USER -> chatHistory.add(JsonObject().apply {
                    addProperty("role", "USER")
                    addProperty("message", msg.content)
                })
                MessageRole.ASSISTANT -> chatHistory.add(JsonObject().apply {
                    addProperty("role", "CHATBOT")
                    addProperty("message", msg.content)
                })
                else -> {}
            }
        }

        val lastUserMessage = messages.lastOrNull { it.role == MessageRole.USER }?.content ?: ""

        val requestBody = JsonObject().apply {
            addProperty("model", model)
            if (preamble.isNotEmpty()) addProperty("preamble", preamble.toString())
            add("chat_history", chatHistory)
            addProperty("message", lastUserMessage)
            addProperty("stream", true)
            if (temperature > 0) addProperty("temperature", temperature)
            maxTokens?.let { addProperty("max_tokens", it) }
        }

        try {
            val response = httpClient.post(url) {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $apiKey")
                header("Accept", "text/event-stream")
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
                    val eventType = json.get("type")?.asString
                    when (eventType) {
                        "content-delta" -> {
                            val delta = json.getAsJsonObject("delta")
                            val text = delta?.get("message")?.asString ?: ""
                            if (text.isNotEmpty()) emit(StreamEvent(delta = text))
                        }
                        "message-stop" -> {
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
            ModelInfo("command-r-plus", "Command R+", type, 128_000),
            ModelInfo("command-r", "Command R", type, 128_000),
            ModelInfo("command-light", "Command Light", type, 4096),
        )
    }

    override suspend fun validateConnection(apiKey: String, baseUrl: String): Boolean {
        return try {
            val response = httpClient.post("${baseUrl.trimEnd('/')}/v1/chat") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $apiKey")
                setBody("""{"model":"$defaultModel","message":"Hi","stream":false}""")
            }
            response.status.isSuccess()
        } catch (_: Exception) {
            false
        }
    }
}
