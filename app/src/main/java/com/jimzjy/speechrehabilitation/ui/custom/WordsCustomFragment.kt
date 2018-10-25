package com.jimzjy.speechrehabilitation.ui.custom

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import com.jimzjy.speechrehabilitation.R
import com.jimzjy.speechrehabilitation.common.widget.LATTICE_PAD_MODE_EDIT_ADD
import com.jimzjy.speechrehabilitation.common.widget.LATTICE_PAD_MODE_EDIT_REMOVE
import com.jimzjy.speechrehabilitation.common.widget.ListDialog
import kotlinx.android.synthetic.main.fragment_words_custom.*

class WordsCustomFragment : Fragment() {

    companion object {
        fun newInstance() = WordsCustomFragment()
    }

    private lateinit var mViewModel: WordsCustomViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_words_custom, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(this).get(WordsCustomViewModel::class.java)

        init()
    }

    override fun onStop() {
        super.onStop()

        mViewModel.latticeBackStack.value = custom_words_lattice_pad.backStack
        mViewModel.latticePointList.value = custom_words_lattice_pad.pointList
        mViewModel.selectChipText.value = custom_select_words_chip.text.toString()
    }

    private fun init() {
        custom_words_lattice_back.setOnClickListener {
            custom_words_lattice_pad.back()
        }
        custom_words_lattice_clear.setOnClickListener {
            custom_words_lattice_pad.clear()
        }
        custom_words_save_bt.setOnClickListener {
            custom_select_words_chip.text.let {
                if (it.isNotEmpty() && it.toString() != resources.getString(R.string.select_words)) {
                    mViewModel.save(it.toString(), custom_words_lattice_pad.pointList)
                } else {
                    Toast.makeText(context, R.string.not_select_words, Toast.LENGTH_LONG).show()
                }
            }
        }
        custom_select_words_chip.setOnClickListener {
            val listDialog = ListDialog.newInstance(mViewModel.wordList, 3)
            listDialog.resultCallback = {
                val word = mViewModel.wordList[it]
                mViewModel.loadLattice(word)
                custom_select_words_chip.text = word
            }
            listDialog.show(fragmentManager, "custom_list_dialog")
        }
        custom_words_lattice_mode_add.setOnClickListener {
            custom_words_lattice_pad.mode = LATTICE_PAD_MODE_EDIT_ADD
            custom_words_lattice_mode_add.isChecked = true
            custom_words_lattice_mode_remove.isChecked = false
        }
        custom_words_lattice_mode_remove.setOnClickListener {
            custom_words_lattice_pad.mode = LATTICE_PAD_MODE_EDIT_REMOVE
            custom_words_lattice_mode_remove.isChecked = true
            custom_words_lattice_mode_add.isChecked = false
        }

        mViewModel.selectChipText.observe(this, Observer {
            it?.let {
                custom_select_words_chip.text = it
            }
        })
        mViewModel.latticePointList.observe(this, Observer {
            it?.let {
                custom_words_lattice_pad.setPointPosition(it)
            }
        })
        mViewModel.latticeBackStack.observe(this, Observer {
            it?.let {
                custom_words_lattice_pad.backStack = it
            }
        })
    }
}
