package com.aichat.app.domain.model

data class FileAttachment(
    val uri: String,
    val mimeType: String,
    val base64Data: String? = null,
    val fileName: String,
    val isImage: Boolean = false,
    val fileSize: Long = 0,
)
