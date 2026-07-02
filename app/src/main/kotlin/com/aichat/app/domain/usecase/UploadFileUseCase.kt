package com.aichat.app.domain.usecase

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Base64
import com.aichat.app.domain.model.FileAttachment
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UploadFileUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    operator fun invoke(uri: Uri): FileAttachment? {
        return try {
            val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"
            val fileName = getFileName(uri) ?: "unknown"
            val isImage = mimeType.startsWith("image/")
            val fileSize = getFileSize(uri)

            val base64Data = if (isImage) {
                encodeImageToBase64(uri)
            } else null

            FileAttachment(
                uri = uri.toString(),
                mimeType = mimeType,
                base64Data = base64Data,
                fileName = fileName,
                isImage = isImage,
                fileSize = fileSize,
            )
        } catch (_: Exception) {
            null
        }
    }

    private fun getFileName(uri: Uri): String? {
        var name: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex >= 0) {
                        name = it.getString(nameIndex)
                    }
                }
            }
        }
        if (name == null) {
            name = uri.path?.substringAfterLast('/')
        }
        return name
    }

    private fun getFileSize(uri: Uri): Long {
        var size: Long = 0
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
                    if (sizeIndex >= 0) {
                        size = it.getLong(sizeIndex)
                    }
                }
            }
        }
        return size
    }

    private fun encodeImageToBase64(uri: Uri): String {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val buffer = ByteArrayOutputStream()
        inputStream?.use { input ->
            val data = ByteArray(1024)
            var bytesRead: Int
            while (input.read(data, 0, data.size).also { bytesRead = it } != -1) {
                buffer.write(data, 0, bytesRead)
            }
        }
        val bytes = buffer.toByteArray()
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
}
