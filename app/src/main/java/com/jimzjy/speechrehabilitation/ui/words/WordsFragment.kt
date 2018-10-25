package com.jimzjy.speechrehabilitation.ui.words

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.view.menu.MenuView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager

import com.jimzjy.speechrehabilitation.R
import com.jimzjy.speechrehabilitation.common.WordsChoiceRVAdapter
import com.jimzjy.speechrehabilitation.room.lattice.Lattice
import com.jimzjy.speechrehabilitation.room.word.Word
import com.jimzjy.speechrehabilitation.ui.singleWord.*
import kotlinx.android.synthetic.main.fragment_words.*
import kotlinx.android.synthetic.main.layout_result.*
import kotlinx.android.synthetic.main.layout_tts_hint.*

class WordsFragment : Fragment() {
    companion object {
        fun newInstance() = WordsFragment()
    }

    private lateinit var mViewModel: WordsViewModel
    private lateinit var mChoiceRVAdapter: WordsChoiceRVAdapter
    private val mLatticeList = mutableListOf<Lattice>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_words, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(this).get(WordsViewModel::class.java)

        mChoiceRVAdapter = WordsChoiceRVAdapter(context, mLatticeList)
        words_choice_rv.layoutManager = GridLayoutManager(context, 2)
        words_choice_rv.adapter = mChoiceRVAdapter

        init()
    }

    private fun init() {
        mChoiceRVAdapter.setOnClickCallback {
            val result = mViewModel.check(it)
            resultAnimation(result)

            mViewModel.readyForNext = true
        }
        words_next_iv.setOnClickListener {
            if (mViewModel.readyForNext) {
                words_choice_result_ll.visibility = View.INVISIBLE
                words_choice_result_tv.visibility = View.INVISIBLE
                words_choice_result_ll.alpha = 0f
                mViewModel.next()
                words_choice_rv.visibility = View.VISIBLE
            } else {
                Toast.makeText(context, resources.getString(R.string.need_select), Toast.LENGTH_LONG).show()
            }
        }
        words_play_hint.setOnClickListener {
            mViewModel.replay()
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
                    words_cl.visibility = View.INVISIBLE
                    words_result_layout.visibility = View.INVISIBLE
                    words_tts_layout.visibility = View.VISIBLE
                }
                TEST_PREPARED -> {
                    tts_hint_tv.text = resources.getString(R.string.init_successful)
                    tts_hint_start_bt.visibility = View.VISIBLE
                    tts_hint_tts_bt.visibility = View.VISIBLE
                    tts_hint_set_bt.visibility = View.VISIBLE
                }
                TEST_START -> {
                    words_cl.visibility = View.VISIBLE
                    words_result_layout.visibility = View.INVISIBLE
                    words_tts_layout.visibility = View.INVISIBLE
                }
                TEST_FINISHED -> {
                    words_cl.visibility = View.INVISIBLE
                    words_result_layout.visibility = View.VISIBLE
                    words_tts_layout.visibility = View.INVISIBLE

                    result_score_tv.text = mViewModel.score.value.toString()
                }
            }
        })
        mViewModel.count.observe(this, Observer {
            @Suppress
            words_num_tv.text = "$it / 25"
        })
        mViewModel.score.observe(this, Observer {
            words_score_tv.text = it.toString()
        })
        mViewModel.lattices.observe(this, Observer {
            mLatticeList.clear()
            mLatticeList.addAll(it)
            mChoiceRVAdapter.notifyDataSetChanged()
        })
        mViewModel.ttsActive.observe(this, Observer {
            words_play_hint.active = it
        })
    }

    private fun resultAnimation(result: Boolean) {
        if (result) {
            context?.let {
                words_choice_result_iv.setColorFilter(ContextCompat.getColor(it, R.color.colorPrimary))
                words_choice_result_iv.setImageResource(R.drawable.vector_check)
            }
        } else {
            context?.let {
                words_choice_result_iv.setColorFilter(ContextCompat.getColor(it, R.color.sr_red))
                words_choice_result_iv.setImageResource(R.drawable.vector_close)

                words_choice_result_tv.text = mViewModel.currentWord
                words_choice_result_tv.visibility = View.VISIBLE
            }
        }
        words_choice_rv.visibility = View.INVISIBLE
        words_choice_result_ll.visibility = View.VISIBLE
        words_choice_result_ll.animate().setDuration(500).alpha(1f)
    }
}
