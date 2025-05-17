package com.sulav.chatgptclone.viewmodel

import androidx.lifecycle.ViewModel
import com.sulav.chatgptclone.repository.ConversationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    repo: ConversationRepository
) : ViewModel() {
    val conversations = repo.conversations     // expose Flow to UI
}
