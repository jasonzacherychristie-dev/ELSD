package com.elsd.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import java.util.Locale

/**
 * Voice-only command path (M1 uses platform SpeechRecognizer).
 * Vosk offline swap-in later without changing CommandGrammar.
 */
class VoiceRouter(
    private val context: Context,
    private val onCommand: (Command) -> Unit,
    private val onPartial: (String) -> Unit = {},
) {
    private var recognizer: SpeechRecognizer? = null
    private val main = Handler(Looper.getMainLooper())
    private var running = false
    private val restart = Runnable { if (running) startListening() }

    fun start() {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            Log.w(TAG, "SpeechRecognizer unavailable")
            return
        }
        running = true
        if (recognizer == null) {
            recognizer = SpeechRecognizer.createSpeechRecognizer(context).also {
                it.setRecognitionListener(listener)
            }
        }
        startListening()
    }

    fun stop() {
        running = false
        main.removeCallbacks(restart)
        try {
            recognizer?.cancel()
            recognizer?.destroy()
        } catch (_: Exception) {
        }
        recognizer = null
    }

    private fun startListening() {
        val r = recognizer ?: return
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM,
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
        try {
            r.startListening(intent)
        } catch (e: Exception) {
            Log.e(TAG, "startListening failed", e)
            main.postDelayed(restart, 1500)
        }
    }

    private val listener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {}
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {}

        override fun onError(error: Int) {
            if (running) main.postDelayed(restart, 400)
        }

        override fun onResults(results: Bundle?) {
            val texts = results
                ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                .orEmpty()
            val best = texts.firstOrNull().orEmpty()
            if (best.isNotBlank()) {
                val cmd = CommandGrammar.parse(best)
                Log.i(TAG, "heard=\"$best\" -> $cmd")
                onCommand(cmd)
            }
            if (running) main.postDelayed(restart, 250)
        }

        override fun onPartialResults(partialResults: Bundle?) {
            val texts = partialResults
                ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                .orEmpty()
            texts.firstOrNull()?.let { onPartial(it) }
        }

        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    companion object {
        private const val TAG = "ELSD.Voice"
    }
}
