package com.sulav.chatgptclone.ui.shared

sealed interface UiEvent {
    data class SnackBar(val message: String) : UiEvent
}