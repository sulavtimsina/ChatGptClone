package com.sulav.chatgptclone.ui.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sulav.chatgptclone.model.Message
import com.sulav.chatgptclone.ui.chat.components.MessageBubble
import com.sulav.chatgptclone.ui.chat.components.ThinkingShimmer
import com.sulav.chatgptclone.ui.history.HistoryDrawerContent
import com.sulav.chatgptclone.ui.navigation.Destinations
import com.sulav.chatgptclone.ui.shared.ChatTopAppBar
import com.sulav.chatgptclone.ui.theme.ChatGPTCloneTheme
import com.sulav.chatgptclone.viewmodel.ChatViewModel
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    navController: NavController,
    vm: ChatViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val input by vm.input.collectAsState()
    val msgs by vm.messages.collectAsState()
    val thinking by vm.isThinking.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            HistoryDrawerContent { convId ->
                scope.launch { drawerState.close() }
                navController.navigate("chat?conversationId=$convId") // same route with arg
            }
        }
    ) {
        Scaffold(
            topBar = {
                ChatTopAppBar(
                    title = "ChatGPT",
                    onMenu = { scope.launch { drawerState.open() } },
                    onNewChat = { navController.navigate(Destinations.CHAT) }
                )
            },
            bottomBar = {
                BottomInputBar(
                    text = input,
                    onTextChange = vm::onInputChange,
                    onSend = vm::onSendClicked,
                    onVoiceClicked = { navController.navigate(Destinations.VOICE) }
                )
            }
        ) { inner ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(inner),
                contentPadding = PaddingValues(16.dp),
                reverseLayout = true
            ) {
                if (thinking) item { ThinkingShimmer() }
                items(msgs) { m ->
                    MessageBubble(
                        text = m.content,
                        isUser = m.role == Message.Role.USER,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun BottomInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    onVoiceClicked: () -> Unit = {}
) {
    val focusRequester = remember { FocusRequester() }
    Row(
        Modifier
            .navigationBarsPadding()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester),
            placeholder = { Text("Ask anything") }
        )
        IconButton(onClick = onSend) {
            Icon(Icons.Default.Send, contentDescription = "Send")
        }
        OutlinedButton(
            onClick = { onVoiceClicked() },
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.size(48.dp)
        ) {
            Icon(Icons.Filled.Phone, contentDescription = null)
        }

    }
}

@Preview
@Composable
fun BottomInputBarPreview() {
    ChatGPTCloneTheme {
        BottomInputBar(
            text = "Sample text",
            onTextChange = {},
            onSend = {}
        )
    }
}

@Preview
@Composable
fun ChatScreenPreview() {
    ChatGPTCloneTheme {
        ChatScreen(
            navController = rememberNavController()
        )
    }
}