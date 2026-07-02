package com.aichat.app.data.remote.streaming

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class StreamingState(
    val isStreaming: Boolean = false,
    val partialText: String = "",
    val isComplete: Boolean = false,
    val error: String? = null,
)

class StreamingStateManager {
    private val _state = MutableStateFlow(StreamingState())
    val state: StateFlow<StreamingState> = _state.asStateFlow()

    fun startStreaming() {
        _state.update {
            StreamingState(isStreaming = true, partialText = "", isComplete = false, error = null)
        }
    }

    fun appendText(text: String) {
        _state.update { it.copy(partialText = it.partialText + text) }
    }

    fun complete() {
        _state.update { it.copy(isStreaming = false, isComplete = true) }
    }

    fun error(message: String) {
        _state.update { it.copy(isStreaming = false, error = message) }
    }

    fun reset() {
        _state.value = StreamingState()
    }
}
