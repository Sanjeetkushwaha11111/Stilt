package com.ourstilt.common

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.animation.AnticipateInterpolator
import android.view.animation.OvershootInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.google.gson.Gson
import com.ourstilt.R
import com.ourstilt.common.Constants.PATTERN
import kotlinx.coroutines.suspendCancellableCoroutine
import org.jsoup.Jsoup
import kotlin.math.roundToInt

private var isAnimating = false

fun Activity.startWithSlideUp(intent: Intent, finishCurrent: Boolean = true) {
    val options = ActivityOptions.makeCustomAnimation(
        this, R.anim.slide_up, R.anim.no_animation
    )
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
    currentLongToast= null
}


private var currentShortToast: Toast? = null
fun Context.showToastShort(message: String?) {
    currentShortToast?.cancel()
    currentShortToast = Toast.makeText(this, message, Toast.LENGTH_SHORT).apply {
        show()
    }
    currentShortToast = null
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
fun String.isPhoneNumber(): Boolean {
    val matcher = PATTERN.matcher(this)
    return matcher.matches()
}

private fun Activity.getScreenWidth(): Int {
    val displayMetrics = DisplayMetrics()
    val windowManager = this.windowManager
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    } else {
        return windowManager.currentWindowMetrics.bounds.width()
    }
}

fun ImageView.animateBackButton() {
    // Create a rounded black background
    val shape = GradientDrawable().apply {
        shape = GradientDrawable.OVAL
        setColor(Color.BLACK)
    }

    // Set rounded black background with a smooth transition
    this.background = shape

    // Pop animation (scale)
    val scaleX = ObjectAnimator.ofFloat(this, "scaleX", 1f, 1.2f, 1f)
    val scaleY = ObjectAnimator.ofFloat(this, "scaleY", 1f, 1.2f, 1f)
    scaleX.duration = 300
    scaleY.duration = 300

    // Animate color change of the arrow from black to white
    val colorAnimator = ObjectAnimator.ofObject(
        this.drawable,
        "tint",
        ArgbEvaluator(),
        Color.BLACK,  // Start color (arrow)
        Color.WHITE   // End color (arrow)
    ).apply {
        duration = 300
    }

    // Start animations together
    scaleX.start()
    scaleY.start()
    colorAnimator.start()
}

fun View.showKeyboard() {
    this.post {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
}

fun View.hideKeyboard() {
    this.post {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}
fun TextView.slideDown(duration: Long = 500) {
    this.translationY = -this.height.toFloat()
    this.visibility = View.VISIBLE
    ObjectAnimator.ofFloat(this, "translationY", 0f).apply {
        this.duration = duration
        start()
    }
}

fun View.showWithPopupEffect() {
    if (visibility == View.VISIBLE || isAnimating) {
        return
    }
    isAnimating = true
    visibility = View.VISIBLE
    animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(500)
        .setInterpolator(OvershootInterpolator()).withStartAction {
            scaleX = 0f
            scaleY = 0f
            alpha = 0f
        }.withEndAction {
            isAnimating = false
        }.start()
}

fun View.hideWithPopInEffect() {
    if (visibility == View.GONE || isAnimating) {
        return
    }
    isAnimating = true
    animate().scaleX(0f).scaleY(0f).alpha(0f).setDuration(500)
        .setInterpolator(AnticipateInterpolator()).withEndAction {
            isAnimating = false
            visibility = View.GONE
        }.start()
    invalidate()
}


@SuppressLint("SetTextI18n")
fun TextView.animateTextChangeIfDifferent(
    oldValue: String, newValue: String, duration: Long = 300, isPrice: Boolean = false
) {
    if (oldValue == newValue) return
    val oldNumber = oldValue.replace("₹", "").toDoubleOrNull()
    val newNumber = newValue.replace("₹", "").toDoubleOrNull()

    if (oldNumber != null && newNumber != null) {
        val animator = ValueAnimator.ofFloat(oldNumber.toFloat(), newNumber.toFloat())
        animator.duration = duration
        animator.addUpdateListener {
            if (isPrice) {
                this.text = "₹${(it.animatedValue as Float).toInt()}"
            } else {
                this.text = "${(it.animatedValue as Float).toInt()}"
            }
        }
        animator.start()
    } else {
        this.animate().alpha(0f).setDuration(duration / 2).withEndAction {
            this.text = newValue
            this.animate().alpha(1f).setDuration(duration / 2).start()
        }.start()
    }
}
inline fun <reified T> T.toJson(): String {
    val gson = Gson()
    return gson.toJson(this)
}
fun View.vibrateOnClick(duration: Long = 50) {
    this.setOnClickListener {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(duration)
        }
    }
}

infix fun Int.percentOf(value: Int): Int {
    return if (this == 0) 0
    else ((this.toDouble() / 100) * value).toInt()
}
suspend fun ValueAnimator.awaitEnd() = suspendCancellableCoroutine<Unit> { continuation ->
    addListener(object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator) {}
        override fun onAnimationEnd(animation: Animator) {
            continuation.resume(Unit) {}
        }

        override fun onAnimationCancel(animation: Animator) {
            continuation.resume(Unit) {}
        }

        override fun onAnimationRepeat(animation: Animator) {}
    })
}