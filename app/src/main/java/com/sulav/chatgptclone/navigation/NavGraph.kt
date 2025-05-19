package com.sulav.chatgptclone.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sulav.chatgptclone.chat.ui.ChatScreen
import com.sulav.chatgptclone.voice.ui.VoiceChatScreen

object Destinations {
    const val CHAT = "chat"
    const val VOICE = "voice"
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
        // Base route without parameters
        composable(route = Destinations.CHAT) {
            ChatScreen(navController)
        }

        // Route with parameter
        composable(
            route = "${Destinations.CHAT}?conversationId={conversationId}",
            arguments = listOf(navArgument("conversationId") {
                nullable = false; type = NavType.LongType; defaultValue = -1L
            })
        ) { backStackEntry ->
            ChatScreen(navController)
        }

        composable(Destinations.VOICE) { VoiceChatScreen(navController) }
    }
}
