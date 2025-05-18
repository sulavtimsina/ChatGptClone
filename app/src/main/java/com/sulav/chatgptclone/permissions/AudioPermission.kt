package com.sulav.chatgptclone.permissions

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequireRecordAudio(
    onGranted: @Composable () -> Unit,
    onDenied: @Composable () -> Unit
) {
    val state = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    when {
        state.status.isGranted -> onGranted()
        else -> {
            LaunchedEffect(Unit) { state.launchPermissionRequest() }
            onDenied()
        }
    }
}
