package com.sulav.chatgptclone.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TextToSpeechHelper @Inject constructor(ctx: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech = TextToSpeech(ctx.applicationContext, this)
    private var onDone: (() -> Unit)? = null

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.US
        }
    }
    fun speak(text: String, onDone: () -> Unit) {
        onDone.also { this.onDone = it }
        tts.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
            override fun onDone(uttId: String?) { onDone() }
            override fun onError(uttId: String?) { onDone() }
            override fun onStart(uttId: String?) {}
        })
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utt1")
    }
}
