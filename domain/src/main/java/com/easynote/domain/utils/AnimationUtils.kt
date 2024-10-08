package com.easynote.domain.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View


object  AnimationUtils {

    fun slideDown(view: View) {
        view.animate()
            .translationY(view.height.toFloat())
            .alpha(0f)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    // superfluous restoration
                    view.visibility = View.GONE
                    view.alpha = 1f
                    view.translationY = 0f
                }
            })
    }

    fun slideUp(view: View) {
        view.visibility = View.VISIBLE
        view.alpha = 0f
        if (view.height > 0) {
            com.easynote.domain.utils.AnimationUtils.slideUpNow(view)
        } else {
            // wait till height is measured
            view.post { com.easynote.domain.utils.AnimationUtils.slideUpNow(view) }
        }
    }

    private fun slideUpNow(view: View) {
        view.translationY = view.height.toFloat()
        view.animate()
            .translationY(0F)
            .alpha(1f)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.visibility = View.VISIBLE
                    view.alpha = 1f
                }
            })
    }
}