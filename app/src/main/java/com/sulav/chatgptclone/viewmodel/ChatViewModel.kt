package com.sulav.chatgptclone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sulav.chatgptclone.repository.ConversationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repo: ConversationRepository
) : ViewModel() {

    private val _input = MutableStateFlow("")
    val input: StateFlow<String> = _input

    fun onInputChange(text: String) { _input.value = text }

    fun onSendClicked() {
        val text = _input.value.trim()
        if (text.isEmpty()) return
        viewModelScope.launch { repo.startNewConversation(text) }
        _input.value = ""
    }
}
