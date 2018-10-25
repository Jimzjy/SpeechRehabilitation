package com.jimzjy.speechrehabilitation.common.widget

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.jimzjy.speechrehabilitation.R
import com.jimzjy.speechrehabilitation.common.WidgetListDialogRVAdapter
import kotlinx.android.synthetic.main.widget_list_dialog.*
import java.util.regex.Pattern

class ListDialog : DialogFragment() {
    companion object {
        fun newInstance(dataList: List<String>, spanCount: Int = 1) = ListDialog().apply {
            this.mDataList.addAll(dataList)
            this.mDataListOG.addAll(dataList)
            this.mSpanCount = spanCount
        }
    }

    var resultCallback: ((Int) -> Unit)? = null

    private var mSpanCount = 1
    private val mDataList = mutableListOf<String>()
    private val mDataListOG = mutableListOf<String>()
    private lateinit var mRVAdapter: WidgetListDialogRVAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(R.layout.widget_list_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mRVAdapter = WidgetListDialogRVAdapter(context, mDataList)
        widget_list_dialog_rv.layoutManager = GridLayoutManager(context, mSpanCount)
        widget_list_dialog_rv.adapter = mRVAdapter
        mRVAdapter.setOnClickCallback {
            resultCallback?.invoke(it)
            dismiss()
        }

        widget_list_dialog_search_et.setOnKeyListener { v, _, _ ->
            val et = (v as TextInputEditText)
            if (et.text.isNullOrEmpty()) {
                return@setOnKeyListener false
            }
            search(et.text.toString())

            false
        }
    }

    private fun search(text: String) {
        val list = mutableListOf<String>()
        val regex = Pattern.compile(".*$text.*", Pattern.CASE_INSENSITIVE)

        mDataListOG.forEach {
            if (regex.matcher(it).matches()) {
                list.add(it)
            }
        }

        mDataList.clear()
        mDataList.addAll(list)
        mRVAdapter.notifyDataSetChanged()
    }
}