package com.sulav.chatgptclone.chat.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sulav.chatgptclone.R
import com.sulav.chatgptclone.chat.components.MessageBubble
import com.sulav.chatgptclone.chat.components.ThinkingShimmer
import com.sulav.chatgptclone.chat.viewmodel.ChatViewModel
import com.sulav.chatgptclone.history.ui.HistoryDrawerScreen
import com.sulav.chatgptclone.model.Message
import com.sulav.chatgptclone.navigation.Destinations
import com.sulav.chatgptclone.ui.shared.ChatTopAppBar
import com.sulav.chatgptclone.ui.shared.UiError
import com.sulav.chatgptclone.ui.theme.ChatGPTCloneTheme
import com.sulav.chatgptclone.ui.theme.Medium
import com.sulav.chatgptclone.ui.theme.OutlinedButtonSize
import com.sulav.chatgptclone.ui.theme.Small
import com.sulav.chatgptclone.ui.theme.zero
import com.sulav.chatgptclone.utils.ClipboardHelper
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    navController: NavController,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val input by viewModel.input.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val thinking by viewModel.isThinking.collectAsState()
    val ctx = LocalContext.current
    val err by viewModel.error.collectAsState()

    val snackBarHostState = remember { SnackbarHostState() }
    LaunchedEffect(err) {
        err?.let {
            val msg = when (it) {
                UiError.NoInternet -> ctx.getString(R.string.no_internet_message)
                is UiError.Generic -> it.msg
            }
            snackBarHostState.showSnackbar(msg)
            viewModel.clearError()
        }
    }

    ChatContent(
        navController = navController,
        input = input,
        messages = messages,
        isThinking = thinking,
        onInputChange = viewModel::onInputChange,
        onSendClicked = viewModel::onSendClicked,
        onVoiceClicked = { navController.navigate(Destinations.VOICE) },
        onCopy = { ClipboardHelper.copy(ctx, it) },
        onPlay = { viewModel.ttsHelper.speak(it) {} },
        snackBarHostState = snackBarHostState
    )
}

@Composable
fun ChatContent(
    navController: NavController,
    input: String,
    messages: List<Message>,
    isThinking: Boolean,
    onInputChange: (String) -> Unit,
    onSendClicked: () -> Unit,
    onVoiceClicked: () -> Unit,
    onCopy: (String) -> Unit,
    onPlay: (String) -> Unit,
    snackBarHostState: SnackbarHostState,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            HistoryDrawerScreen(navController) { convId ->
                scope.launch { drawerState.close() }
                navController.navigate("chat?conversationId=$convId")
            }
        }
    ) {
        Scaffold(
            topBar = {
                ChatTopAppBar(title = stringResource(R.string.top_bar_title)) {
                    scope.launch { drawerState.open() }
                }
            },
            snackbarHost = { SnackbarHost(snackBarHostState) },
            bottomBar = {
                BottomInputBar(
                    text = input,
                    onTextChange = onInputChange,
                    onSend = onSendClicked,
                    onVoiceClicked = onVoiceClicked
                )
            }
        ) { inner ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(inner),
                contentPadding = PaddingValues(Medium),
                reverseLayout = true
            ) {
                if (isThinking) item { ThinkingShimmer() }
                items(messages) { message ->
                    MessageBubble(
                        message = message,
                        onCopy = { onCopy(message.content) },
                        onPlay = { onPlay(message.content) }
                    )
                    Spacer(Modifier.height(Small))
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
            .padding(Small),
        horizontalArrangement = Arrangement.spacedBy(Small)
    ) {
        val scrollState = rememberScrollState()

        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester)
                .verticalScroll(scrollState),
            placeholder = {
                Text(
                    stringResource(
                        R.string.ask_anything
                    )
                )
            },
            maxLines = 10,
            singleLine = false,
        )

        IconButton(onClick = onSend) {
            Icon(
                painterResource(id = R.drawable.outline_send_24),
                contentDescription = stringResource(R.string.send)
            )
        }
        OutlinedButton(
            onClick = { onVoiceClicked() },
            contentPadding = PaddingValues(zero),
            modifier = Modifier.size(OutlinedButtonSize)
        ) {
            Icon(
                painterResource(id = R.drawable.outline_mic_24),
                contentDescription = stringResource(R.string.open_voice_chat)
            )
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