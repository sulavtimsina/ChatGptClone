package com.sulav.chatgptclone.ui.shared

sealed interface UiEvent {
    data class Snackbar(val message: String) : UiEvent
}