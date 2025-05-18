package com.sulav.chatgptclone.viewmodel

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sulav.chatgptclone.model.Message
import com.sulav.chatgptclone.repository.ConversationRepository
import com.sulav.chatgptclone.utils.TextToSpeechHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VoiceChatViewModel @Inject constructor(
    app: Application,
    private val repo: ConversationRepository,
    private val tts: TextToSpeechHelper
) : AndroidViewModel(app) {

    enum class Phase { Idle, Listening, WaitingAi, Speaking }

    private val _phase = MutableStateFlow(Phase.Idle)
    val phase: StateFlow<Phase> = _phase

    private val _partial = MutableStateFlow("")
    val partial: StateFlow<String> = _partial

    private val sr: SpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(app)

    private var currentConv: Long? = null

    init {
        sr.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle?) {
                _phase.value = Phase.Listening
            }

            override fun onResults(bundle: Bundle?) {
                val result = bundle?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull() ?: return
                sendToAi(result)
            }

            override fun onRmsChanged(rms: Float) {
                rmsAmplitude.value = rms
            }

            override fun onPartialResults(b: Bundle?) {
                _partial.value = b?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull() ?: ""
            }

            /* unused callbacks */
            override fun onError(i: Int) {}
            override fun onBeginningOfSpeech() {
                println("!! beginning of speech")
            }
            override fun onEndOfSpeech() {}
            override fun onBufferReceived(p0: ByteArray?) {}
            override fun onEvent(p0: Int, p1: Bundle?) {}
        })
    }

    val rmsAmplitude = MutableStateFlow(0f)        // 0-10 range

    fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
        sr.startListening(intent)
    }

    fun stop() {
        sr.stopListening(); _phase.value = Phase.Idle
    }

    private fun sendToAi(userText: String) {
        _partial.value = ""
        _phase.value = Phase.WaitingAi
        viewModelScope.launch {
            val id = currentConv ?: repo.startConversation(userText).also { currentConv = it }
            repo.send(id, userText)           // streams AI reply into DB
            _phase.value = Phase.Speaking
        }
        viewModelScope.launch {
            val id = currentConv ?: repo.startConversation(userText).also { currentConv = it }
            /*  stream reply so we can TTS it  */
            repo.send(id, userText)          // already streams into DB
            val aiReply = repo.messages(id).first()         // newest assistant text
                .first { it.role == Message.Role.ASSISTANT }.content
            _phase.value = Phase.Speaking
            tts.speak(aiReply) { _phase.value = Phase.Idle }   // callback when done
        }
    }

    override fun onCleared() {
        super.onCleared(); sr.destroy()
    }
}
