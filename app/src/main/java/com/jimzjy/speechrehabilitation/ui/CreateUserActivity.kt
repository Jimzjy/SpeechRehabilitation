package com.jimzjy.speechrehabilitation.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.textfield.TextInputEditText
import com.jimzjy.speechrehabilitation.R
import com.jimzjy.speechrehabilitation.common.SRApplication
import com.jimzjy.speechrehabilitation.common.widget.ListDialog
import com.jimzjy.speechrehabilitation.room.history.History
import com.jimzjy.speechrehabilitation.room.history.HistoryRepository
import com.jimzjy.speechrehabilitation.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_create_user.*
import javax.inject.Inject

class CreateUserActivity : AppCompatActivity() {
    @Inject
    lateinit var historyRepository: HistoryRepository

    private var mUsernameList = mutableListOf<String>()

    init {
        DaggerStartActivityComponent.builder()
                .applicationComponent(SRApplication.applicationComponent)
                .build().inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)

        historyRepository.loadAllUser {
            it?.let {
                mUsernameList.clear()
                mUsernameList.addAll(it)
            }
        }

        init()
    }

    private fun init() {
        create_select_bt.setOnClickListener {
            val listDialog = ListDialog.newInstance(mUsernameList)
            listDialog.resultCallback = {
                val editor = this.getSharedPreferences("basic", Context.MODE_PRIVATE).edit()
                editor.putString("username", mUsernameList[it])
                editor.apply()

                startActivity(Intent(this@CreateUserActivity, MainActivity::class.java))
            }
            listDialog.show(supportFragmentManager, "create_list_dialog")
        }
        create_create_bt.setOnClickListener {
            if (create_username_et.text.isNullOrEmpty()) {
                create_username_til.error = resources.getString(R.string.username_empty)
                return@setOnClickListener
            }
            if (create_username_et.text.toString() in mUsernameList) {
                create_username_til.error = resources.getString(R.string.user_exists)
                return@setOnClickListener
            }
            val history = History(create_username_et.text.toString(), "", "")
            historyRepository.insertHistory(history) {
                val editor = this.getSharedPreferences("basic", Context.MODE_PRIVATE).edit()
                editor.putString("username", history.username)
                editor.apply()

                startActivity(Intent(this@CreateUserActivity, MainActivity::class.java))
            }
        }
        create_username_et.setOnKeyListener { view, _, _ ->
            if (isUsernameValid((view as TextInputEditText).text.toString())) {
                create_username_til.error = null
            }
            false
        }
    }

    private fun isUsernameValid(username: String): Boolean {
        return !(username.isEmpty() || username in mUsernameList)
    }
}
