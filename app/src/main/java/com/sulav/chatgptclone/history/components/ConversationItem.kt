package com.sulav.chatgptclone.history.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.sulav.chatgptclone.ui.theme.ChatGPTCloneTheme
import com.sulav.chatgptclone.ui.theme.Medium
import com.sulav.chatgptclone.ui.theme.One
import com.sulav.chatgptclone.ui.theme.Smaller

@Composable
fun ConversationItem(
    title: String,
    onClick: () -> Unit
) {
    Surface(
        tonalElevation = One,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Smaller)
            .clickable { onClick() }
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                .padding(Medium),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Preview
@Composable
fun ConversationItemPreview() {
    ChatGPTCloneTheme {
        ConversationItem(
            title = "Sample Conversation",
            onClick = {}
        )
    }
}