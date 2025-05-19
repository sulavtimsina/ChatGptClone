package com.sulav.chatgptclone.chat.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mukesh.MarkDown
import com.sulav.chatgptclone.ui.theme.ChatGPTCloneTheme

@Composable
fun MarkdownMessage(text: String) {
    MarkDown(
        text = text,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
@Preview
fun MarkdownMessagePreview() {
    ChatGPTCloneTheme()
    { MarkdownMessage("**This is bold text**") }
}
