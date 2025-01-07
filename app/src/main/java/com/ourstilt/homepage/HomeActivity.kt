package com.ourstilt.homepage

import android.animation.ArgbEvaluator
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.RenderEffect
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.Animator
import androidx.core.animation.AnimatorListenerAdapter
import androidx.core.animation.ObjectAnimator
import androidx.core.animation.ValueAnimator
import androidx.core.content.ContextCompat
import androidx.core.os.HandlerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.ourstilt.R
import com.ourstilt.animatedbottombar.AnimatedBottomBar
import com.ourstilt.base.BaseViewPagerAdapter
import com.ourstilt.common.Constants
import com.ourstilt.common.fadeIn
import com.ourstilt.common.fadeOut
import com.ourstilt.common.hide
import com.ourstilt.common.loggableFormat
import com.ourstilt.common.setTextFromHtmlOrHide
import com.ourstilt.common.show
import com.ourstilt.databinding.ActivityHomeBinding
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderEffectBlur
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.math.abs

class HomeActivity : AppCompatActivity() {

    private val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }
    private val homeViewModel: HomeViewModel by viewModels()
    private val pagerAdapter by lazy { BaseViewPagerAdapter(supportFragmentManager, lifecycle) }
    private val bottomBar by lazy { binding.bottomBar }
    private val fragmentMap = createFragmentMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        setupUI()
        observeViewModel()
        homeViewModel.getHomeActivityData()
    }

    private fun setupUI() {
        configureWindowInsets()
        setupAppBarBehavior()
        setupBlurView()
    }


    private fun observeViewModel() {
        homeViewModel.homeData.observe(this) { data ->

            data?.let {
                setupTabsWithPager(data.tabsData, data.tabToLand)
                val welcomeTextBlack = "#111111"
                val welcomeTextWhite = "#ffffff"
                binding.optionalTv.apply {
                    setTextFromHtmlOrHide(data.welcomeText)
                    setTextColor(Color.parseColor(welcomeTextBlack))
                }
                if (!data.homeTopBg.isNullOrEmpty()) {
                    binding.topBg.apply {
                        data.homeTopBg?.let { imageUrl ->
                            Glide.with(context).asBitmap().load(imageUrl)
                                .listener(object : RequestListener<Bitmap> {
                                    override fun onResourceReady(
                                        resource: Bitmap,
                                        model: Any,
                                        target: com.bumptech.glide.request.target.Target<Bitmap>?,
                                        dataSource: DataSource,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        binding.topBg.setImageBitmap(resource)
                                        expand(binding.topBg, 500)
                                        return true
                                    }

                                    override fun onLoadFailed(
                                        e: GlideException?,
                                        model: Any?,
                                        target: com.bumptech.glide.request.target.Target<Bitmap>,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        Timber.e("Image load failed: ${e?.message}")
                                        return false
                                    }
                                }).into(binding.topBg)
                        } ?: run {
                            Timber.e("Banner image is null, hiding imgParent")
                            hide()
                        }
                    }
                    binding.topBg.setBackgroundResource(R.color.colorBlack)
                    binding.optionalTv.apply {
                        setTextFromHtmlOrHide(data.welcomeText)
                        setTextColor(Color.parseColor(welcomeTextWhite))
                    }
                } else {
                    binding.optionalTv.apply {
                        setTextFromHtmlOrHide(data.welcomeText)
                        setTextColor(Color.parseColor(welcomeTextBlack))
                    }
                }

            }
        }
    }

    private fun setupTabsWithPager(tabsData: List<TabData>?, tabToLand: Int?) {
        if (tabsData.isNullOrEmpty()) {
            return
        }
        binding.recyclerView.adapter = pagerAdapter
        lifecycleScope.launch(Dispatchers.Main) {
            val icons = withContext(Dispatchers.IO) {
                tabsData.map { tabData ->
                    tabData.tabImage?.let { url ->
                        runCatching {
                            Glide.with(this@HomeActivity).asDrawable().load(url).submit().get()
                        }.getOrNull()
                    } ?: ContextCompat.getDrawable(this@HomeActivity, R.drawable.location)
                }
            }

            tabsData.forEachIndexed { index, tabData ->
                val fragment = fragmentMap[tabData.tabName] ?: run {
                    return@forEachIndexed
                }

                val icon = icons[index]
                setupTab(index, tabData, icon, fragment)
            }

            configureViewPagerWithBottomBar(tabToLand, tabsData.size)
        }
    }

    private fun setupTab(index: Int, tabData: TabData, icon: Drawable?, fragment: Fragment) {
        val tab = bottomBar.createTab(icon = icon, title = tabData.tabName ?: "", id = index)
        bottomBar.addTab(tab)

        if (!tabData.badgeCount.isNullOrEmpty()) {
            val badge = AnimatedBottomBar.Badge(tabData.badgeCount)
            bottomBar.setBadgeAtTabIndex(index, badge)
        }

        pagerAdapter.addFragment(fragment, tabData.tabName ?: "")
    }

    private fun configureViewPagerWithBottomBar(tabToLand: Int?, totalTabs: Int) {
        bottomBar.setupWithViewPager2(binding.recyclerView)

        binding.recyclerView.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                bottomBar.selectTabAt(position)
            }
        })

        val safeTabIndex = tabToLand?.takeIf { it in 0 until totalTabs } ?: 0
        bottomBar.selectTabAt(safeTabIndex)
        binding.recyclerView.setCurrentItem(safeTabIndex, false)
    }

    private fun createFragmentMap(): Map<String, Fragment> {
        return mapOf(
            "Home" to HomeFragment(),
            "Home2" to HomeFragment(),
            "Home3" to HomeFragment(),
            "Home4" to HomeFragment()
        )
    }

    private fun expand(v: View, duration: Long) {
        val matchParentMeasureSpec =
            View.MeasureSpec.makeMeasureSpec((v.parent as View).width, View.MeasureSpec.EXACTLY)
        val wrapContentMeasureSpec =
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
        val targetHeight = v.measuredHeight
        v.layoutParams.height = 1
        v.visibility = View.VISIBLE
        val animator: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                v.layoutParams.height =
                    if (interpolatedTime == 1f) ViewGroup.LayoutParams.WRAP_CONTENT
                    else (targetHeight * interpolatedTime).toInt()
                v.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }
        animator.duration = duration
        v.startAnimation(animator)
    }

    private fun setupAppBarBehavior() {
        binding.appbar.addOnOffsetChangedListener { _, verticalOffset ->
            val totalScrollRange = binding.appbar.totalScrollRange
            val searchBarPinned = binding.searchBarPinned
            val searchBarFloating = binding.searchBarFloating

            when {
                verticalOffset == 0 -> {
                    searchBarFloating.fadeIn()
                    searchBarPinned.fadeOut { searchBarPinned.hide() }
                }

                abs(verticalOffset) == totalScrollRange -> {
                    searchBarPinned.fadeIn()
                    searchBarFloating.fadeOut()
                }

                else -> {
                    searchBarPinned.fadeIn()
                    searchBarFloating.fadeOut()
                }
            }
        }
    }

    private fun setupBlurView() {
        val decorView = window.decorView
        val rootView = decorView.findViewById<ViewGroup>(android.R.id.content)
        val windowBackground = decorView.background

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            binding.blurView.setupWith(rootView).setFrameClearDrawable(windowBackground)
                .setBlurRadius(20f)
        } else {
            binding.bottomBar.setBackgroundResource(R.drawable.rounded_bg_with_border_gradient)
        }
    }

    private fun configureWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
