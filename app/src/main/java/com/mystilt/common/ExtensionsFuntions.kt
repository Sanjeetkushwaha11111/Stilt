package com.mystilt.common

import android.R.attr.defaultValue
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
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
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnticipateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.view.animation.Transformation
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.mystilt.R
import com.mystilt.common.Constants.PATTERN
import com.mystilt.homepage.data.CustomText
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.suspendCancellableCoroutine
import org.jsoup.Jsoup
import timber.log.Timber
import kotlin.math.roundToInt
import androidx.core.graphics.toColorInt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

fun Context.showLongToast(message: String?) {
    ToastManager.show(this, message, true)
}

fun Context.showToastShort(message: String?) {
    ToastManager.show(this, message, false)
}

private object ToastManager {
    private var currentToast: Toast? = null
    fun show(context: Context, message: String?, isLong: Boolean) {
        currentToast?.cancel()
        currentToast = Toast.makeText(
            context.applicationContext,
            message,
            if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
        ).apply { show() }
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

fun Activity.getScreenHeightPercentage(percentage: Float): Int {
    val displayMetrics = DisplayMetrics()
    val windowManager = this.windowManager
    val screenHeight = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        displayMetrics.heightPixels
    } else {
        windowManager.currentWindowMetrics.bounds.height()
    }

    return (screenHeight * (percentage / 100)).toInt()
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

// Extension for Activity
fun Activity.requestPermission(
    permission: AppPermission,
    onGranted: () -> Unit,
    onDenied: () -> Unit = {},
    onShowRationale: () -> Unit = { showDefaultRationale(permission) },
    onPermanentlyDenied: () -> Unit = { showSettingsDialog() }
) {
    when (PermissionUtils.checkPermission(this, permission)) {
        PermissionUtils.PermissionResult.Granted -> onGranted()
        PermissionUtils.PermissionResult.ShowRationale -> onShowRationale()
        PermissionUtils.PermissionResult.Denied -> {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permission.permission),
                permission.requestCode
            )
        }
        PermissionUtils.PermissionResult.PermanentlyDenied -> onPermanentlyDenied()
    }
}


// Extension for Fragment
fun Fragment.requestPermission(
    permission: AppPermission,
    onGranted: () -> Unit,
    onDenied: () -> Unit = {},
    onShowRationale: () -> Unit = { requireActivity().showDefaultRationale(permission) },
    onPermanentlyDenied: () -> Unit = { requireActivity().showSettingsDialog() }
) {
    context?.let { context ->
        when (PermissionUtils.checkPermission(context, permission)) {
            PermissionUtils.PermissionResult.Granted -> onGranted()
            PermissionUtils.PermissionResult.ShowRationale -> onShowRationale()
            PermissionUtils.PermissionResult.Denied -> {
                requestPermissions(
                    arrayOf(permission.permission),
                    permission.requestCode
                )
            }
            PermissionUtils.PermissionResult.PermanentlyDenied -> onPermanentlyDenied()
        }
    }
}

private fun Activity.showDefaultRationale(permission: AppPermission) {
    androidx.appcompat.app.AlertDialog.Builder(this)
        .setTitle("Permission Required")
        .setMessage(permission.rationale)
        .setPositiveButton("Grant") { _, _ ->
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permission.permission),
                permission.requestCode
            )
        }
        .setNegativeButton("Deny") { dialog, _ ->
            dialog.dismiss()
        }
        .show()
}

private fun Context.showSettingsDialog() {
    androidx.appcompat.app.AlertDialog.Builder(this)
        .setTitle("Permission Required")
        .setMessage("This permission is required. Please enable it in app settings.")
        .setPositiveButton("Settings") { _, _ ->
            openAppSettings()
        }
        .setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        .show()
}

private fun Context.openAppSettings() {
    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", packageName, null)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(this)
    }
}
internal fun pxToDp(px: Int): Int {
    val density = Resources.getSystem().displayMetrics.density
    return (px / density).toInt()
}

fun View.slideUp(duration: Long = 500) {
    this.post {
        this.translationY = this.height.toFloat() // Start from bottom
        this.visibility = View.VISIBLE
        this.animate()
            .translationY(0f) // Move to original position
            .setDuration(duration)
            .setInterpolator(DecelerateInterpolator()) // Smooth deceleration
            .start()
    }
}


fun TextView.applyCustomText(customText: CustomText) {
    text = customText.text ?: ""
    customText.size?.toFloatOrNull()?.let { textSize = it }
    customText.color?.let { colorHex ->
        runCatching { setTextColor(colorHex.toColorInt()) }
            .onFailure { Timber.e("Invalid color: $colorHex") }
    }
    customText.fontWeight?.let { weight ->
        setTypeface(typeface, when (weight.lowercase()) {
            "bold" -> Typeface.BOLD
            "italic" -> Typeface.ITALIC
            "bold_italic" -> Typeface.BOLD_ITALIC
            else -> Typeface.NORMAL
        })
    }
}

fun View.setBackgroundWithBorder(
    backgroundColor: String,
    borderColor: String=backgroundColor,
    borderWidth: Int = 0,
    cornerRadius: Float = 0f
) {
    val drawable = GradientDrawable().apply {
        setColor(backgroundColor.toColorInt())
        setStroke(borderWidth, borderColor.toColorInt())
        this.cornerRadius = cornerRadius
    }
    this.background = drawable
}

fun TextView.startRotatingHintsWithCoroutine(
    hints: List<String>,
    interval: Long = 3000,
    lifecycleScope: CoroutineScope,
    hintColor: Int = Color.BLACK
) {
    lifecycleScope.launch {
        var index = 0
        while (true) {
            text = hints[index]
            setHintTextColor(hintColor) // Apply hint color
            index = (index + 1) % hints.size
            delay(interval)
        }
    }
}

