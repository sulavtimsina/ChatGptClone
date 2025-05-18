package com.sulav.chatgptclone.ui.voice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sulav.chatgptclone.permissions.RequireRecordAudio
import com.sulav.chatgptclone.ui.voice.components.VoiceIndicator
import com.sulav.chatgptclone.ui.voice.components.VoiceIndicatorMode
import com.sulav.chatgptclone.viewmodel.VoiceChatViewModel
import kotlinx.coroutines.launch

@Composable
fun VoiceChatScreen(
    navController: NavController,
    vm: VoiceChatViewModel = hiltViewModel()
) {
    val phase by vm.phase.collectAsState()
    val partial by vm.partial.collectAsState()

    RequireRecordAudio(
        onGranted = {
            LaunchedEffect(Unit) { vm.startListening() }
            ScreenContent(navController, partial, phase) { vm.stop() }
        },
        onDenied = { Text("Microphone permission denied") }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScreenContent(
    navController: NavController,
    partial: String,
    phase: VoiceChatViewModel.Phase,
    onStop: () -> Unit
) {
    val isActive = phase == VoiceChatViewModel.Phase.UserSpeaking
    val mode = if (phase == VoiceChatViewModel.Phase.AiSpeaking)
        VoiceIndicatorMode.EQUALIZER else VoiceIndicatorMode.YARN_BALL

    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Voice chat") },
                navigationIcon = {
                    IconButton(onClick = {
                        onStop(); scope.launch { navController.popBackStack() }
                    }) { Icon(Icons.Default.Close, null) }
                }
            )
        }
    ) { p ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(p),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            VoiceIndicator(
                isActive = isActive,
                onModeChange = {},
                mode = mode,
            )
            Spacer(Modifier.height(32.dp))
            Text(
                text = when (phase) {
                    VoiceChatViewModel.Phase.WaitingAi -> "Listening to AI…"
                    VoiceChatViewModel.Phase.AiSpeaking -> "AI is speaking…"
                    else -> partial.ifBlank { "Speak now…" }
                },
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}
