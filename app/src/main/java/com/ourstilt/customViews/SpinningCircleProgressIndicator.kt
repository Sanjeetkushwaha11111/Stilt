package com.ourstilt.customViews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnticipateInterpolator
import android.view.animation.OvershootInterpolator
import com.ourstilt.R

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
    private var isSpinning = true

    init {
        context.theme.obtainStyledAttributes(
            attrs, R.styleable.SpinningCircleProgressIndicator, 0, 0
        ).apply {
            try {
                color =
                    getColor(R.styleable.SpinningCircleProgressIndicator_circleColor, Color.BLUE)
                speed = getFloat(R.styleable.SpinningCircleProgressIndicator_rotationSpeed, 5f)
                itemCount = getInt(R.styleable.SpinningCircleProgressIndicator_itemCount, 8)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                recycle()
            }
        }
        postInvalidateOnAnimation()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (width <= 0 || height <= 0) {
            return
        }

        if (!isSpinning) return

        val centerX = width / 2f
        val centerY = height / 2f
        val maxRadius = width * radiusRatio

        for (i in 0 until itemCount) {
            val rotationAngle = angle + i * (360f / itemCount)
            val alpha = ((itemCount - i) / dynamicAlpha.toFloat()).coerceIn(0f, 1f)

            paint.color = color
            paint.alpha = (alpha * 255).toInt()

            val dx =
                (centerX + maxRadius * Math.cos(Math.toRadians(rotationAngle.toDouble()))).toFloat()
            val dy =
                (centerY + maxRadius * Math.sin(Math.toRadians(rotationAngle.toDouble()))).toFloat()

            canvas.drawCircle(dx, dy, maxRadius / 4, paint)
        }

        angle = (angle + speed) % 360
        postInvalidateOnAnimation()
    }

    fun setColor(newColor: Int) {
        color = newColor
        invalidate()
    }

    fun setSpeed(newSpeed: Float) {
        speed = newSpeed
    }

    fun setItemCount(count: Int) {
        itemCount = count
        invalidate()
    }

    fun setRadiusRatio(ratio: Float) {
        radiusRatio = ratio
        invalidate()
    }

    fun showWithPopupEffect() {
        try {
            if (visibility == VISIBLE) return
            visibility = VISIBLE
            isSpinning = true
            animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(300)
                .setInterpolator(OvershootInterpolator()).withStartAction {
                    scaleX = 0f
                    scaleY = 0f
                    alpha = 0f
                }.start()
            postInvalidateOnAnimation()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun hideWithPopInEffect() {
        try {
            animate().scaleX(0f).scaleY(0f).alpha(0f).setDuration(300)
                .setInterpolator(AnticipateInterpolator()).withEndAction {
                    visibility = GONE
                    isSpinning = false
                }.start()
            invalidate()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}


