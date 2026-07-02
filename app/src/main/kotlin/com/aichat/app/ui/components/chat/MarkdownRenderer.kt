package com.aichat.app.ui.components.chat

import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.aichat.app.domain.util.MarkdownParser

@Composable
fun MarkdownRenderer(
    markdown: String,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        factory = { ctx ->
            TextView(ctx).apply {
                movementMethod = android.text.method.LinkMovementMethod.getInstance()
            }
        },
        update = { textView ->
            MarkdownParser.renderToTextView(textView, markdown)
        },
        modifier = modifier,
    )
}
