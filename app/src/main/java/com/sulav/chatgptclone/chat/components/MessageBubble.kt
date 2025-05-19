package com.sulav.chatgptclone.chat.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sulav.chatgptclone.model.Message
import com.sulav.chatgptclone.ui.theme.Default

@Composable
fun MessageBubble(
    message: Message,
    onCopy: () -> Unit = {},
    onPlay: () -> Unit = {}
) {
    val (bg, fg) = if (message.role == Message.Role.USER)
        MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
    else
        MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurface

    Surface(color = bg, contentColor = fg, shape = MaterialTheme.shapes.medium) {
        Column(Modifier.padding(Default)) {
            if (message.role == Message.Role.ASSISTANT)
                MarkdownMessage(message.content)
            else
                Text(message.content)
            if (message.role == Message.Role.ASSISTANT) {
                MessageActions(onCopy = onCopy, onPlay = onPlay)
            }
        }
    }
}
