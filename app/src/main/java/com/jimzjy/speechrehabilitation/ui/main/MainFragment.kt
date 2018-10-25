package com.jimzjy.speechrehabilitation.ui.main

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jimzjy.speechrehabilitation.R
import com.jimzjy.speechrehabilitation.common.widget.PointPosition
import com.jimzjy.speechrehabilitation.ui.custom.CustomActivity
import com.jimzjy.speechrehabilitation.ui.singleWord.SingleWordFragment
import com.jimzjy.speechrehabilitation.ui.words.WordsFragment
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        init()
    }

    private fun init() {
        main_single_word_test_bt.setOnClickListener {
            (activity as MainActivity).navigateTo(SingleWordFragment.newInstance())
        }
        main_words_test_bt.setOnClickListener {
            (activity as MainActivity).navigateTo(WordsFragment.newInstance())
        }
        main_custom_bt.setOnClickListener {
            startActivity(Intent(context, CustomActivity::class.java))
        }

        val lattice = "0 1|0 2|0 3|0 4|0 5|0 6|0 7|0 8|0 9|0 10|0 11|0 0|0 12|0 16|0 17|0 18|0 19|0 20|0 21|0 22|0 23|0 24|0 25|0 26|0 13|0 14|0 15|1 26|2 26|3 26|4 26|5 26|6 26|7 26|8 26|9 26|10 26|11 26|12 26|13 26|14 26|15 26|16 26|17 26|18 26|19 26|20 26|21 26|22 26|23 26|24 26|25 26|26 26|27 26|28 26|29 26|29 25|29 24|29 23|29 22|29 21|29 20|29 19|29 18|29 17|29 16|29 15|29 14|29 13|29 12|29 8|29 7|29 6|29 5|29 4|29 3|29 2|29 1|29 0|29 11|29 10|29 9|28 0|27 0|26 0|25 0|24 0|23 0|22 0|21 0|20 0|19 0|18 0|17 0|16 0|15 0|14 0|13 0|12 0|11 0|10 0|9 0|8 0|7 0|6 0|5 0|4 0|3 0|2 0|1 0|12 10|12 11|12 12|12 13|12 14|13 10|14 10|15 10|16 10|17 10|17 11|17 12|17 13|17 14|17 15|16 15|15 15|14 15|13 15|12 15|9 7|8 7|8 6|8 5|8 4|14 7|14 6|15 6|16 5|17 5|18 4|19 4|20 10|21 10|22 11|23 11|23 12|24 13|19 17|19 18|19 19|19 20|19 21|12 18|11 18|10 19|9 19|9 20|8 20|9 13|8 13|7 13|6 13|6 14|5 14"
        val list = mutableListOf<PointPosition>()
        lattice.split('|').forEach {
            val tmp = it.split(' ')
            list.add(PointPosition(tmp[0].toInt(), tmp[1].toInt()))
        }
        main_custom_lattice_pad.setPointPosition(list)
    }
}
