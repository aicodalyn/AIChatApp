package com.aichat.app.data.remote.streaming

import com.aichat.app.domain.model.StreamEvent

class SseParser {
    private val dataBuffer = StringBuilder()

    fun parseLine(line: String): StreamEvent? {
        if (line.startsWith("data: ")) {
            val data = line.removePrefix("data: ").trim()

            if (data == "[DONE]") {
                return StreamEvent(isDone = true)
            }

            return try {
                val json = com.google.gson.JsonParser.parseString(data).asJsonObject
                val choices = json.getAsJsonArray("choices")
                if (choices != null && choices.size() > 0) {
                    val choice = choices[0].asJsonObject
                    val delta = choice.getAsJsonObject("delta")
                    val content = delta?.get("content")?.asString ?: ""
                    val finishReason = choice.get("finish_reason")?.let {
                        if (it.isJsonNull) null else it.asString
                    }

                    val usage = json.getAsJsonObject("usage")?.let {
                        com.aichat.app.domain.model.UsageInfo(
                            promptTokens = it.get("prompt_tokens")?.asInt ?: 0,
                            completionTokens = it.get("completion_tokens")?.asInt ?: 0,
                            totalTokens = it.get("total_tokens")?.asInt ?: 0,
                        )
                    }

                    StreamEvent(
                        delta = content,
                        finishReason = finishReason,
                        isDone = finishReason != null,
                        usage = usage,
                    )
                } else {
                    null
                }
            } catch (_: Exception) {
                null
            }
        }
        return null
    }
}
