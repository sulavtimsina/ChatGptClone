package com.sulav.chatgptclone.history.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sulav.chatgptclone.data.AppSettings
import com.sulav.chatgptclone.repository.ConversationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    repo: ConversationRepository,
    private val settings: AppSettings
) : ViewModel() {
    val conversations = repo.conversations

    val useRealAi = settings.useRealAi

    fun setUseReal(value: Boolean) {
        viewModelScope.launch {
            settings.setUseRealAi(value)
        }
    }
}