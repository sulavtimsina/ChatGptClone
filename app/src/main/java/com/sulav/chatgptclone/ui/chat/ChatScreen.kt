package com.sulav.chatgptclone.ui.chat

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.sulav.chatgptclone.viewmodel.ChatViewModel

@Composable
fun ChatScreen(navController: NavController, vm: ChatViewModel = hiltViewModel()) {
    val text by vm.input.collectAsState()
    Scaffold(
        bottomBar = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = text,
                    onValueChange = vm::onInputChange,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 56.dp, max = 200.dp),
                    placeholder = { Text("Ask something…") }
                )
                IconButton(onClick = vm::onSendClicked) {
                    Icon(Icons.Default.Send, contentDescription = "Send")
                }
            }
        }
    ) { /* Empty content for now */ }
}
