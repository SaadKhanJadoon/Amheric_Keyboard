package com.amherickeyboard.voicetyping.translator.dictionary

import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
import androidx.annotation.RequiresApi
import java.util.*

class Constants : TextToSpeech.OnInitListener {

    private var textForSpeak: String = ""

    companion object {
        const val url = "https://api.dictionaryapi.dev/api/v2/entries/"
        const val baseurl = "https://api.dictionaryapi.dev/api/v2/entries/en/"
        const val dictionaryLink = "https://googledictionaryapi.eu-gb.mybluemix.net/"
        var shouldShowAd: Boolean = true
        var inputLanguageCode: String = ""
        var outputLanguageCode: String = "eng"
        var outputCodeForSpeak: String = "eng"
        lateinit var outputLanguageName: String
        const val SpeechRequestCode = 15
        var pitch = 1f
        var speed = .7f
        private lateinit var tts: TextToSpeech
    }

    fun ttsInitialization(context: Context) {
        tts = TextToSpeech(context, this@Constants)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun forSpeak(textForSpeak: String, outputCode: String, context: Context) {
        this.textForSpeak = textForSpeak
        tts = TextToSpeech(
            context,
            this@Constants
        )
        outputCodeForSpeak = outputCode
        tts.language = Locale(outputCode)
        if (!tts.isSpeaking) {
            tts.setPitch(pitch)
            tts.setSpeechRate(speed)
            tts.speak(
                textForSpeak,
                TextToSpeech.QUEUE_ADD,
                null, null
            )
        } else {
            tts.stop()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onInit(status: Int) {
        if (status != TextToSpeech.ERROR) {
            tts.language = Locale(outputCodeForSpeak)
            tts.speak(
                textForSpeak,
                TextToSpeech.QUEUE_ADD,
                null, null
            )
        }
    }

    fun getTts(): TextToSpeech {
        return tts
    }
}