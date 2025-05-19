package com.sulav.chatgptclone.ui.history

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sulav.chatgptclone.ui.history.HistoryDrawerContent
import com.sulav.chatgptclone.ui.history.components.ConversationItem
import com.sulav.chatgptclone.ui.history.components.SearchBarWithPencil
import com.sulav.chatgptclone.ui.navigation.Destinations
import com.sulav.chatgptclone.ui.theme.ChatGPTCloneTheme
import com.sulav.chatgptclone.viewmodel.HistoryViewModel
import com.sulav.chatgptclone.viewmodel.SettingsViewModel

//@Composable
//fun HistoryDrawerScreen(
//    vm: HistoryViewModel = hiltViewModel(),
//    settingsViewModel: SettingsViewModel = hiltViewModel(),
//    onConversationClick: (Long) -> Unit
//) {
//    HistoryDrawerContent(vm, settingsViewModel, onConversationClick)
//}

@Composable
fun HistoryDrawerContent(
    navController: NavController,
    vm: HistoryViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    onConversationClick: (Long) -> Unit,
) {
    val useReal by settingsViewModel.settings.useRealAi.collectAsState(initial = false)
    var query by remember { mutableStateOf("") }
    val conversations by vm.conversations.collectAsState(initial = emptyList())
    ModalDrawerSheet()
    {
        SearchBarWithPencil(query = query, onQueryChange = { query = it }, onPencilClick = {
            navController.navigate(
                Destinations.CHAT
            )
        })
        Spacer(Modifier.height(16.dp))
        LazyColumn(modifier = Modifier.fillMaxHeight()) {
            items(conversations.filter { it.title.contains(query, true) }) { conv ->
                ConversationItem(title = conv.title) {
                    onConversationClick(conv.id)
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text("Use Live LLM")
            Spacer(Modifier.weight(1f))
            Switch(
                checked = useReal,
                onCheckedChange = { settingsViewModel.setUseReal(it) }
            )
        }
    }
}
//
//@Preview
//@Composable
//fun HistoryDrawerContentPreview() {
//    ChatGPTCloneTheme {
//        val navController = rememberNavController()
//        HistoryDrawerContent(navController = navController)(
//            onConversationClick = {}
//        )
//    }
//}