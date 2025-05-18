package com.sulav.chatgptclone.ui.chat.components

import androidx.compose.foundation.background
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.halilibo.richtext.ui.RichText

@Composable
fun MarkdownMessage(text: String) {
    RichText(modifier = Modifier.background(color = Color.White)) {
        Text(text)
    }
}
