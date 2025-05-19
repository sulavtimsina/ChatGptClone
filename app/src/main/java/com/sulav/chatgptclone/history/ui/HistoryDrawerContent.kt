package com.sulav.chatgptclone.history.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sulav.chatgptclone.R
import com.sulav.chatgptclone.history.components.ConversationItem
import com.sulav.chatgptclone.history.components.SearchBarWithPencil
import com.sulav.chatgptclone.history.viewmodel.HistoryViewModel
import com.sulav.chatgptclone.model.Conversation
import com.sulav.chatgptclone.navigation.Destinations
import com.sulav.chatgptclone.ui.theme.Medium
import com.sulav.chatgptclone.ui.theme.Small

@Composable
fun HistoryDrawerScreen(
    navController: NavController,
    viewModel: HistoryViewModel = hiltViewModel(),
    onConversationClick: (Long) -> Unit
) {
    val conversations by viewModel.conversations.collectAsState(emptyList())
    val useReal by viewModel.useRealAi.collectAsState(false)

    HistoryDrawerContent(
        conversations = conversations,
        useReal = useReal,
        onUseRealChange = viewModel::setUseReal,
        navigateToChat = {
            navController.navigate(
                Destinations.CHAT
            )
        },
        onConversationClick = onConversationClick
    )
}

@Composable
fun HistoryDrawerContent(
    conversations: List<Conversation>,
    useReal: Boolean,
    onUseRealChange: (Boolean) -> Unit,
    navigateToChat: () -> Unit,
    onConversationClick: (Long) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    ModalDrawerSheet()
    {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Small)
        ) {
            Text(stringResource(R.string.use_live_llm))
            Spacer(Modifier.weight(1f))
            Switch(
                checked = useReal,
                onCheckedChange = onUseRealChange
            )
        }

        SearchBarWithPencil(
            query = query,
            onQueryChange = { query = it },
            onPencilClick = {
                navigateToChat()
            })
        Spacer(Modifier.height(Medium))
        LazyColumn(modifier = Modifier.fillMaxHeight()) {
            items(conversations.filter { it.title.contains(query, true) }) { conv ->
                ConversationItem(title = conv.title) {
                    onConversationClick(conv.id)
                }
            }
        }
    }
}
