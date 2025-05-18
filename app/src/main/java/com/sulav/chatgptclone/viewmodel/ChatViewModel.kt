package com.sulav.chatgptclone.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sulav.chatgptclone.model.Message
import com.sulav.chatgptclone.repository.ConversationRepository
import com.sulav.chatgptclone.utils.TextToSpeechHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repo: ConversationRepository,
    savedState: SavedStateHandle
) : ViewModel() {
    @Inject lateinit var ttsHelper: TextToSpeechHelper
    /* ---------- nav arg ---------- */
    private val conversationId: Long? =
        savedState.get<Long>("conversationId")

    /* ---------- input ---------- */
    private val _input = MutableStateFlow("")
    val input: StateFlow<String> = _input
    fun onInputChange(t: String) {
        _input.value = t
    }

    /* ---------- thinking flag ---------- */
    private val _isThinking = MutableStateFlow(false)
    val isThinking: StateFlow<Boolean> = _isThinking

    /* ---------- messages ---------- */
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    init {
        conversationId?.let { id ->
            viewModelScope.launch {
                repo.messages(id).collect { _messages.value = it }
            }
        }
    }

    fun onSendClicked() {
        val text = _input.value.trim()
        if (text.isEmpty()) return
        _input.value = ""
        _isThinking.value = true

        viewModelScope.launch {
            if (conversationId == null) {
                repo.startConversation(text)
            } else {
                repo.send(conversationId, text)
            }
            _isThinking.value = false
        }
    }
}
