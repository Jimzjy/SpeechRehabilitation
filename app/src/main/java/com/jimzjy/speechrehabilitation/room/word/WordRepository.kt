package com.jimzjy.speechrehabilitation.room.word

import android.os.AsyncTask
import androidx.room.Room
import com.jimzjy.speechrehabilitation.common.SRApplication

class WordRepository {
    private val mWordDao = Room.databaseBuilder(SRApplication.instance,
            WordDatabase::class.java, "single_word_database").build().wordDao()

    fun insertWord(word: Word, resultCallback: (() -> Unit)? = null) {
        InsertWordTask(mWordDao, resultCallback).execute(word)
    }

    fun loadWord(type: Int, resultCallback: ((word: Word?) -> Unit)? = null) {
        LoadWordTask(mWordDao, resultCallback).execute(type)
    }

    private class InsertWordTask(private val mDao: WordDao, private val resultCallback: (() -> Unit)?)
        : AsyncTask<Word, Unit, Unit>() {

        override fun doInBackground(vararg p0: Word?) {
            p0.forEach {
                it?.let {
                    mDao.insert(it)
                }
            }
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            resultCallback?.invoke()
        }
    }

    private class LoadWordTask(private val mDao: WordDao, private val resultCallback: ((word: Word?) -> Unit)?)
        : AsyncTask<Int, Unit, Word>() {

        override fun doInBackground(vararg p0: Int?): Word {
            return mDao.loadWord(p0[0] ?: 0)
        }

        override fun onPostExecute(result: Word?) {
            super.onPostExecute(result)
            resultCallback?.invoke(result)
        }
    }
}