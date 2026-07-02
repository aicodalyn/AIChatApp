package com.aichat.app.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aichat.app.domain.model.ChatMessage
import com.aichat.app.domain.model.FileAttachment
import com.aichat.app.domain.model.Message
import com.aichat.app.domain.model.MessageRole
import com.aichat.app.domain.model.ProviderProfile
import com.aichat.app.domain.usecase.ManageConversationsUseCase
import com.aichat.app.domain.usecase.ManageProvidersUseCase
import com.aichat.app.domain.usecase.SendMessageUseCase
import com.aichat.app.data.local.security.EncryptedPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val inputText: String = "",
    val attachments: List<FileAttachment> = emptyList(),
    val isStreaming: Boolean = false,
    val streamingText: String = "",
    val activeProvider: ProviderProfile? = null,
    val availableProviders: List<ProviderProfile> = emptyList(),
    val error: String? = null,
    val conversationId: String? = null,
    val conversationTitle: String? = null,
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val sendMessageUseCase: SendMessageUseCase,
    private val manageConversationsUseCase: ManageConversationsUseCase,
    private val manageProvidersUseCase: ManageProvidersUseCase,
    private val encryptedPreferences: EncryptedPreferences,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            manageProvidersUseCase.getEnabled().collect { providers ->
                _uiState.update { state ->
                    state.copy(
                        availableProviders = providers,
                        activeProvider = state.activeProvider ?: providers.firstOrNull(),
                    )
                }
            }
        }
    }

    fun loadConversation(conversationId: String?) {
        if (conversationId == null || conversationId == "new") {
            viewModelScope.launch {
                val conversation = manageConversationsUseCase.create()
                _uiState.update { it.copy(conversationId = conversation.id, conversationTitle = conversation.title) }
            }
            return
        }

        _uiState.update { it.copy(conversationId = conversationId) }

        viewModelScope.launch {
            manageConversationsUseCase.getById(conversationId).collect { conversation ->
                _uiState.update { it.copy(conversationTitle = conversation?.title) }
            }
        }

        viewModelScope.launch {
            manageConversationsUseCase.getMessages(conversationId).collect { messages ->
                _uiState.update { it.copy(messages = messages) }
            }
        }
    }

    fun onInputChange(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun onProviderSelected(provider: ProviderProfile) {
        _uiState.update { it.copy(activeProvider = provider) }
    }

    fun onAttachmentAdded(attachment: FileAttachment) {
        _uiState.update { it.copy(attachments = it.attachments + attachment) }
    }

    fun onAttachmentRemoved(attachment: FileAttachment) {
        _uiState.update { it.copy(attachments = it.attachments - attachment) }
    }

    fun sendMessage() {
        val state = _uiState.value
        if (state.inputText.isBlank() || state.isStreaming) return
        val profile = state.activeProvider ?: return
        val conversationId = state.conversationId ?: return

        viewModelScope.launch {
            val userMessage = Message(
                id = UUID.randomUUID().toString(),
                conversationId = conversationId,
                role = MessageRole.USER,
                content = state.inputText,
                timestamp = System.currentTimeMillis(),
                attachments = state.attachments,
            )

            _uiState.update {
                it.copy(
                    messages = it.messages + userMessage,
                    inputText = "",
                    attachments = emptyList(),
                    isStreaming = true,
                    streamingText = "",
                    error = null,
                )
            }

            if (state.messages.isEmpty()) {
                manageConversationsUseCase.autoRename(conversationId, state.inputText)
            }

            try {
                val chatMessages = state.messages.map { msg ->
                    ChatMessage(
                        role = msg.role,
                        content = msg.content,
                        images = msg.attachments.filter { it.isImage }.map { it.base64Data ?: it.uri },
                    )
                } + ChatMessage(
                    role = MessageRole.USER,
                    content = state.inputText,
                    images = state.attachments.filter { it.isImage }.map { it.base64Data ?: it.uri },
                )

                val apiKey = encryptedPreferences.getApiKey(profile.apiKeyRef)
                if (apiKey == null) {
                    _uiState.update { it.copy(isStreaming = false, error = "API key not found") }
                    return@launch
                }

                val responseBuilder = StringBuilder()
                sendMessageUseCase.invoke(conversationId, state.inputText, profile, state.messages)
                    .collect { event ->
                        if (event.error != null) {
                            _uiState.update { it.copy(isStreaming = false, error = event.error.message) }
                            return@collect
                        }
                        if (event.delta.isNotEmpty()) {
                            responseBuilder.append(event.delta)
                            _uiState.update { it.copy(streamingText = responseBuilder.toString()) }
                        }
                        if (event.isDone) {
                            val assistantMessage = Message(
                                id = UUID.randomUUID().toString(),
                                conversationId = conversationId,
                                role = MessageRole.ASSISTANT,
                                content = responseBuilder.toString(),
                                timestamp = System.currentTimeMillis(),
                                providerUsed = profile.name,
                                modelUsed = profile.model,
                            )
                            _uiState.update {
                                it.copy(
                                    messages = it.messages + assistantMessage,
                                    isStreaming = false,
                                    streamingText = "",
                                )
                            }
                            sendMessageUseCase.saveAssistantMessage(
                                conversationId = conversationId,
                                content = responseBuilder.toString(),
                                providerName = profile.name,
                                model = profile.model,
                            )
                            manageConversationsUseCase.update(
                                com.aichat.app.domain.model.Conversation(
                                    id = conversationId,
                                    title = _uiState.value.conversationTitle ?: "New Chat",
                                    createdAt = System.currentTimeMillis(),
                                    updatedAt = System.currentTimeMillis(),
                                )
                            )
                        }
                    }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isStreaming = false,
                        error = e.message ?: "Unknown error",
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun deleteMessage(messageId: String) {
        _uiState.update {
            it.copy(messages = it.messages.filter { msg -> msg.id != messageId })
        }
    }
}
