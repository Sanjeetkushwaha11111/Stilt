package com.ourstilt.customViews.autoscrollrecyclerview

import android.content.Context
import android.graphics.PorterDuff
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.setMargins
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.ourstilt.R
import com.ourstilt.common.hide
import com.ourstilt.common.percentOf
import com.ourstilt.common.show
import com.ourstilt.databinding.AutoScrollRecyclerviewContainerBinding
import timber.log.Timber

class AutoScrollCircularPagerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var selectedDotColor = ContextCompat.getColor(context, android.R.color.black)
    private var unSelectedDotColor = ContextCompat.getColor(context, android.R.color.white)
    private var autoScrollDelay = 1000L
    private var scrollHandler = Handler(Looper.getMainLooper())
    private var slidingImageDots = ArrayList<AppCompatImageView>()
    private var centerItemPosition = Int.MAX_VALUE / 2
    private var circularAdapter: CircularAdapter<*>? = null
    private var isAutoScrollEnabled = true
    private var isHalfVisible = false
    private var dotBackground = false
    private var marginDots = -1
    private var dotGravity = DotGravity.BOTTOM.value
    private val binding by lazy {
        AutoScrollRecyclerviewContainerBinding.inflate(
            LayoutInflater.from(context), this, true
        )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private val runnable = Runnable {
        autoScrollViewpager()
    }

    init {
        attrs?.let(this::setupAttrs)
        initRecyclerView()
    }

    private fun setupAttrs(attrs: AttributeSet) {
        val typedArray =
            context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.AutoScrollCircularPagerView,
                0,
                0
            )
        try {
            with(typedArray) {
                unSelectedDotColor = getColor(
                    R.styleable.AutoScrollCircularPagerView_unselected_dot_color,
                    ContextCompat.getColor(context, android.R.color.white)
                )
                selectedDotColor = getColor(
                    R.styleable.AutoScrollCircularPagerView_selected_dot_color,
                    ContextCompat.getColor(context, android.R.color.black)
                )
                autoScrollDelay = getInteger(
                    R.styleable.AutoScrollCircularPagerView_auto_scroll_delay,
                    1000
                ).toLong()

                isAutoScrollEnabled = getBoolean(
                    R.styleable.AutoScrollCircularPagerView_is_auto_scroll,
                    true
                )
                isHalfVisible = getBoolean(
                    R.styleable.AutoScrollCircularPagerView_is_item_visible, false
                )
                dotBackground = getBoolean(
                    R.styleable.AutoScrollCircularPagerView_dot_backgroun,
                    false
                )
                marginDots = getDimensionPixelSize(
                    R.styleable.AutoScrollCircularPagerView_dot_margin,
                    -1
                )

                dotGravity = getInt(
                    R.styleable.AutoScrollCircularPagerView_dot_gravity,
                    DotGravity.BOTTOM.value
                )
                if (autoScrollDelay < 500L) {
                    autoScrollDelay = 500L
                }
            }
        } catch (exception: Exception) {
            Timber.e("AutoScrollContainer  $exception")
        } finally {
            typedArray.recycle()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun setAutoScrollDelay(autoScrollDelay: Long) {
        if (autoScrollDelay < 500) {
            this.autoScrollDelay = autoScrollDelay
            stopAutoScrollIfRequired()
            startAutoScrollIfRequired()
        }
    }

    private fun initRecyclerView() {
        val snapHelper: SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvAutoScroll)
        binding.rvAutoScroll.itemAnimator = null
        binding.rvAutoScroll.addOnScrollListener(
            object : OnScrollListener(
                binding.rvAutoScroll.layoutManager as LinearLayoutManager,
                object : CenterItemCallback {
                    override fun onScrollFinished(visibleItemPosition: Int) {
                        centerItemPosition = visibleItemPosition
                        onPageSelected(centerItemPosition)
                    }
                    @RequiresApi(Build.VERSION_CODES.Q)
                    override fun onScrolled(dx: Int) {
                        stopAutoScrollIfRequired()
                    }
                },
                RecyclerView.SCROLL_STATE_IDLE
            ) {
                @RequiresApi(Build.VERSION_CODES.Q)
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        startAutoScrollIfRequired()
                    }
                }
            }
        )
        post {
            startAutoScrollIfRequired()
            setUpHalfVisibility(isHalfVisible)
        }
    }

    private fun setUpHalfVisibility(boolean: Boolean) {
        if (boolean) {
            val horizontalSpace = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
            binding.rvAutoScroll.addItemDecoration(
                HorizontalSpaceItemDecoration(horizontalSpace)
            )

            binding.rvAutoScroll.layoutManager =
                object : LinearLayoutManager(context, HORIZONTAL, false) {
                    override fun checkLayoutParams(lp: RecyclerView.LayoutParams): Boolean {
                        lp.width = 90 percentOf width
                        return true
                    }
                }
        }
    }

    fun setAdapter(circularAdapter: CircularAdapter<*>) {
        this.circularAdapter = circularAdapter
        binding.rvAutoScroll.adapter = circularAdapter
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    @Suppress("UNCHECKED_CAST")
    fun <E> setItems(items: ArrayList<E>, clearPreviousElements: Boolean = false,showDots: Boolean = false) {
        if (binding.rvAutoScroll.adapter is CircularAdapter<*>) {
            val circularAdapter = binding.rvAutoScroll.adapter as CircularAdapter<E>
            circularAdapter.setItems(items, clearPreviousElements)
            if (items.size > 1) {
                val number: Int = Int.MAX_VALUE / items.size / 2
                centerItemPosition = number * items.size
                binding.rvAutoScroll.layoutManager?.scrollToPosition(centerItemPosition)
                if (showDots) {
                    setDots(items)
                } else {
                    binding.llSliderDots.removeAllViews()
                    binding.llSliderDots.hide()
                }
            }
        }
        startAutoScrollIfRequired()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAutoScrollIfRequired()
    }

    private fun <E> setDots(items: ArrayList<E>) {
        if(items.size > 0){
            binding.llSliderDots.show()
        }
        if(!dotBackground){
            binding.llSliderDots.background=null
        }
        slidingImageDots.clear()
        binding.llSliderDots.removeAllViews()
        val llSliderDotsParams = binding.llSliderDots.layoutParams as LayoutParams
        llSliderDotsParams.setMargins(marginDots)
        if (dotGravity == DotGravity.TOP.value) {
            llSliderDotsParams.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        } else {
            llSliderDotsParams.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        }

        binding.llSliderDots.layoutParams = llSliderDotsParams
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(8, 0, 8, 0)

        items.indices.forEach {
            slidingImageDots.add(it, AppCompatImageView(context))
            binding.llSliderDots.addView(slidingImageDots[it], params)
            onPageSelected(0)
        }
    }

    /**
     * Manage dot selection on scroll
     */
    private fun onPageSelected(middleElementPosition: Int) {
        if (slidingImageDots.size > 0) {
            val position = middleElementPosition % slidingImageDots.size
            slidingImageDots.indices.forEach { i ->
                if (i == position) {
                    slidingImageDots[i].setImageResource(R.drawable.ic_active_dot)
                    slidingImageDots[i].setColorFilter(selectedDotColor, PorterDuff.Mode.SRC_IN)
                } else {
                    slidingImageDots[i].setImageResource(R.drawable.ic_inactive_dot)
                    slidingImageDots[i].setColorFilter(unSelectedDotColor, PorterDuff.Mode.SRC_IN)
                }
            }
        }
    }


    fun autoScrollEnabled(boolean: Boolean){
        this.isAutoScrollEnabled = boolean
    }

    fun isHalfVisible(boolean: Boolean){
        isHalfVisible = boolean
    }

    private fun requiredAutoScroll(): Boolean {
        return (circularAdapter?.getActualItemCount() ?: 0) > 1
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun startAutoScrollIfRequired() {
        if (requiredAutoScroll() && !scrollHandler.hasCallbacks(runnable) && isAutoScrollEnabled)
            scrollHandler.postDelayed(runnable, autoScrollDelay)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun stopAutoScrollIfRequired() {
        if (scrollHandler.hasCallbacks(runnable)) {
            scrollHandler.removeCallbacks(runnable)
        }
    }

    /**
     * smooth scroll item to next position
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun autoScrollViewpager() {
        binding.rvAutoScroll.adapter?.let {
            centerItemPosition += 1
            binding.rvAutoScroll.smoothScrollToPosition(centerItemPosition)
        }
        stopAutoScrollIfRequired()
        startAutoScrollIfRequired()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAutoScrollIfRequired()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onVisibilityAggregated(isVisible: Boolean) {
        super.onVisibilityAggregated(isVisible)
        if (isVisible) {
            startAutoScrollIfRequired()
        } else {
            stopAutoScrollIfRequired()
        }
    }

    enum class DotGravity(val value: Int) {
        BOTTOM(0),
        TOP(1)
    }
}