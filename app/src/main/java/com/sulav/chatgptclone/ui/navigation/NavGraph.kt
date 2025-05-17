package com.sulav.chatgptclone.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sulav.chatgptclone.ui.chat.ChatScreen
import com.sulav.chatgptclone.ui.history.HistoryScreen
import com.sulav.chatgptclone.ui.voice.VoiceChatScreen

object Destinations {
    const val CHAT = "chat"
    const val HISTORY = "history"
    const val VOICE  = "voice"
}

@Composable
fun ChatNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Destinations.CHAT,
        modifier = modifier
    ) {
        composable(Destinations.CHAT)    { ChatScreen(navController) }
        composable(Destinations.HISTORY) { HistoryScreen(navController) }
        composable(Destinations.VOICE)   { VoiceChatScreen(navController) }
    }
}
