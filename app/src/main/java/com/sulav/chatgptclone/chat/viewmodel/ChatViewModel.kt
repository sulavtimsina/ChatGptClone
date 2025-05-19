package com.sulav.chatgptclone.chat.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sulav.chatgptclone.model.Message
import com.sulav.chatgptclone.repository.ConversationRepository
import com.sulav.chatgptclone.ui.shared.UiError
import com.sulav.chatgptclone.utils.NetworkMonitor
import com.sulav.chatgptclone.utils.TextToSpeechHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okio.IOException
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repo: ConversationRepository,
    private val net: NetworkMonitor,
    savedState: SavedStateHandle
) : ViewModel() {
    @Inject
    lateinit var ttsHelper: TextToSpeechHelper

    private val _convId =
        MutableStateFlow<Long?>(savedState.get<Long>("conversationId")?.takeIf { it >= 0 })
    private val convId: StateFlow<Long?> get() = _convId

    private val _input = MutableStateFlow("")
    val input: StateFlow<String> = _input
    fun onInputChange(t: String) {
        _input.value = t
    }

    private val _isThinking = MutableStateFlow(false)
    val isThinking: StateFlow<Boolean> = _isThinking

    private val _error = MutableStateFlow<UiError?>(null)
    val error: StateFlow<UiError?> = _error

    val messages: StateFlow<List<Message>> = convId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList())
            else repo.messages(id)
        }
        .stateIn(viewModelScope, SharingStarted.Companion.WhileSubscribed(5_000), emptyList())

    fun onSendClicked() {
        val text = _input.value.trim()
        if (text.isEmpty()) return
        _input.value = ""
        _isThinking.value = true

        viewModelScope.launch {
            if (!net.isOnline.first()) {
                _error.value = UiError.NoInternet
                return@launch
            }
            val id = convId.value?.takeIf { it >= 0 }
            try {
                if (id == null) {
                    val newId = repo.startConversation(text)
                    _convId.value = newId
                } else {
                    repo.send(id, text)
                }
            } catch (e: IOException) {
                _error.value = UiError.NoInternet
            } catch (e: Exception) {
                _error.value = UiError.Generic(e.message ?: "Unknown error")
            } finally {
                _isThinking.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}