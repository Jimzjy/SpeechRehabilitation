package com.jimzjy.speechrehabilitation.ui.user

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jimzjy.speechrehabilitation.R
import com.jimzjy.speechrehabilitation.common.SRApplication
import com.jimzjy.speechrehabilitation.room.history.HistoryRepository
import com.jimzjy.speechrehabilitation.ui.singleWord.DaggerWordComponent
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class UserViewModel : ViewModel() {
    // 0: singleWord, 1: words
    val singleLineList = mutableListOf(floatArrayOf())
    val singleXTextList = MutableLiveData<List<String>>()
    val wordsLineList = mutableListOf(floatArrayOf())
    val wordsXTextList = MutableLiveData<List<String>>()

    @Inject
    lateinit var historyRepository: HistoryRepository

    private val mSingleWordHistoryDataList = mutableListOf<HistoryData>()
    private val mWordsHistoryDataList = mutableListOf<HistoryData>()

    init {
        DaggerWordComponent.builder()
                .applicationComponent(SRApplication.applicationComponent)
                .build().inject(this)

        val username = SRApplication.instance.getSharedPreferences("basic", Context.MODE_PRIVATE)
                .getString("username", "")
        historyRepository.loadHistory(username) {
            it?.let {
                it.singleWord.split('|').forEach {
                    if (it.isNotEmpty()) {
                        val tmp = it.split(' ')
                        mSingleWordHistoryDataList.add(HistoryData(tmp[0].toLong(), tmp[1].toInt()))
                    }
                }
                it.words.split('|').forEach {
                    if (it.isNotEmpty()) {
                        val tmp = it.split(' ')
                        mWordsHistoryDataList.add(HistoryData(tmp[0].toLong(), tmp[1].toInt()))
                    }
                }
            }

            weekData()
        }
    }

    fun weekData() {
        val singleArray = floatArrayOf(-1f, -1f, -1f, -1f, -1f, -1f, -1f)
        val wordsArray = floatArrayOf(-1f, -1f, -1f, -1f, -1f, -1f, -1f)

        val cal = Calendar.getInstance()
        if (mSingleWordHistoryDataList.size > 0) {
            cal.timeInMillis = mSingleWordHistoryDataList.last().time
        }
        val singleDayStart = getStartDayTime(cal, 6)
        if (mWordsHistoryDataList.size > 0) {
            cal.timeInMillis = mWordsHistoryDataList.last().time
        }
        val wordsDayStart = getStartDayTime(cal, 6)

        val step = 24 * 3600000L
        setData(mSingleWordHistoryDataList, singleArray, singleDayStart, step)
        setData(mWordsHistoryDataList, wordsArray, wordsDayStart, step)

        singleLineList[0] = singleArray
        wordsLineList[0] = wordsArray

        val singleTextList = mutableListOf<String>()
        val wordsTextList = mutableListOf<String>()
        for (i in 0 until 7) {
            singleTextList.add(SimpleDateFormat("MM.dd", Locale.CHINA).format(Date(singleDayStart + step * i)))
            wordsTextList.add(SimpleDateFormat("MM.dd", Locale.CHINA).format(Date(wordsDayStart + step * i)))
        }
        singleXTextList.value = singleTextList
        wordsXTextList.value = wordsTextList
    }

    // 28 days
    fun monthData() {
        val singleArray = floatArrayOf(-1f, -1f, -1f, -1f)
        val wordsArray = floatArrayOf(-1f, -1f, -1f, -1f)

        val cal = Calendar.getInstance()
        if (mSingleWordHistoryDataList.size > 0) {
            cal.timeInMillis = mSingleWordHistoryDataList.last().time
        }
        val singleDayStart = getStartDayTime(cal, 27)
        if (mWordsHistoryDataList.size > 0) {
            cal.timeInMillis = mWordsHistoryDataList.last().time
        }
        val wordsDayStart = getStartDayTime(cal, 27)

        val step = 7 * 24 * 3600000L
        setData(mSingleWordHistoryDataList, singleArray, singleDayStart, step)
        setData(mWordsHistoryDataList, wordsArray, wordsDayStart, step)

        singleLineList[0] = singleArray
        wordsLineList[0] = wordsArray

        val singleTextList = mutableListOf<String>()
        val wordsTextList = mutableListOf<String>()
        val weekFormat = SRApplication.instance.resources.getString(R.string.week_format)
        for (i in 0 until 4) {
            singleTextList.add(String.format(weekFormat, 4-i))
            wordsTextList.add(String.format(weekFormat, 4-i))
        }
        singleXTextList.value = singleTextList
        wordsXTextList.value = wordsTextList
    }

    fun fromRecordingData() {
        val singleArray = floatArrayOf(-1f, -1f, -1f, -1f)
        val wordsArray = floatArrayOf(-1f, -1f, -1f, -1f)
        val singleTextList = mutableListOf<String>()
        val wordsTextList = mutableListOf<String>()

        if (mSingleWordHistoryDataList.size > 0) {
            singleArray[0] = mSingleWordHistoryDataList[0].data.toFloat() / 100
            singleArray[3] = mSingleWordHistoryDataList.last().data.toFloat() / 100

            singleTextList.addAll(
                    arrayOf(SimpleDateFormat("MM.dd", Locale.CHINA).format(Date(mSingleWordHistoryDataList[0].time)),
                            "", "",
                            SimpleDateFormat("MM.dd", Locale.CHINA).format(Date(mSingleWordHistoryDataList.last().time)))
            )
        }
        if (mWordsHistoryDataList.size > 0) {
            wordsArray[0] = mWordsHistoryDataList[0].data.toFloat() / 100
            wordsArray[3] = mWordsHistoryDataList.last().data.toFloat() / 100

            wordsTextList.addAll(
                    arrayOf(SimpleDateFormat("MM.dd", Locale.CHINA).format(Date(mWordsHistoryDataList[0].time)),
                            "", "",
                            SimpleDateFormat("MM.dd", Locale.CHINA).format(Date(mWordsHistoryDataList.last().time)))
            )
        }

        singleLineList[0] = singleArray
        wordsLineList[0] = wordsArray
        singleXTextList.value = singleTextList
        wordsXTextList.value = wordsTextList
    }

    private fun getStartDayTime(cal: Calendar, days: Int): Long {
        return cal.time.time -
                (cal.get(Calendar.HOUR_OF_DAY) + 24 * days) * 3600000L -
                cal.get(Calendar.MINUTE) * 60000L -
                cal.get(Calendar.SECOND) * 1000 -
                cal.get(Calendar.MILLISECOND)
    }

    private fun setData(dataList: List<HistoryData>, line: FloatArray, time: Long, step: Long) {
        if (dataList.size > 1) {
            var dayStart = time
            var dayCount = 0

            for (i in 0..(dataList.size - 1)) {
                if (dataList[i].time >= dayStart && dataList[i].time < dayStart + step) {
                    line[dayCount] = if (line[dayCount] != -1f) {
                        (line[dayCount] + dataList[i].data.toFloat() / 100) / 2
                    } else {
                        dataList[i].data.toFloat() / 100
                    }
                } else if (dataList[i].time >= dayStart + step) {
                    dayStart += step
                    dayCount++
                    while (!(dataList[i].time >= dayStart && dataList[i].time < dayStart + step)) {
                        dayStart += step
                        dayCount++
                    }
                    line[dayCount] = dataList[i].data.toFloat() / 100
                }
            }
        } else if (dataList.size == 1) {
            line[line.size - 1] = dataList[0].data.toFloat() / 100
        }
    }
}

data class HistoryData(val time: Long, val data: Int)
