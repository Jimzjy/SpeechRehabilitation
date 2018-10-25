package com.jimzjy.speechrehabilitation.ui.custom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jimzjy.speechrehabilitation.R
import kotlinx.android.synthetic.main.activity_custom.*

class CustomActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom)
        custom_toolbar.setNavigationOnClickListener { onBackPressed() }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.custom_container, WordsCustomFragment.newInstance())
                    .commitNow()
        }
    }
}
