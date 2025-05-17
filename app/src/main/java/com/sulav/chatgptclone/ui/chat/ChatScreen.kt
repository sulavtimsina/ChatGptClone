package com.sulav.chatgptclone.ui.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sulav.chatgptclone.ui.shared.ChatTopAppBar
import com.sulav.chatgptclone.viewmodel.ChatViewModel

@Composable
fun ChatScreen(
    navController: NavController,
    vm: ChatViewModel = hiltViewModel()
) {
    val text by vm.input.collectAsState()
    val focusRequester = remember { FocusRequester() }

    Scaffold(
        topBar = {
            ChatTopAppBar(
                title = "ChatGPT",
                onMenu = { navController.navigateUp() },
                onNewChat = { /* TODO */ }
            )
        },
        bottomBar = {
            Row(
                Modifier
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { /* TODO open voice */ },
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = null)
                }
                OutlinedTextField(
                    value = text,
                    onValueChange = vm::onInputChange,
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester),
                    placeholder = { Text("Ask anything") }
                )
                IconButton(onClick = vm::onSendClicked) {
                    Icon(Icons.Default.Send, contentDescription = "Send")
                }
            }
        }
    ) { inner ->
        // Placeholder empty list for now
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner),
            contentPadding = PaddingValues(16.dp),
            reverseLayout = true
        ) {
            // TODO items(messages)
        }
    }
}
