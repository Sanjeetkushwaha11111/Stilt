package com.mystilt.customViews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnticipateInterpolator
import android.view.animation.OvershootInterpolator
import com.mystilt.R
import timber.log.Timber
import kotlin.math.cos
import kotlin.math.sin

class SpinningLoader @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        isAntiAlias = true
    }

    private var angle = 0f
    private var itemCount = 8
    private var radiusRatio = 0.32f
    private var dynamicAlpha = 4
    private var color = Color.WHITE
    private var speed = 5f
    private var isSpinning = false
    private var isAnimating = false
    private var isInitialized = false

    init {
        initializeAttributes(context, attrs)
    }

    private fun initializeAttributes(context: Context, attrs: AttributeSet?) {
        if (isInitialized) {
            Timber.e("Already initialized, skipping attribute initialization.")
            return
        }

        context.theme.obtainStyledAttributes(
            attrs, R.styleable.SpinningCircleProgressIndicator, 0, 0
        ).apply {
            try {
                color = getColor(R.styleable.SpinningCircleProgressIndicator_circleColor, Color.WHITE)
                speed = getFloat(R.styleable.SpinningCircleProgressIndicator_rotationSpeed, 5f)
                itemCount = getInt(R.styleable.SpinningCircleProgressIndicator_itemCount, 8)
                Timber.e("Initialized with color: $color, speed: $speed, itemCount: $itemCount")
            } catch (e: Exception) {
                Timber.e("Error initializing attributes: ${e.message}")
            } finally {
                recycle()
            }
        }

        visibility = GONE
        scaleX = 0f
        scaleY = 0f
        alpha = 0f
        Timber.e("SpinningLoader initialized with visibility: $visibility")
        isInitialized = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (width <= 0 || height <= 0) {
            Timber.e("Width or height is zero, skipping onDraw")
            return
        }

        if (!isSpinning) {
            Timber.e("Not spinning, skipping onDraw")
            return
        }

        val centerX = width / 2f
        val centerY = height / 2f
        val maxRadius = width * radiusRatio

        for (i in 0 until itemCount) {
            val rotationAngle = angle + i * (360f / itemCount)
            val alpha = ((itemCount - i) / dynamicAlpha.toFloat()).coerceIn(0f, 1f)

            paint.color = color
            paint.alpha = (alpha * 255).toInt()

            val dx = (centerX + maxRadius * cos(Math.toRadians(rotationAngle.toDouble()))).toFloat()
            val dy = (centerY + maxRadius * sin(Math.toRadians(rotationAngle.toDouble()))).toFloat()

            canvas.drawCircle(dx, dy, maxRadius / 4, paint)
        }

        angle = (angle + speed) % 360
        postInvalidateOnAnimation()
    }

    fun setColor(newColor: Int) {
        color = newColor
        Timber.e("Color set to $color")
        invalidate()
    }

    fun setSpeed(newSpeed: Float) {
        speed = newSpeed
        Timber.d("Speed set to $speed")
    }

    fun setItemCount(count: Int) {
        itemCount = count
        Timber.d("ItemCount set to $itemCount")
        invalidate()
    }

    fun setRadiusRatio(ratio: Float) {
        radiusRatio = ratio
        Timber.d("RadiusRatio set to $radiusRatio")
        invalidate()
    }

    fun showWithPopupEffect() {
        if (visibility == VISIBLE || isAnimating) {
            Timber.e("Show called but loader is already visible or animating")
            return
        }
        isAnimating = true
        visibility = VISIBLE
        isSpinning = true
        Timber.e("Showing loader with animation")
        animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(300)
            .setInterpolator(OvershootInterpolator())
            .withStartAction {
                scaleX = 0f
                scaleY = 0f
                alpha = 0f
                Timber.e("Starting show animation")
            }
            .withEndAction {
                isAnimating = false
                isSpinning = true
                Timber.e("Show animation ended, spinning: $isSpinning, animating: $isAnimating")
            }
            .start()
    }

    fun hideWithPopInEffect() {
        if (visibility == GONE || isAnimating) {
            Timber.e("Hide called but loader is already hidden or animating")
            return
        }
        isAnimating = true
        Timber.e("Hiding loader with animation")
        animate().scaleX(0f).scaleY(0f).alpha(0f).setDuration(500)
            .setInterpolator(AnticipateInterpolator())
            .withEndAction {
                isAnimating = false
                visibility = GONE
                isSpinning = false
                Timber.e("Hide animation ended, spinning: $isSpinning, animating: $isAnimating")
            }
            .start()
        invalidate()
    }
}