package com.sulav.chatgptclone.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sulav.chatgptclone.viewmodel.HistoryViewModel
import androidx.compose.foundation.lazy.items

@Composable
fun HistoryScreen(navController: NavController, vm: HistoryViewModel = hiltViewModel()) {
    val list by vm.conversations.collectAsState(initial = emptyList())
    Scaffold { padding ->
        LazyColumn(contentPadding = padding) {
            items(list) { conv ->
                ListItem(
                    headlineContent = { Text(conv.title) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }
    }
}
