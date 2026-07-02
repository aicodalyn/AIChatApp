package com.aichat.app.ui.screens.chat

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.aichat.app.ui.components.chat.MessageBubble
import com.aichat.app.ui.components.chat.StreamingText
import com.aichat.app.ui.components.chat.TypingIndicator
import com.aichat.app.ui.components.input.AttachmentPreview
import com.aichat.app.ui.components.input.ChatInputBar
import com.aichat.app.ui.components.input.FilePickerSheet
import com.aichat.app.ui.components.provider.ProviderCarousel
import com.aichat.app.domain.usecase.UploadFileUseCase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    conversationId: String?,
    onBack: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showFilePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val uploadFileUseCase = remember { UploadFileUseCase(context) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val attachment = uploadFileUseCase(it)
            attachment?.let { viewModel.onAttachmentAdded(it) }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        // Handle camera result
    }

    val fileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            val attachment = uploadFileUseCase(it)
            attachment?.let { viewModel.onAttachmentAdded(it) }
        }
    }

    LaunchedEffect(conversationId) {
        viewModel.loadConversation(conversationId)
    }

    LaunchedEffect(uiState.messages.size, uiState.streamingText) {
        if (uiState.messages.isNotEmpty() || uiState.streamingText.isNotEmpty()) {
            listState.animateScrollToItem(
                if (uiState.streamingText.isNotEmpty()) uiState.messages.size else uiState.messages.size - 1
            )
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.conversationTitle ?: "Chat") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            ProviderCarousel(
                providers = uiState.availableProviders,
                activeProviderId = uiState.activeProvider?.id,
                onProviderClick = { viewModel.onProviderSelected(it) },
                modifier = Modifier.fillMaxWidth(),
            )

            Box(modifier = Modifier.weight(1f)) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    items(uiState.messages, key = { it.id }) { message ->
                        MessageBubble(message = message)
                    }

                    if (uiState.isStreaming && uiState.streamingText.isNotEmpty()) {
                        item {
                            StreamingText(text = uiState.streamingText)
                        }
                    }

                    if (uiState.isStreaming && uiState.streamingText.isEmpty()) {
                        item {
                            TypingIndicator()
                        }
                    }
                }
            }

            AttachmentPreview(
                attachments = uiState.attachments,
                onRemove = { viewModel.onAttachmentRemoved(it) },
            )

            ChatInputBar(
                text = uiState.inputText,
                onTextChange = viewModel::onInputChange,
                onSend = viewModel::sendMessage,
                onAttach = { showFilePicker = true },
                isStreaming = uiState.isStreaming,
            )
        }

        if (showFilePicker) {
            FilePickerSheet(
                onDismiss = { showFilePicker = false },
                onGallery = { galleryLauncher.launch("image/*") },
                onCamera = { /* Handle camera */ },
                onFile = { fileLauncher.launch(arrayOf("application/pdf", "text/plain")) },
            )
        }
    }
}
