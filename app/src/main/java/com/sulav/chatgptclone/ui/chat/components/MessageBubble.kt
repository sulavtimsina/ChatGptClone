package com.sulav.chatgptclone.ui.chat.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MessageBubble(
    text: String,
    isUser: Boolean,
    modifier: Modifier = Modifier
) {
    val color =
        if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val contentColor =
        if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    androidx.compose.material3.Surface(
        color = color,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
    ) {
        androidx.compose.material3.Text(
            text = text,
            modifier = Modifier.padding(12.dp)
        )
    }
}
