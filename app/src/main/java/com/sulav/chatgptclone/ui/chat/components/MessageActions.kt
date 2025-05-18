package com.sulav.chatgptclone.ui.chat.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MessageActions(
    onCopy: () -> Unit,
    onPlay: () -> Unit
) {
    Row(Modifier.padding(top = 4.dp)) {
        IconButton(onClick = onCopy) {
            Icon(Icons.Default.Email, null) //TODO: add copy icon here
        }
        IconButton(onClick = onPlay) {
            Icon(Icons.Default.PlayArrow, null) // TODO: add play icon here
        }
    }
}
