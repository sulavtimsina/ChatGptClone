package com.sulav.chatgptclone.voice.viewmodel

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
import com.sulav.chatgptclone.ui.shared.UiEvent
import com.sulav.chatgptclone.utils.NetworkMonitor
import com.sulav.chatgptclone.utils.TextToSpeechHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VoiceChatViewModel @Inject constructor(
    app: Application,
    private val repo: ConversationRepository,
    private val tts: TextToSpeechHelper,
    private val net: NetworkMonitor
) : AndroidViewModel(app) {

    enum class Phase { Idle, Ready, UserSpeaking, WaitingAi, AiSpeaking }

    private val _phase = MutableStateFlow(Phase.Idle)
    val phase: StateFlow<Phase> = _phase

    private val _partial = MutableStateFlow("")
    val partial: StateFlow<String> = _partial

    private val _events = MutableSharedFlow<UiEvent>()
    val events: SharedFlow<UiEvent> = _events

    private val sr: SpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(app)
    private val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
    }

    private var convId: Long? = null

    init {
        sr.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle?) {
                println("!! ready for speech")
                _phase.value = Phase.Ready
            }

            override fun onBeginningOfSpeech() {
                println("!! beginning of speech")
                _phase.value = Phase.UserSpeaking
            }

            override fun onResults(b: Bundle?) {
                val text = b?.getStringArrayList(
                    SpeechRecognizer.RESULTS_RECOGNITION
                )?.firstOrNull().orEmpty()
                if (text.isNotBlank()) {
                    _partial.value = ""
                    processUserUtterance(text)
                } else restartListening()
            }

            override fun onRmsChanged(rms: Float) {
            }

            override fun onEndOfSpeech() {
                println("!! end of speech")
                _phase.value = Phase.WaitingAi
            }

            override fun onPartialResults(b: Bundle?) {
                println("!! partial results")
                _partial.value = b?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull() ?: ""
            }

            /* unused callbacks */
            override fun onError(i: Int) {
                println("!! on error")
                _phase.value = Phase.Idle
            }

            override fun onBufferReceived(p0: ByteArray?) {}
            override fun onEvent(p0: Int, p1: Bundle?) {}
        })
        restartListening()
    }

    private fun processUserUtterance(userText: String) {
        viewModelScope.launch {
            if (!net.isOnline.first()) {
                _events.emit(UiEvent.SnackBar("No internet – try later"))
                _phase.value = Phase.Ready
                restartListening()
                return@launch
            }
            try {
                val id = if (convId == null) {
                    repo.startConversation(userText).also { convId = it }
                } else {
                    repo.send(convId!!, userText)
                    convId!!
                }

                // ── wait for first non-empty assistant message ──
                val aiReply = repo.messages(id)
                    .mapNotNull { list ->
                        list.firstOrNull { it.role == Message.Role.ASSISTANT && it.content.isNotBlank() }
                    }
                    .first()
                    .content

                _phase.value = Phase.AiSpeaking
                tts.speak(aiReply) {
                    _phase.value = Phase.Ready
                    restartListening()
                }
            } catch (e: Exception) {                      // ← handles no-internet or others
                _events.emit(UiEvent.SnackBar("Network error – try again when online"))
                _phase.value = Phase.Ready
                restartListening()
            }
        }
    }

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

    private fun restartListening() {
        if (_phase.value == Phase.AiSpeaking) return           // hang on, TTS still talking
        viewModelScope.launch {
            delay(300)                      // let audio focus settle
            sr.startListening(intent)
        }
    }

    fun stop() {
        sr.stopListening(); _phase.value = Phase.Idle
    }

    override fun onCleared() {
        super.onCleared();
        sr.destroy()
    }
}
