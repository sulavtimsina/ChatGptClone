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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sulav.chatgptclone.R

@Composable
fun MessageActions(
    onCopy: () -> Unit,
    onPlay: () -> Unit
) {
    Row(Modifier.padding(top = 4.dp)) {
        IconButton(onClick = onCopy) {
            Icon(painterResource(id = R.drawable.outline_content_copy_24), "copy message to clipboard") //TODO: add copy icon here
        }
        IconButton(onClick = onPlay) {
            Icon(Icons.Default.PlayArrow, null) // TODO: add play icon here
        }
    }
}
