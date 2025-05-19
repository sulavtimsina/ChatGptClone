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
import androidx.compose.ui.res.stringResource
import com.sulav.chatgptclone.R
import com.sulav.chatgptclone.ui.theme.Smaller

@Composable
fun MessageActions(
    onCopy: () -> Unit,
    onPlay: () -> Unit
) {
    // TODO: Add pause button to pause playback
    Row(Modifier.padding(top = Smaller)) {
        IconButton(onClick = onCopy) {
            Icon(
                painter = painterResource(id = R.drawable.outline_content_copy_24),
                contentDescription = stringResource(R.string.a11y_copy_message_to_clipboard)
            )
        }
        IconButton(onClick = onPlay) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = stringResource(R.string.a11y_play_message)
            )
        }
    }
}
