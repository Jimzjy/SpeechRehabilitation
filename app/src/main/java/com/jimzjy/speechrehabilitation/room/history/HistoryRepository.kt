package com.jimzjy.speechrehabilitation.room.history

import android.os.AsyncTask
import androidx.room.Room
import com.jimzjy.speechrehabilitation.common.SRApplication

class HistoryRepository {
    private val mHistoryDao = Room.databaseBuilder(SRApplication.instance,
            HistoryDatabase::class.java, "history_database").build().historyDao()

    fun insertHistory(history: History, resultCallback: (() -> Unit)? = null) {
        InsertHistoryTask(mHistoryDao, resultCallback).execute(history)
    }

    fun loadAllUser(resultCallback: ((List<String>?) -> Unit)? = null) {
        LoadAllUserTask(mHistoryDao, resultCallback).execute()
    }

    fun loadHistory(username: String, resultCallback: ((History?) -> Unit)? = null) {
        LoadHistoryTask(mHistoryDao, resultCallback).execute(username)
    }

    private class InsertHistoryTask(private val mDao: HistoryDao, private val resultCallback: (() -> Unit)?)
        : AsyncTask<History, Unit, Unit>() {

        override fun doInBackground(vararg p0: History?) {
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

    private class LoadAllUserTask(private val mDao: HistoryDao, private val resultCallback: ((List<String>?) -> Unit)?)
        : AsyncTask<Unit, Unit, List<String>>() {

        override fun doInBackground(vararg p0: Unit?): List<String> {
            return mDao.loadAllUser()
        }

        override fun onPostExecute(result: List<String>?) {
            super.onPostExecute(result)
            resultCallback?.invoke(result)
        }
    }

    private class LoadHistoryTask(private val mDao: HistoryDao, private val resultCallback: ((History?) -> Unit)?)
        : AsyncTask<String, Unit, History>() {

        override fun doInBackground(vararg p0: String?): History {
            return mDao.loadHistory(p0[0]!!)
        }

        override fun onPostExecute(result: History?) {
            super.onPostExecute(result)

            resultCallback?.invoke(result)
        }
    }
}