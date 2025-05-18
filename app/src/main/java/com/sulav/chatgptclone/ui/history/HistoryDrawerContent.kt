package com.sulav.chatgptclone.ui.history

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sulav.chatgptclone.ui.history.components.ConversationItem
import com.sulav.chatgptclone.ui.history.components.SearchBarWithPencil
import com.sulav.chatgptclone.ui.theme.ChatGPTCloneTheme
import com.sulav.chatgptclone.viewmodel.HistoryViewModel

@Composable
fun HistoryDrawerContent(
    vm: HistoryViewModel = hiltViewModel(),
    onConversationClick: (Long) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    val conversations by vm.conversations.collectAsState(initial = emptyList())
    ModalDrawerSheet()
    {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(WindowInsets.systemBars.asPaddingValues())
//                .padding(16.dp)
//        ) {
            SearchBarWithPencil(query = query, onQueryChange = { query = it }, onPencilClick = {})
            Spacer(Modifier.height(16.dp))
            LazyColumn {
                items(conversations.filter { it.title.contains(query, true) }) { conv ->
                    ConversationItem(title = conv.title) {
                        onConversationClick(conv.id)
                    }
                }
//            }
        }
    }
}

@Preview
@Composable
fun HistoryDrawerContentPreview() {
    ChatGPTCloneTheme {
        HistoryDrawerContent(
            onConversationClick = {}
        )
    }
}