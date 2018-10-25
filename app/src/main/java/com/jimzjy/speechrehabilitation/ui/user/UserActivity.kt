package com.jimzjy.speechrehabilitation.ui.user

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jimzjy.speechrehabilitation.R
import kotlinx.android.synthetic.main.activity_user.*

class UserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        user_toolbar.title = this.getSharedPreferences("basic", Context.MODE_PRIVATE).getString("username", "")
        setSupportActionBar(user_toolbar)
        user_toolbar.setNavigationOnClickListener { onBackPressed() }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.user_container, UserFragment.newInstance())
                    .commitNow()
        }
    }
}
