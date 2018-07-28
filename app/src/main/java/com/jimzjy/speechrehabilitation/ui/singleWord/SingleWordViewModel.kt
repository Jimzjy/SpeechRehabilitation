package com.jimzjy.speechrehabilitation.ui.singleWord

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.jimzjy.speechrehabilitation.common.*
import com.jimzjy.speechrehabilitation.room.history.History
import com.jimzjy.speechrehabilitation.room.history.HistoryRepository
import com.jimzjy.speechrehabilitation.room.word.ROOM_SINGLE_WORD
import com.jimzjy.speechrehabilitation.room.word.WordRepository
import com.jimzjy.speechrehabilitation.ui.custom.WordsCustomViewModel
import com.jimzjy.speechrehabilitation.ui.user.UserViewModel
import com.jimzjy.speechrehabilitation.ui.words.WordsViewModel
import dagger.Component
import java.util.*
import javax.inject.Inject
import javax.inject.Scope

const val TEST_DATA_INIT_FAILED = -2
const val TEST_TTS_INIT_FAILED = -1
const val TEST_WAITING = 0
const val TEST_PREPARED = 1
const val TEST_START = 2
const val TEST_FINISHED = 3

class SingleWordViewModel(application: Application) : AndroidViewModel(application) {
    val count = MutableLiveData<Int>()
    val score = MutableLiveData<Int>()
    val state = MutableLiveData<Int>()
    val words = MutableLiveData<List<String>>()
    val ttsActive = MutableLiveData<Boolean>()
    var readyForNext = false
    var currentWord = ""

    private val mWordsList = mutableListOf<String>()
    private val mTTSUtil = TTSUtil()

    @Inject
    lateinit var wordRepository: WordRepository
    @Inject
    lateinit var historyRepository: HistoryRepository

    init {
        DaggerWordComponent.builder()
                .applicationComponent(SRApplication.applicationComponent)
                .build().inject(this)

        count.value = 0
        score.value = 0
        words.value = emptyList()
        ttsActive.value = false

        init()
    }

    fun init() {
        state.value = TEST_WAITING
        mTTSUtil.errorCallback = {
            when(it) {
                TTS_INIT_ERROR -> {
                    Log.e("tts", "INIT ERROR")
                    state.value = TEST_TTS_INIT_FAILED
                }
                TTS_ERROR -> {
                    Log.e("tts", "ERROR")
                }
                TTS_NOT_SUPPORTED -> {
                    Log.e("tts", "NOT SUPPORTED")
                    state.value = TEST_TTS_INIT_FAILED
                }
            }
        }
        mTTSUtil.startCallback = {
            ttsActive.postValue(true)
        }
        mTTSUtil.doneCallback = {
            ttsActive.postValue(false)
        }
        mTTSUtil.init {
            wordRepository.loadWord(ROOM_SINGLE_WORD) {
                if (mWordsList.size > 0) mWordsList.clear()

                val list = mutableListOf<String>()
                if (it != null && it.id == ROOM_SINGLE_WORD) {
                    it.words.split('|').forEach {
                        if (it.isNotEmpty()) list.add(it)
                    }
                    mWordsList.addAll(list.shuffled().subList(0, 25))

                    state.value = TEST_PREPARED
                } else {
                    state.value = TEST_DATA_INIT_FAILED
                }
            }
        }
    }

    fun speak(text: String) {
        mTTSUtil.speak(text)
    }

    fun replay() {
        speak(currentWord.last().toString())
    }

    fun next() {
        if (count.value == 25) {
            val preference = getApplication<SRApplication>().getSharedPreferences("basic", Context.MODE_PRIVATE)
            historyRepository.loadHistory(preference.getString("username", "")) {
                if (it == null) {
                    state.value = TEST_FINISHED
                    return@loadHistory
                }
                it.let {
                    val history = History(it.username, it.singleWord + "|${Date().time} ${score.value}", it.words)
                    historyRepository.insertHistory(history)
                }
                state.value = TEST_FINISHED
            }
            return
        }

        count.value = (count.value ?: 0) + 1

        val list = mutableListOf<String>()
        mWordsList[(count.value ?: 1) - 1].split(" +".toRegex()).forEach {
            if (it.isNotEmpty()) list.add(it)
        }
        words.value = list
        readyForNext = false

        currentWord = list[(Math.random() * list.size).toInt()]

        replay()
    }

    fun check(word: String): Boolean {
        val result = word == currentWord
        if (result) score.value = (score.value ?: 0) + 4

        return result
    }

    override fun onCleared() {
        super.onCleared()
        mTTSUtil.release()
    }
}

@WordScope
@Component(dependencies = [ApplicationComponent::class])
interface WordComponent {
    fun inject(singleWordViewModel: SingleWordViewModel)
    fun inject(wordsViewModel: WordsViewModel)
    fun inject(userViewModel: UserViewModel)
    fun inject(wordsCustomViewModel: WordsCustomViewModel)
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class WordScope
