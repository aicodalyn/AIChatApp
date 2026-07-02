package com.aichat.app.domain.util

import android.content.Context
import android.text.method.LinkMovementMethod
import android.widget.TextView
import io.noties.markwon.Markwon
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.ImagesPlugin

object MarkdownParser {
    private var markwon: Markwon? = null

    fun getMarkwon(context: Context): Markwon {
        return markwon ?: Markwon.builder(context)
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(TablePlugin.create(context))
            .usePlugin(HtmlPlugin.create())
            .usePlugin(ImagesPlugin.create())
            .build().also { markwon = it }
    }

    fun renderToTextView(textView: TextView, markdown: String) {
        val markwon = getMarkwon(textView.context)
        markwon.setMarkdown(textView, markdown)
        textView.movementMethod = LinkMovementMethod.getInstance()
    }
}
