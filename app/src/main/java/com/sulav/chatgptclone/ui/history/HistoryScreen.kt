package com.sulav.chatgptclone.ui.history

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sulav.chatgptclone.ui.history.components.ConversationItem
import com.sulav.chatgptclone.ui.history.components.SearchBar
import com.sulav.chatgptclone.ui.shared.ChatTopAppBar
import com.sulav.chatgptclone.viewmodel.HistoryViewModel

@Composable
fun HistoryScreen(
    navController: NavController,
    vm: HistoryViewModel = hiltViewModel()
) {
    var query by remember { mutableStateOf("") }
    val conversations by vm.conversations.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            ChatTopAppBar(
                title = "Chats",
                onMenu = { navController.navigateUp() },
                onNewChat = { /* TODO */ }
            )
        }
    ) { inner ->
        Column(
            Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            SearchBar(query = query, onQueryChange = { query = it })
            Spacer(Modifier.height(16.dp))
            LazyColumn {
                items(conversations.filter { it.title.contains(query, true) }) { conv ->
                    ConversationItem(title = conv.title) {
                        // TODO open conversation
                    }
                }
            }
        }
    }
}
