package com.jimzjy.speechrehabilitation.common

import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.*

const val TTS_NOT_SUPPORTED = -1
const val TTS_INIT_ERROR = -2
const val TTS_ERROR = -3

class TTSUtil : UtteranceProgressListener() {
    var errorCallback: ((error: Int) -> Unit)? = null
    var startCallback: (() -> Unit)? = null
    var doneCallback: (() -> Unit)? = null

    private var mTextToSpeech: TextToSpeech? = null

    fun init(initCallback: () -> Unit) {
        mTextToSpeech = TextToSpeech(SRApplication.instance) {
            if (it == TextToSpeech.SUCCESS) {
                val code = mTextToSpeech?.setLanguage(Locale.CHINA)
                if (code == TextToSpeech.LANG_MISSING_DATA || code == TextToSpeech.LANG_NOT_SUPPORTED) {
                    errorCallback?.invoke(TTS_NOT_SUPPORTED)
                    return@TextToSpeech
                }
                mTextToSpeech?.setSpeechRate(0.8f)
                mTextToSpeech?.setOnUtteranceProgressListener(this@TTSUtil)

                initCallback.invoke()
            } else {
                errorCallback?.invoke(TTS_INIT_ERROR)
            }
        }
    }

    fun speak(text: String) {
        mTextToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, text)
    }

    fun release() {
        mTextToSpeech?.shutdown()
    }

    override fun onDone(p0: String?) {
        doneCallback?.invoke()
    }

    override fun onError(p0: String?) {
        Log.e("tts", "onError")
    }

    override fun onStart(p0: String?) {
        startCallback?.invoke()
    }
}