package com.aichat.app.ui.screens.conversations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aichat.app.domain.model.Conversation
import com.aichat.app.domain.usecase.ManageConversationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ConversationsUiState(
    val conversations: List<Conversation> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
)

@HiltViewModel
class ConversationsViewModel @Inject constructor(
    private val manageConversationsUseCase: ManageConversationsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConversationsUiState())
    val uiState: StateFlow<ConversationsUiState> = _uiState.asStateFlow()

    init {
        loadConversations()
    }

    private fun loadConversations() {
        viewModelScope.launch {
            manageConversationsUseCase.getAll().collect { conversations ->
                _uiState.update {
                    it.copy(conversations = conversations, isLoading = false)
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        viewModelScope.launch {
            if (query.isBlank()) {
                manageConversationsUseCase.getAll().collect { conversations ->
                    _uiState.update { it.copy(conversations = conversations) }
                }
            } else {
                manageConversationsUseCase.search(query).collect { conversations ->
                    _uiState.update { it.copy(conversations = conversations) }
                }
            }
        }
    }

    fun deleteConversation(id: String) {
        viewModelScope.launch {
            manageConversationsUseCase.delete(id)
        }
    }

    fun togglePin(id: String) {
        viewModelScope.launch {
            manageConversationsUseCase.togglePin(id)
        }
    }

    fun renameConversation(id: String, newTitle: String) {
        viewModelScope.launch {
            manageConversationsUseCase.rename(id, newTitle)
        }
    }
}
