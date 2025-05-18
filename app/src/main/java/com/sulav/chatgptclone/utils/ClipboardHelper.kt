package com.sulav.chatgptclone.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

object ClipboardHelper {
    fun copy(context: Context, text: String) {
        val clip = ClipData.newPlainText("chat reply", text)
        context.getSystemService(ClipboardManager::class.java).setPrimaryClip(clip)
    }
}
