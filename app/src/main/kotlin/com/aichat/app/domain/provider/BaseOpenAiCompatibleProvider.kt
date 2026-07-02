package com.aichat.app.domain.provider

import com.aichat.app.domain.model.ChatMessage
import com.aichat.app.domain.model.MessageRole
import com.aichat.app.domain.model.ModelInfo
import com.aichat.app.domain.model.ProviderException
import com.aichat.app.domain.model.StreamEvent
import com.aichat.app.domain.model.UsageInfo
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

abstract class BaseOpenAiCompatibleProvider : AiProvider {

    lateinit var httpClient: HttpClient

    protected open fun buildHeaders(apiKey: String): Map<String, String> = mapOf(
        "Authorization" to "Bearer $apiKey",
    )

    protected open fun buildMessagesBody(messages: List<ChatMessage>): JsonArray {
        val messagesArray = JsonArray()
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
        return messagesArray
    }

    protected open fun buildRequestBody(
        messages: List<ChatMessage>,
        model: String,
        temperature: Double,
        maxTokens: Int?,
        systemPrompt: String?,
    ): String {
        val messagesArray = JsonArray()
        systemPrompt?.let {
            messagesArray.add(JsonObject().apply {
                addProperty("role", "system")
                addProperty("content", it)
            })
        }
        messagesArray.addAll(buildMessagesBody(messages))

        return JsonObject().apply {
            addProperty("model", model)
            add("messages", messagesArray)
            addProperty("stream", true)
            if (temperature > 0) addProperty("temperature", temperature)
            maxTokens?.let { addProperty("max_tokens", it) }
        }.toString()
    }

    protected open fun parseStreamLine(line: String): StreamEvent? {
        if (!line.startsWith("data: ")) return null
        val data = line.removePrefix("data: ").trim()
        if (data == "[DONE]") return StreamEvent(isDone = true)

        return try {
            val json = JsonParser.parseString(data).asJsonObject
            val choices = json.getAsJsonArray("choices") ?: return null
            if (choices.size() == 0) return null
            val choice = choices[0].asJsonObject
            val delta = choice.getAsJsonObject("delta")
            val content = delta?.get("content")?.asString ?: ""
            val finishReason = choice.get("finish_reason")?.let {
                if (it.isJsonNull) null else it.asString
            }
            val usage = json.getAsJsonObject("usage")?.let {
                UsageInfo(
                    promptTokens = it.get("prompt_tokens")?.asInt ?: 0,
                    completionTokens = it.get("completion_tokens")?.asInt ?: 0,
                    totalTokens = it.get("total_tokens")?.asInt ?: 0,
                )
            }
            StreamEvent(delta = content, finishReason = finishReason, isDone = finishReason != null, usage = usage)
        } catch (_: Exception) {
            null
        }
    }

    override fun sendMessage(
        messages: List<ChatMessage>,
        model: String,
        apiKey: String,
        baseUrl: String,
        temperature: Double,
        maxTokens: Int?,
    ): Flow<StreamEvent> = flow {
        val url = "${baseUrl.trimEnd('/')}/v1/chat/completions"
        val body = buildRequestBody(messages, model, temperature, maxTokens, null)

        try {
            val response = httpClient.post(url) {
                contentType(ContentType.Application.Json)
                buildHeaders(apiKey).forEach { (k, v) -> header(k, v) }
                setBody(body)
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
                val event = parseStreamLine(line ?: continue)
                if (event != null) {
                    emit(event)
                    if (event.isDone) break
                }
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
            val url = "${baseUrl.trimEnd('/')}/v1/models"
            val response = httpClient.post(url) {
                contentType(ContentType.Application.Json)
                buildHeaders(apiKey).forEach { (k, v) -> header(k, v) }
            }
            if (response.status.isSuccess()) {
                val body = response.bodyAsText()
                val json = JsonParser.parseString(body).asJsonObject
                val dataArray = json.getAsJsonArray("data") ?: return emptyList()
                dataArray.mapNotNull { element ->
                    val obj = element.asJsonObject
                    ModelInfo(
                        id = obj.get("id")?.asString ?: return@mapNotNull null,
                        name = obj.get("owned_by")?.asString ?: obj.get("id")?.asString ?: "",
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
            val url = "${baseUrl.trimEnd('/')}/v1/models"
            val response = httpClient.post(url) {
                contentType(ContentType.Application.Json)
                buildHeaders(apiKey).forEach { (k, v) -> header(k, v) }
            }
            response.status.isSuccess()
        } catch (_: Exception) {
            false
        }
    }
}
