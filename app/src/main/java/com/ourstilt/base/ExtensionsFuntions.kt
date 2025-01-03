package com.ourstilt.base

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.view.View
import com.ourstilt.R


fun Activity.startWithSlideUp(intent: Intent, finishCurrent: Boolean = true) {
    // Create custom animation options
    val options = ActivityOptions.makeCustomAnimation(
        this, R.anim.slide_up, R.anim.no_animation
    )

    // Start the activity with the animation
    startActivity(intent, options.toBundle())

    if (finishCurrent) {
        finish()
    }
}

fun View.fadeIn(duration: Long = 300) {
    if (visibility != View.VISIBLE) visibility = View.VISIBLE
    animate().alpha(1f).setDuration(duration).start()
}

fun View.fadeOut(duration: Long = 300, endAction: (() -> Unit)? = null) {
    animate().alpha(0f).setDuration(duration).withEndAction {
        visibility = View.GONE
        endAction?.invoke()
    }.start()
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.isVisible(): Boolean = visibility == View.VISIBLE
fun View.isGone(): Boolean = visibility == View.GONE

