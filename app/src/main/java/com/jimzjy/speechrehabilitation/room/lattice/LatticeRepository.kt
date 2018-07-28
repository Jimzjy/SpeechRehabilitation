package com.jimzjy.speechrehabilitation.room.lattice

import android.os.AsyncTask
import androidx.room.Room
import com.jimzjy.speechrehabilitation.common.SRApplication

const val REPOSITORY_INSERT = 0
const val REPOSITORY_DELETE = 1

class LatticeRepository {
    private val mLatticeDao = Room.databaseBuilder(SRApplication.instance, LatticeDatabase::class.java,
            "lattice_database").build().latticeDao()

    fun insertLattice(lattice: Lattice, resultCallback: (() -> Unit)? = null) {
        PrimaryLatticeTask(mLatticeDao, REPOSITORY_INSERT, resultCallback).execute(lattice)
    }

    fun loadLattice(word: String, resultCallback: ((Lattice?) -> Unit)? = null) {
        LoadLatticeTask(mLatticeDao, resultCallback).execute(word)
    }

    fun loadLattices(words: List<String>, resultCallback: ((List<Lattice>?) -> Unit)? = null) {
        LoadLatticesTask(mLatticeDao, resultCallback).execute(words)
    }

    private class PrimaryLatticeTask(private val mDao: LatticeDao, private val type: Int, private val resultCallback: (() -> Unit)?)
        : AsyncTask<Lattice, Unit, Unit>() {

        override fun doInBackground(vararg p0: Lattice?) {
            p0.forEach {
                it?.let {
                    when(type) {
                        REPOSITORY_INSERT -> {
                            mDao.insert(it)
                        }
                        REPOSITORY_DELETE -> {
                            mDao.delete(it)
                        }
                    }
                }
            }
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            resultCallback?.invoke()
        }
    }

    private class LoadLatticeTask(private val mDao: LatticeDao, private val resultCallback: ((Lattice?) -> Unit)?)
        : AsyncTask<String, Unit, Lattice>() {

        override fun doInBackground(vararg p0: String?): Lattice {
            return mDao.loadLattice(p0[0] ?: "")
        }

        override fun onPostExecute(result: Lattice?) {
            super.onPostExecute(result)
            resultCallback?.invoke(result)
        }
    }

    private class LoadLatticesTask(private val mDao: LatticeDao, private val resultCallback: ((List<Lattice>?) -> Unit)?)
        : AsyncTask<List<String>, Unit, List<Lattice>>() {

        override fun doInBackground(vararg p0: List<String>?): List<Lattice> {
            return mDao.loadLattices(p0[0] ?: emptyList())
        }

        override fun onPostExecute(result: List<Lattice>?) {
            super.onPostExecute(result)
            resultCallback?.invoke(result)
        }
    }
}