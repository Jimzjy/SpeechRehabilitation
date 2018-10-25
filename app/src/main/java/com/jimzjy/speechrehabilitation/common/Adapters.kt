package com.jimzjy.speechrehabilitation.common

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jimzjy.speechrehabilitation.R
import com.jimzjy.speechrehabilitation.common.widget.LatticePad
import com.jimzjy.speechrehabilitation.common.widget.PointPosition
import com.jimzjy.speechrehabilitation.room.lattice.Lattice
import kotlinx.android.synthetic.main.item_single_choice_rv.view.*
import kotlinx.android.synthetic.main.item_words_choice_rv.view.*

class SingleWordChoiceRVAdapter(context: Context?, private val mChoiceList: List<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mInflater = LayoutInflater.from(context)
    private var mClickCallback: ((String) -> Unit)? = null

    inner class ViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        val text: TextView = root.item_single_choice_tv

        init {
            root.setOnClickListener {
                mClickCallback?.invoke(mChoiceList[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mInflater.inflate(R.layout.item_single_choice_rv, parent, false))
    }

    override fun getItemCount(): Int {
        return mChoiceList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as ViewHolder

        viewHolder.text.text = mChoiceList[position]
    }

    fun setOnClickCallback(callback: (String) -> Unit) {
        mClickCallback = callback
    }
}

class WidgetListDialogRVAdapter(context: Context?, private val mDataList: List<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mInflater = LayoutInflater.from(context)
    private var mClickCallback: ((position: Int) -> Unit)? = null

    inner class ViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        val name: TextView = root.findViewById(R.id.widget_list_dialog_item_tv)

        init {
            root.setOnClickListener {
                mClickCallback?.invoke(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mInflater.inflate(R.layout.widget_list_dialog_item, parent, false))
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as ViewHolder
        viewHolder.name.text = mDataList[position]
    }

    fun setOnClickCallback(callback: (position: Int) -> Unit) {
        mClickCallback = callback
    }
}

class WordsChoiceRVAdapter(context: Context?, private val mChoiceList: List<Lattice>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mInflater = LayoutInflater.from(context)
    private var mClickCallback: ((String) -> Unit)? = null

    inner class ViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        val text: TextView = root.item_words_tv
        val latticePad: LatticePad = root.item_words_lattice_pad

        init {
            root.item_words_tv.setOnClickListener {
                mClickCallback?.invoke(mChoiceList[adapterPosition].word)
            }
            root.item_words_lattice_cover_view.setOnClickListener {
                mClickCallback?.invoke(mChoiceList[adapterPosition].word)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mInflater.inflate(R.layout.item_words_choice_rv, parent, false))
    }

    override fun getItemCount(): Int {
        return mChoiceList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as ViewHolder

        viewHolder.text.text = mChoiceList[position].word

        mChoiceList[position].lattice.let {
            if (it.isNotEmpty()) {
                val list = mutableListOf<PointPosition>()
                it.split('|').forEach {
                    if (it.isNotEmpty()) {
                        val p = it.split(' ')
                        list.add(PointPosition(p[0].toInt(), p[1].toInt()))
                    }
                }
                viewHolder.latticePad.visibility = View.VISIBLE
                viewHolder.text.textSize = 16f
                viewHolder.latticePad.setPointPosition(list)
            } else {
                viewHolder.latticePad.visibility = View.GONE
                viewHolder.text.textSize = 24f
            }
        }
    }

    fun setOnClickCallback(callback: (String) -> Unit) {
        mClickCallback = callback
    }
}