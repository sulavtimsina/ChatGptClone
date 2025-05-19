package com.sulav.chatgptclone.voice.ui

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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sulav.chatgptclone.R
import com.sulav.chatgptclone.permissions.RequireRecordAudio
import com.sulav.chatgptclone.ui.shared.UiEvent
import com.sulav.chatgptclone.ui.theme.ExtraLarge
import com.sulav.chatgptclone.voice.viewmodel.VoiceChatViewModel
import com.sulav.chatgptclone.voice.components.VoiceIndicator
import com.sulav.chatgptclone.voice.components.VoiceIndicatorMode
import kotlinx.coroutines.launch

@Composable
fun VoiceChatScreen(
    navController: NavController,
    viewModel: VoiceChatViewModel = hiltViewModel()
) {
    val snackbarHost = remember { SnackbarHostState() }

    val phase by viewModel.phase.collectAsState()
    val partial by viewModel.partial.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { ev ->
            when (ev) {
                is UiEvent.SnackBar -> snackbarHost.showSnackbar(ev.message)
            }
        }
    }
    RequireRecordAudio(
        onGranted = {
            LaunchedEffect(Unit) { viewModel.startListening() }
            VoiceScreenContent(
                partial = partial,
                phase = phase,
                snackbarHostState = snackbarHost,
                navigateBack = { navController.popBackStack() },
                onStop = { viewModel.stop() }
            )
        },
        onDenied = { Text(stringResource(R.string.microphone_denied)) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VoiceScreenContent(
    partial: String,
    phase: VoiceChatViewModel.Phase,
    snackbarHostState: SnackbarHostState,
    navigateBack: () -> Unit,
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
                        onStop()
                        scope.launch { navigateBack() }
                    }) {
                        Icon(Icons.Default.Close, null)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
            Spacer(Modifier.height(ExtraLarge))
            Text(
                text = when (phase) {
                    VoiceChatViewModel.Phase.WaitingAi -> stringResource(R.string.listening_to_AI)
                    VoiceChatViewModel.Phase.AiSpeaking -> stringResource(R.string.speaking_with_user)
                    else -> partial.ifBlank {
                        stringResource(R.string.speak_now)
                    }
                },
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}
