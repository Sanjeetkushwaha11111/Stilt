package com.ourstilt.base

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import com.ourstilt.R

object ExtensionsFuntions {

    fun Activity.startWithSlideUp(intent: Intent, finishCurrent: Boolean = true) {
        // Create custom animation options
        val options = ActivityOptions.makeCustomAnimation(
            this,
            R.anim.slide_up,
            R.anim.no_animation
        )

        // Start the activity with the animation
        startActivity(intent, options.toBundle())

        if (finishCurrent) {
            finish()
        }
    }
}