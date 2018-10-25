package com.jimzjy.speechrehabilitation.common

import android.animation.ObjectAnimator
import android.graphics.drawable.Drawable
import android.view.View
import android.view.animation.Interpolator
import android.widget.ImageView

class NavigationIconClickListener(
        private val mTargetView: View,
        private val mControlView: View,
        private val mInterpolator: Interpolator? = null,
        private val mOpenIcon: Drawable? = null,
        private val mCloseIcon: Drawable? = null,
        private val mDuration: Long = 300) : View.OnClickListener {

    private var mIsBackdropShown = false
    private var mNavigationView: View? = null

    override fun onClick(view: View) {
        mNavigationView = view

        mIsBackdropShown = !mIsBackdropShown
        updateIcon(view)

        val translateY = mControlView.height

        val animator = ObjectAnimator.ofFloat(mTargetView, "translationY", (if(mIsBackdropShown) translateY else 0).toFloat())
        animator.duration = mDuration
        if (mInterpolator != null) {
            animator.interpolator = mInterpolator
        }

        animator.start()
    }

    fun click() {
        if (mNavigationView == null) return

        mIsBackdropShown = !mIsBackdropShown
        updateIcon(mNavigationView!!)

        val translateY = mControlView.height

        val animator = ObjectAnimator.ofFloat(mTargetView, "translationY", (if(mIsBackdropShown) translateY else 0).toFloat())
        animator.duration = mDuration
        if (mInterpolator != null) {
            animator.interpolator = mInterpolator
        }

        animator.start()
    }

    private fun updateIcon(view: View) {
        if (mOpenIcon != null && mCloseIcon != null) {
            if (view !is ImageView) {
                throw IllegalArgumentException("updateIcon() must be called on an ImageView")
            }
            if (mIsBackdropShown) {
                view.setImageDrawable(mCloseIcon)
            } else {
                view.setImageDrawable(mOpenIcon)
            }
        }
    }
}