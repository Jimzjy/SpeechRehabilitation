package com.jimzjy.speechrehabilitation.ui.main

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.jimzjy.speechrehabilitation.R
import com.jimzjy.speechrehabilitation.common.NavigationIconClickListener
import com.jimzjy.speechrehabilitation.ui.custom.CustomActivity
import com.jimzjy.speechrehabilitation.ui.singleWord.SingleWordFragment
import com.jimzjy.speechrehabilitation.ui.user.UserActivity
import com.jimzjy.speechrehabilitation.ui.words.WordsFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.layout_main_backdrop.view.*

class MainActivity : AppCompatActivity() {
    private val mNavigationIconClickListener by lazy {
        NavigationIconClickListener(
                main_container,
                main_backdrop_ll,
                AccelerateDecelerateInterpolator(),
                ContextCompat.getDrawable(this, R.drawable.vector_menu),
                ContextCompat.getDrawable(this, R.drawable.vector_close), 300)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(main_toolbar)
        init()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.main_container, MainFragment.newInstance())
                    .commitNow()
        }
    }

    fun navigateTo(fragment: Fragment, addToBackStack: Boolean = true) {
        val transaction = supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment)

        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }

    private fun init() {
        main_toolbar.setNavigationOnClickListener(mNavigationIconClickListener)

        main_backdrop_ll.let {
            it.main_backdrop_single_word_test.setOnClickListener {
                navigateTo(SingleWordFragment.newInstance())
                mNavigationIconClickListener.click()
            }
            it.main_backdrop_words_test.setOnClickListener {
                navigateTo(WordsFragment.newInstance())
                mNavigationIconClickListener.click()
            }
            it.main_backdrop_custom.setOnClickListener {
                startActivity(Intent(this@MainActivity, CustomActivity::class.java))
            }
            it.main_backdrop_user.setOnClickListener {
                startActivity(Intent(this@MainActivity, UserActivity::class.java))
            }

            val username = this.getSharedPreferences("basic", android.content.Context.MODE_PRIVATE).getString("username", "")
            it.main_backdrop_user.text = String.format(resources.getString(R.string.user_format), username)
        }
    }
}
