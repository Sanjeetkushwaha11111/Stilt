package com.ourstilt.homepage.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.Transformation
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.ourstilt.R
import com.ourstilt.base.ui.BaseViewPagerAdapter
import com.ourstilt.common.show
import com.ourstilt.customViews.animatedbottombar.AnimatedBottomBar
import com.ourstilt.databinding.ActivityHomeBinding
import com.ourstilt.deeplink.DeepLinkResponse
import com.ourstilt.homepage.data.HomeTopItem
import com.ourstilt.homepage.data.TabData
import com.ourstilt.homepage.ui.fragments.DailyBiteFragment
import com.ourstilt.homepage.ui.fragments.HomeFragment
import com.ourstilt.homepage.ui.fragments.ShopFragment
import com.ourstilt.search.ui.SearchActivity
import com.ourstilt.userCustomMenu.ui.CustomMenuActivity
import com.ourstilt.userlogin.ui.UserProfileFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.math.abs


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context, deepLinkResponse: DeepLinkResponse? = null): Intent {
            return Intent(context, HomeActivity::class.java)
        }
    }

    // ViewStub inflated layouts
    private var topBg: ConstraintLayout? = null

    private val appBarOffsetListener =
        AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val percentage = abs(verticalOffset).toFloat() / appBarLayout.totalScrollRange
            binding.searchBarFloating.apply {
                alpha = 1 - percentage
                isVisible = percentage < 1f
            }
            binding.searchBarPinned.apply {
                alpha = percentage
                isVisible = percentage > 0f
        }
    }

    private val screenName = "Home"
    private val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }
    private val homeViewModel: HomeViewModel by viewModels()
    private val pagerAdapter by lazy { BaseViewPagerAdapter(supportFragmentManager, lifecycle) }
    private val bottomBar by lazy { binding.bottomBar }
    private val fragmentMap = createFragmentMap()

    private val topItemRecyclerViewAdapter by lazy {
        HomeTopItemViewAdapter(
            homeViewModel, screenName
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        setupUI()
        clickListeners()
        observeViewModel()
        homeViewModel.getHomeActivityData(true)
    }

    private fun clickListeners() {
        binding.searchEt.setOnClickListener {
            openSearch(it)
        }
        binding.searchBarPinned.setOnClickListener {
            openSearch(it)
        }
        binding.userIv.setOnClickListener {
            val intent = Intent(this, CustomMenuActivity::class.java)
            startActivity(intent)
        }
    }

    private fun openSearch(view: View) {
        val options = ActivityOptions.makeSceneTransitionAnimation(
            this,
            view,
            "searchBar"
        )
        val intent = Intent(this@HomeActivity, SearchActivity::class.java)
        startActivity(intent, options.toBundle())
    }

    private fun setupUI() {
        setupBlurView()
    }

    override fun onResume() {
        super.onResume()
        binding.appbar.addOnOffsetChangedListener(appBarOffsetListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.appbar.removeOnOffsetChangedListener(appBarOffsetListener)
    }

    private fun observeViewModel() {
        homeViewModel.homeActivityData.observe(this) { data ->
            data?.let {
                setupTabsWithPager(data.tabsData, data.tabToLand)
                data.homeTopBg?.let {
                    lifecycleScope.launch {
                        delay(1000)
                        setUpHomeTopView(data.homeTopItems!!)
                    }
                } ?: resetScrollFlags()
            }
        }
    }

    private fun resetScrollFlags() {
        (binding.collapsingBar.layoutParams as AppBarLayout.LayoutParams).scrollFlags = 0
    }

    private suspend fun setUpHomeTopView(homeTopItems: List<HomeTopItem>) {
        expandTopBackground()
        delay(3000)
        animateHeightChange(
            binding.topBg,
            targetHeight = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._250sdp)
        )
        showTopItems(homeTopItems)
    }

    private suspend fun expandTopBackground() {
        withContext(Dispatchers.Main) {
            (binding.collapsingBar.layoutParams as AppBarLayout.LayoutParams).scrollFlags =
                AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED or AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP
        }

        suspendCancellableCoroutine { continuation ->
            val matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                (binding.topBg.parent as View).width, View.MeasureSpec.EXACTLY
            )
            val wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                0, View.MeasureSpec.UNSPECIFIED
            )

            binding.topBg.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
            val targetHeight = binding.topBg.measuredHeight
            binding.topBg.layoutParams.height = 1
            binding.topBg.visibility = View.VISIBLE

            val animator = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                    binding.topBg.layoutParams.height =
                        if (interpolatedTime == 1f) ViewGroup.LayoutParams.WRAP_CONTENT
                        else (targetHeight * interpolatedTime).toInt()
                    binding.topBg.requestLayout()
                }

                override fun willChangeBounds(): Boolean = true
            }

            animator.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationRepeat(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    continuation.resume(Unit) {}
                }
            })

            animator.duration = 500
            binding.topBg.startAnimation(animator)
        }
    }

    private suspend fun animateHeightChange(view: View, targetHeight: Int) {
        val currentHeight = view.height

        suspendCancellableCoroutine { continuation ->
            val animator = ValueAnimator.ofInt(currentHeight, targetHeight).apply {
                duration = 300
                interpolator = AccelerateDecelerateInterpolator()

                addUpdateListener { animation ->
                    view.layoutParams = view.layoutParams.apply {
                        height = animation.animatedValue as Int
                    }
                    view.requestLayout()
                }

                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        continuation.resume(Unit) {}
                    }
                })
            }

            animator.start()
        }
    }

    private suspend fun showTopItems(items: List<HomeTopItem>) {
        withContext(Dispatchers.Main) {
            binding.homeTopItemRv.apply {
                show()
                adapter = topItemRecyclerViewAdapter
                topItemRecyclerViewAdapter.submitList(items)
            }
        }
    }

    private fun setupTabsWithPager(tabsData: List<TabData>?, tabToLand: Int?) {
        if (tabsData.isNullOrEmpty()) {
            return
        }
        tabsData.forEachIndexed { _, tabData ->
            val fragmentTag = tabData.tabName ?: return@forEachIndexed
            val fragment = fragmentMap[fragmentTag] ?: return@forEachIndexed
            pagerAdapter.addFragment(fragment, fragmentTag)
        }

        binding.homeViewPager.apply {
            adapter = pagerAdapter
            offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
            isUserInputEnabled = false
        }
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
                val icon = icons[index]
                setupTab(index, tabData, icon)
            }
            configureViewPagerWithBottomBar(tabToLand, tabsData.size)
        }
    }


    private fun setupTab(index: Int, tabData: TabData, icon: Drawable?) {
        val fragmentTag = tabData.tabName ?: ""
        if (binding.bottomBar.tabs.any { it.title == fragmentTag }) {
            Timber.w("Tab with title $fragmentTag already exists. Skipping.")
            return
        }

        try {
            val tab = binding.bottomBar.createTab(icon = icon, title = fragmentTag, id = index)
            binding.bottomBar.addTab(tab)

            if (!tabData.badgeCount.isNullOrEmpty()) {
                val badge = AnimatedBottomBar.Badge(tabData.badgeCount)
                binding.bottomBar.setBadgeAtTabIndex(index, badge)
            }
        } catch (e: Exception) {
            Timber.e("Error setting up tab: ${tabData.tabName}, ${e.message}")
        }
    }
    private fun configureViewPagerWithBottomBar(tabToLand: Int?, totalTabs: Int) {
        try {
            bottomBar.setupWithViewPager2(binding.homeViewPager)
            binding.homeViewPager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    bottomBar.selectTabAt(position)
                }
            })

            val safeTabIndex = tabToLand?.takeIf { it in 0 until totalTabs } ?: 0
            bottomBar.selectTabAt(safeTabIndex)
            binding.homeViewPager.setCurrentItem(safeTabIndex, false)
        } catch (e: Exception) {
            Timber.e("Error configuring ViewPager with BottomBar: ${e.message}")
        }
    }

    private fun createFragmentMap(): Map<String, Fragment> {
        return mapOf(
            "Home" to HomeFragment(),
            "Shops" to ShopFragment(),
            "Daily Bite" to DailyBiteFragment()
        )
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

    fun openProfileFragment() {
        lifecycleScope.launch {
            delay(2000)
            if (!this@HomeActivity.isFinishing) {
                val fragmentManager = supportFragmentManager
                val existingFragment = fragmentManager.findFragmentByTag(UserProfileFragment.TAG)
                if (existingFragment == null || !existingFragment.isVisible) {
                    val bottomSheetFragment = UserProfileFragment()
                    bottomSheetFragment.show(fragmentManager, UserProfileFragment.TAG)
                }
            }
        }
    }
}
