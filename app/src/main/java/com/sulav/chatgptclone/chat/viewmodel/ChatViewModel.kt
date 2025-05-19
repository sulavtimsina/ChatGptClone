package com.sulav.chatgptclone.chat.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sulav.chatgptclone.model.Message
import com.sulav.chatgptclone.repository.ConversationRepository
import com.sulav.chatgptclone.utils.TextToSpeechHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repo: ConversationRepository,
    savedState: SavedStateHandle
) : ViewModel() {
    @Inject
    lateinit var ttsHelper: TextToSpeechHelper

    /* ------------ reactive conversation id ------------- */
    private val _convId =
        MutableStateFlow<Long?>(savedState.get<Long>("conversationId")?.takeIf { it >= 0 })
    private val convId: StateFlow<Long?> get() = _convId

    /* ------------ input field ------------- */
    private val _input = MutableStateFlow("")
    val input: StateFlow<String> = _input
    fun onInputChange(t: String) { _input.value = t }

    /* ------------ thinking flag ------------- */
    private val _isThinking = MutableStateFlow(false)
    val isThinking: StateFlow<Boolean> = _isThinking

    /* ------------ live messages ------------- */
    val messages: StateFlow<List<Message>> = convId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList())
            else repo.messages(id)
        }
        .stateIn(viewModelScope, SharingStarted.Companion.WhileSubscribed(5_000), emptyList())

    /* ------------ send ------------- */
    fun onSendClicked() {
        val text = _input.value.trim()
        if (text.isEmpty()) return
        _input.value = ""
        _isThinking.value = true

        viewModelScope.launch {
            val id = convId.value?.takeIf { it >= 0 }
            if (id == null) {
                /* first message ever -> create conversation THEN start streaming */
                val newId = repo.startConversation(text)
                _convId.value = newId              // 🔑  start collection immediately
            } else {
                repo.send(id, text)
            }
            _isThinking.value = false
        }
    }
}