package com.jimzjy.speechrehabilitation.ui.words

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.jimzjy.speechrehabilitation.common.*
import com.jimzjy.speechrehabilitation.room.history.History
import com.jimzjy.speechrehabilitation.room.history.HistoryRepository
import com.jimzjy.speechrehabilitation.room.lattice.Lattice
import com.jimzjy.speechrehabilitation.room.lattice.LatticeRepository
import com.jimzjy.speechrehabilitation.room.word.ROOM_SINGLE_WORD
import com.jimzjy.speechrehabilitation.room.word.ROOM_WORDS
import com.jimzjy.speechrehabilitation.room.word.WordRepository
import com.jimzjy.speechrehabilitation.ui.singleWord.*
import java.util.*
import javax.inject.Inject

class WordsViewModel(application: Application) : AndroidViewModel(application) {
    val count = MutableLiveData<Int>()
    val score = MutableLiveData<Int>()
    val state = MutableLiveData<Int>()
    val lattices = MutableLiveData<List<Lattice>>()
    val ttsActive = MutableLiveData<Boolean>()
    var readyForNext = false
    var currentWord = ""

    private val mWordsList = mutableListOf<String>()
    private val mTTSUtil = TTSUtil()

    @Inject
    lateinit var wordRepository: WordRepository
    @Inject
    lateinit var historyRepository: HistoryRepository
    @Inject
    lateinit var latticeRepository: LatticeRepository

    init {
        DaggerWordComponent.builder()
                .applicationComponent(SRApplication.applicationComponent)
                .build().inject(this)

        count.value = 0
        score.value = 0
        ttsActive.value = false
        lattices.value = emptyList()

        init()
    }

    fun init() {
        state.value = TEST_WAITING
        mTTSUtil.errorCallback = {
            when(it) {
                TTS_INIT_ERROR -> {
                    Log.e("tts", "INIT ERROR")
                    TEST_TTS_INIT_FAILED
                }
                TTS_ERROR -> {
                    Log.e("tts", "ERROR")
                }
                TTS_NOT_SUPPORTED -> {
                    Log.e("tts", "NOT SUPPORTED")
                    TEST_TTS_INIT_FAILED
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
            wordRepository.loadWord(ROOM_WORDS) {
                if (mWordsList.size > 0) mWordsList.clear()

                val list = mutableListOf<String>()
                if (it != null && it.id == ROOM_WORDS) {
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
        speak(currentWord)
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
                    val history = History(it.username, it.singleWord, it.words + "|${Date().time} ${score.value}")
                    historyRepository.insertHistory(history)
                }
                state.value = TEST_FINISHED
            }
            return
        }

        readyForNext = false
        count.value = (count.value ?: 0) + 1

        val list = mutableListOf<Lattice>()
        val words = mutableListOf<String>()
        mWordsList[(count.value ?: 1) - 1].split(" +".toRegex()).forEach {
            if (it.isNotEmpty()) words.add(it)
        }
        latticeRepository.loadLattices(words) {
            it?.let {
                val lattice = it
                words.forEach wordsLoop@{
                    val tWord = it
                    lattice.forEach {
                        if (it.word == tWord) {
                            list.add(it)
                            return@wordsLoop
                        }
                    }
                    list.add(Lattice(tWord, ""))
                }

                lattices.value = list
                currentWord = list[(Math.random() * list.size).toInt()].word
                replay()
            }
        }
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
