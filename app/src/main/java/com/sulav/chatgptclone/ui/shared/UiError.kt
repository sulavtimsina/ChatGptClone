package com.sulav.chatgptclone.ui.shared

sealed interface UiError {
    object NoInternet : UiError
    data class Generic(val msg: String) : UiError
}