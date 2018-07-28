package com.jimzjy.speechrehabilitation.ui.user

import android.content.Intent
import android.graphics.Color
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer

import com.jimzjy.speechrehabilitation.R
import com.jimzjy.speechrehabilitation.ui.CreateUserActivity
import kotlinx.android.synthetic.main.fragment_user.*

const val SINGLE_WORD_LINE_COLOR = "#E65100"
const val WORDS_LINE_COLOR = "#2E7D32"

class UserFragment : Fragment() {
    companion object {
        fun newInstance() = UserFragment()
    }

    private lateinit var mViewModel: UserViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(this).get(UserViewModel::class.java)

        user_history_single_line_chart.lineList = mViewModel.singleLineList
        user_history_single_line_chart.lineColorList = listOf(Color.parseColor(SINGLE_WORD_LINE_COLOR))
        user_history_words_line_chart.lineList = mViewModel.wordsLineList
        user_history_words_line_chart.lineColorList = listOf(Color.parseColor(WORDS_LINE_COLOR))

        init()
    }

    private fun init() {
        user_switch_bt.setOnClickListener {
            startActivity(Intent(context, CreateUserActivity::class.java))
        }
        user_history_week_chip.setOnClickListener {
            user_history_week_chip.isChecked = true
            mViewModel.weekData()
        }
        user_history_month_chip.setOnClickListener {
            user_history_month_chip.isChecked = true
            mViewModel.monthData()
        }
        user_history_fromRecording_chip.setOnClickListener {
            user_history_fromRecording_chip.isChecked = true
            mViewModel.fromRecordingData()
        }

        mViewModel.singleXTextList.observe(this, Observer {
            it?.let {
                user_history_single_line_chart.xTextList = it
                user_history_single_line_chart.rePaint()
            }
        })
        mViewModel.wordsXTextList.observe(this, Observer {
            it?.let {
                user_history_words_line_chart.xTextList = it
                user_history_words_line_chart.rePaint()
            }
        })
    }
}
