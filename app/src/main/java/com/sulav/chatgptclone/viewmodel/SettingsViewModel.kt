package com.sulav.chatgptclone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sulav.chatgptclone.data.AppSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(val settings: AppSettings) : ViewModel() {
    fun setUseReal(value: Boolean) = viewModelScope.launch {
        settings.setUseRealAi(value)
    }
}
