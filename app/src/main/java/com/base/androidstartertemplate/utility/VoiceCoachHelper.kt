package com.base.androidstartertemplate.utility

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

object VoiceCoachHelper {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    fun init(context: Context) {
        if (tts != null) return
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("VoiceCoachHelper", "US English Language not supported on this device.")
                } else {
                    isInitialized = true
                    tts?.setPitch(1.0f)
                    tts?.setSpeechRate(1.05f)
                }
            } else {
                Log.e("VoiceCoachHelper", "TextToSpeech initialization failed with status $status")
            }
        }
    }

    fun speak(text: String, isVoiceCoachEnabled: Boolean = true) {
        if (!isVoiceCoachEnabled || !isInitialized || text.isBlank()) return
        try {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "VoiceCoach_${System.currentTimeMillis()}")
        } catch (e: Exception) {
            Log.e("VoiceCoachHelper", "Error speaking text: $text", e)
        }
    }

    fun speakCountdown(count: Int, isVoiceCoachEnabled: Boolean = true) {
        if (!isVoiceCoachEnabled) return
        val text = when (count) {
            3 -> "Three"
            2 -> "Two"
            1 -> "One"
            0 -> "Go!"
            else -> ""
        }
        if (text.isNotEmpty()) {
            speak(text, isVoiceCoachEnabled)
        }
    }

    fun speakGetReady(exerciseName: String, isVoiceCoachEnabled: Boolean = true) {
        speak("Get ready for $exerciseName", isVoiceCoachEnabled)
    }

    fun speakExerciseStart(exerciseName: String, target: String, isVoiceCoachEnabled: Boolean = true) {
        speak("Begin $exerciseName! $target", isVoiceCoachEnabled)
    }

    fun speakRestStart(seconds: Int, isVoiceCoachEnabled: Boolean = true) {
        speak("Rest time! $seconds seconds", isVoiceCoachEnabled)
    }

    fun speakWorkoutComplete(isVoiceCoachEnabled: Boolean = true) {
        speak("Workout Complete! Great job crushing your workout!", isVoiceCoachEnabled)
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
            tts = null
            isInitialized = false
        } catch (e: Exception) {
            Log.e("VoiceCoachHelper", "Error shutting down TTS", e)
        }
    }
}
