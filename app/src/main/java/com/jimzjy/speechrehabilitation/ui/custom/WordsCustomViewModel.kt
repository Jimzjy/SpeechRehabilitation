package com.jimzjy.speechrehabilitation.ui.custom

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jimzjy.speechrehabilitation.R
import com.jimzjy.speechrehabilitation.common.SRApplication
import com.jimzjy.speechrehabilitation.common.widget.PointPosition
import com.jimzjy.speechrehabilitation.room.lattice.Lattice
import com.jimzjy.speechrehabilitation.room.lattice.LatticeRepository
import com.jimzjy.speechrehabilitation.room.word.ROOM_WORDS
import com.jimzjy.speechrehabilitation.room.word.WordRepository
import com.jimzjy.speechrehabilitation.ui.singleWord.DaggerWordComponent
import java.util.*
import javax.inject.Inject

class WordsCustomViewModel : ViewModel() {
    var latticePointList = MutableLiveData<List<PointPosition>>()
    var latticeBackStack = MutableLiveData<LinkedList<List<PointPosition>>>()
    var selectChipText = MutableLiveData<String>()
    val wordList = mutableListOf<String>()

    @Inject
    lateinit var wordRepository: WordRepository
    @Inject
    lateinit var latticeRepository: LatticeRepository

    init {
        DaggerWordComponent.builder()
                .applicationComponent(SRApplication.applicationComponent)
                .build().inject(this)

        wordRepository.loadWord(ROOM_WORDS) {
            wordList.clear()
            it?.let {
                it.words.split('|').forEach {
                    if (it.isNotEmpty()) {
                        it.split(' ').forEach {
                            wordList.add(it)
                        }
                    }
                }
            }
        }
    }

    fun save(word: String, list: List<PointPosition>) {
        val stringBuilder = StringBuilder()
        list.forEach {
            stringBuilder.append("|${it.x} ${it.y}")
        }
        Log.e("", stringBuilder.toString())
        latticeRepository.insertLattice(Lattice(word, stringBuilder.toString())) {
            Toast.makeText(SRApplication.instance, R.string.save_successful, Toast.LENGTH_SHORT).show()
        }
    }

    fun loadLattice(word: String) {
        latticeRepository.loadLattice(word) {
            val list = mutableListOf<PointPosition>()

            it?.let {
                it.lattice.split('|').forEach {
                    if (it.isNotEmpty()) {
                        val p = it.split(' ')
                        list.add(PointPosition(p[0].toInt(), p[1].toInt()))
                    }
                }
            }

            latticePointList.value = list
            latticeBackStack.value = LinkedList()
        }
    }
}
