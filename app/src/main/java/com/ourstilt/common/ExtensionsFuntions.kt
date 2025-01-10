package com.ourstilt.common

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.google.gson.Gson
import com.ourstilt.R
import org.jsoup.Jsoup
import kotlin.math.roundToInt


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


@ColorInt
internal fun Context.getColorResCompat(@AttrRes id: Int): Int {
    return ContextCompat.getColor(this, getResourceId(id))
}

@ColorInt
internal fun Context.getTextColor(@AttrRes id: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(id, typedValue, true)
    val arr = obtainStyledAttributes(
        typedValue.data, intArrayOf(
            id
        )
    )
    val color = arr.getColor(0, -1)
    arr.recycle()
    return color
}

internal fun Context.getResourceId(id: Int): Int {
    val resolvedAttr = TypedValue()
    theme.resolveAttribute(id, resolvedAttr, true)
    return resolvedAttr.run { if (resourceId != 0) resourceId else data }
}


internal val Int.dpPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).roundToInt()

internal val Int.spPx: Int
    get() = (this * Resources.getSystem().displayMetrics.scaledDensity).roundToInt()

private var currentLongToast: Toast? = null
fun Context.showLongToast(message: String?) {
    currentLongToast?.cancel() // Cancel the currently showing toast if any
    currentLongToast = Toast.makeText(this, message, Toast.LENGTH_LONG).apply {
        show()
    }
}


private var currentShortToast: Toast? = null
fun Context.showToastShort(message: String?) {
    currentShortToast?.cancel() // Cancel the currently showing toast if any
    currentShortToast = Toast.makeText(this, message, Toast.LENGTH_SHORT).apply {
        show()
    }
}

fun Any.loggableFormat(): String {
    return Gson().toJson(this)
}

fun String.fromHtml(): String {
    return HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
}

fun TextView.setTextFromHtmlOrHide(value: String?) {
    if (value.isNullOrEmpty()) {
        hide()
    } else {
        show()
        text = value.fromHtml()
    }
}

fun String.plainTextFromHtmlForAnalytics(): String {
    return Jsoup.parse(this).text()
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}