package com.jimzjy.speechrehabilitation.ui.singleWord

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.jimzjy.speechrehabilitation.R
import com.jimzjy.speechrehabilitation.common.SingleWordChoiceRVAdapter
import kotlinx.android.synthetic.main.fragment_single_word.*
import kotlinx.android.synthetic.main.layout_result.*
import kotlinx.android.synthetic.main.layout_tts_hint.*

class SingleWordFragment : Fragment() {
    companion object {
        fun newInstance() = SingleWordFragment()
    }

    private lateinit var mViewModel: SingleWordViewModel
    private lateinit var mChoiceRVAdapter: SingleWordChoiceRVAdapter
    private val mWords = mutableListOf<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_single_word, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(this).get(SingleWordViewModel::class.java)

        mChoiceRVAdapter = SingleWordChoiceRVAdapter(context, mWords)
        single_choice_rv.layoutManager = GridLayoutManager(context, 2)
        single_choice_rv.adapter = mChoiceRVAdapter

        init()
    }

    private fun init() {
        mChoiceRVAdapter.setOnClickCallback {
            val result = mViewModel.check(it)
            resultAnimation(result)

            mViewModel.readyForNext = true
        }
        single_play_hint.setOnClickListener {
            mViewModel.replay()
        }
        single_next_iv.setOnClickListener {
            if (mViewModel.readyForNext) {
                single_choice_result_ll.visibility = View.INVISIBLE
                single_choice_result_tv.visibility =View.INVISIBLE
                single_choice_result_ll.alpha = 0f
                mViewModel.next()
                single_choice_rv.visibility = View.VISIBLE
            } else {
                Toast.makeText(context, resources.getString(R.string.need_select), Toast.LENGTH_LONG).show()
            }
        }

        tts_hint_tts_bt.setOnClickListener {
            mViewModel.speak("你好")
        }
        tts_hint_set_bt.setOnClickListener {
            startActivity(Intent("com.android.settings.TTS_SETTINGS"))
        }
        tts_hint_start_bt.setOnClickListener {
            mViewModel.state.value = TEST_START
            mViewModel.next()
        }
        result_reTest_bt.setOnClickListener {
            mViewModel.score.value = 0
            mViewModel.count.value = 0
            mViewModel.init()
        }

        mViewModel.state.observe(this, Observer {
            when(it) {
                TEST_DATA_INIT_FAILED -> {
                    tts_hint_tv.text = resources.getString(R.string.data_init_failed)
                }
                TEST_TTS_INIT_FAILED -> {
                    tts_hint_tv.text = resources.getString(R.string.tts_init_failed)
                }
                TEST_WAITING -> {
                    tts_hint_tv.text = resources.getString(R.string.waiting_for_init)
                    single_word_cl.visibility = View.INVISIBLE
                    single_result_layout.visibility = View.INVISIBLE
                    single_tts_layout.visibility = View.VISIBLE
                }
                TEST_PREPARED -> {
                    tts_hint_tv.text = resources.getString(R.string.init_successful)
                    tts_hint_start_bt.visibility = View.VISIBLE
                    tts_hint_tts_bt.visibility = View.VISIBLE
                    tts_hint_set_bt.visibility = View.VISIBLE
                }
                TEST_START -> {
                    single_tts_layout.visibility = View.INVISIBLE
                    single_result_layout.visibility = View.INVISIBLE
                    single_word_cl.visibility = View.VISIBLE
                }
                TEST_FINISHED -> {
                    single_result_layout.visibility = View.VISIBLE
                    single_tts_layout.visibility = View.INVISIBLE
                    single_word_cl.visibility = View.INVISIBLE

                    result_score_tv.text = mViewModel.score.value.toString()
                }
            }
        })
        mViewModel.count.observe(this, Observer {
            @Suppress
            single_num_tv.text = "$it / 25"
        })
        mViewModel.score.observe(this, Observer {
            single_score_tv.text = it.toString()
        })
        mViewModel.words.observe(this, Observer {
            mWords.clear()
            mWords.addAll(it)
            mChoiceRVAdapter.notifyDataSetChanged()
        })
        mViewModel.ttsActive.observe(this, Observer {
            single_play_hint.active = it
        })
    }

    private fun resultAnimation(result: Boolean) {
        if (result) {
            context?.let {
                single_choice_iv.setColorFilter(ContextCompat.getColor(it, R.color.colorPrimary))
                single_choice_iv.setImageResource(R.drawable.vector_check)
            }
        } else {
            context?.let {
                single_choice_iv.setColorFilter(ContextCompat.getColor(it, R.color.sr_red))
                single_choice_iv.setImageResource(R.drawable.vector_close)

                single_choice_result_tv.text = mViewModel.currentWord
                single_choice_result_tv.visibility = View.VISIBLE
            }
        }
        single_choice_rv.visibility = View.INVISIBLE
        single_choice_result_ll.visibility = View.VISIBLE
        single_choice_result_ll.animate().setDuration(500).alpha(1f)
    }
}
