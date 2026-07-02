package com.aichat.app.ui.screens.providers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aichat.app.domain.model.ProviderProfile
import com.aichat.app.domain.model.ProviderType
import com.aichat.app.domain.usecase.ManageProvidersUseCase
import com.aichat.app.data.local.security.EncryptedPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class ProvidersUiState(
    val profiles: List<ProviderProfile> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

@HiltViewModel
class ProvidersViewModel @Inject constructor(
    private val manageProvidersUseCase: ManageProvidersUseCase,
    private val encryptedPreferences: EncryptedPreferences,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProvidersUiState())
    val uiState: StateFlow<ProvidersUiState> = _uiState.asStateFlow()

    init {
        loadProfiles()
    }

    private fun loadProfiles() {
        viewModelScope.launch {
            manageProvidersUseCase.getAll().collect { profiles ->
                _uiState.update { it.copy(profiles = profiles, isLoading = false) }
            }
        }
    }

    fun addProfile(
        name: String,
        type: ProviderType,
        apiKey: String,
        baseUrl: String,
        model: String,
    ) {
        viewModelScope.launch {
            val id = UUID.randomUUID().toString()
            val apiKeyRef = "apikey_$id"
            encryptedPreferences.storeApiKey(apiKeyRef, apiKey)

            val profile = ProviderProfile(
                id = id,
                name = name,
                type = type,
                apiKeyRef = apiKeyRef,
                baseUrl = baseUrl,
                model = model,
                ordinal = _uiState.value.profiles.size,
            )
            manageProvidersUseCase.create(profile)
        }
    }

    fun updateProfile(profile: ProviderProfile, newApiKey: String?) {
        viewModelScope.launch {
            if (newApiKey != null) {
                encryptedPreferences.storeApiKey(profile.apiKeyRef, newApiKey)
            }
            manageProvidersUseCase.update(profile)
        }
    }

    fun deleteProfile(profile: ProviderProfile) {
        viewModelScope.launch {
            encryptedPreferences.deleteApiKey(profile.apiKeyRef)
            manageProvidersUseCase.delete(profile.id)
        }
    }

    fun toggleEnabled(id: String) {
        viewModelScope.launch {
            manageProvidersUseCase.toggleEnabled(id)
        }
    }
}
