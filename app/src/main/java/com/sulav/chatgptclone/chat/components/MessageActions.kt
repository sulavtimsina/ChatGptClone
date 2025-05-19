package com.sulav.chatgptclone.chat.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.sulav.chatgptclone.R
import com.sulav.chatgptclone.ui.theme.Smaller

@Composable
fun MessageActions(
    onCopy: () -> Unit,
    onPlay: () -> Unit
) {
    Row(Modifier.padding(top = Smaller)) {
        IconButton(onClick = onCopy) {
            Icon(painterResource(id = R.drawable.outline_content_copy_24), "copy message to clipboard") //TODO: add copy icon here
        }
        IconButton(onClick = onPlay) {
            Icon(Icons.Default.PlayArrow, null) // TODO: add play icon here
        }
    }
}
