package com.ourstilt.homepage

import android.graphics.RenderEffect
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.ourstilt.R
import com.ourstilt.animatedbottombar.AnimatedBottomBar
import com.ourstilt.base.BaseViewPagerAdapter
import com.ourstilt.common.Constants
import com.ourstilt.common.fadeIn
import com.ourstilt.common.fadeOut
import com.ourstilt.common.hide
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
        Timber.e(">>>>>>>>>>>>>>HomeActivity started")
        setupUI()
        observeViewModel()
        homeViewModel.getHomeActivityData()
    }

    private fun setupUI() {
        Timber.e(">>>>>>>>>>>>>>Setting up UI")
        configureWindowInsets()
        setupAppBarBehavior()
        setupBlurView()
    }

    private fun setupBlurView() {
        val decorView = window.decorView
        val rootView = decorView.findViewById<ViewGroup>(android.R.id.content)
        val windowBackground = decorView.background

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            binding.blurView.setupWith(rootView).setFrameClearDrawable(windowBackground)
                .setBlurRadius(10f)
        } else {
            binding.bottomBar.setBackgroundResource(R.drawable.rounded_bg_with_border_gradient)
        }
    }

    private fun observeViewModel() {
        homeViewModel.homeData.observe(this) { data ->
            if (data == null) {
                Timber.e(">>>>>>>>>>>>>>Home data is null")
            } else {
                Timber.e(">>>>>>>>>>>>>>Home data received: $data")
                setupTabsWithPager(data.tabsData, data.tabToLand)
            }
        }
    }

    private fun configureWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupTabsWithPager(tabsData: List<TabData>?, tabToLand: Int?) {
        if (tabsData.isNullOrEmpty()) {
            Timber.e(">>>>>>>>>>>>>>Tabs data is empty. Cannot proceed.")
            return
        }

        Timber.e(">>>>>>>>>>>>>>Setting up tabs with pager")
        binding.recyclerView.adapter = pagerAdapter

        lifecycleScope.launch(Dispatchers.Main) {
            // Preload icons in IO context
            val icons = withContext(Dispatchers.IO) {
                tabsData.map { tabData ->
                    tabData.tabImage?.let { url ->
                        runCatching {
                            Glide.with(this@HomeActivity).asDrawable().load(url).submit().get()
                        }.getOrNull()
                    } ?: ContextCompat.getDrawable(this@HomeActivity, R.drawable.location)
                }
            }

            // Setup tabs on Main thread
            tabsData.forEachIndexed { index, tabData ->
                Timber.e(">>>>>>>>>>>>>>Processing tab: ${tabData.tabName}")

                val fragment = fragmentMap[tabData.tabName] ?: run {
                    Timber.e("No fragment found for tab: ${tabData.tabName}")
                    return@forEachIndexed
                }

                val icon = icons[index]
                setupTab(index, tabData, icon, fragment)
            }

            configureViewPagerWithBottomBar(tabToLand, tabsData.size)
        }
    }


    private fun setupTab(index: Int, tabData: TabData, icon: Drawable?, fragment: Fragment) {
        Timber.e(">>>>>>>>>>>>>>Setting up tab at index: $index, Tab name: ${tabData.tabName}")
        val tab = bottomBar.createTab(icon = icon, title = tabData.tabName ?: "", id = index)
        bottomBar.addTab(tab)

        if (!tabData.badgeCount.isNullOrEmpty()) {
            val badge = AnimatedBottomBar.Badge(tabData.badgeCount)
            bottomBar.setBadgeAtTabIndex(index, badge)
            Timber.e(">>>>>>>>>>>>>>Badge set for tab: ${tabData.tabName}")
        }

        pagerAdapter.addFragment(fragment, tabData.tabName ?: "")
    }

    private fun configureViewPagerWithBottomBar(tabToLand: Int?, totalTabs: Int) {
        Timber.e(">>>>>>>>>>>>>>Configuring ViewPager with BottomBar")
        bottomBar.setupWithViewPager2(binding.recyclerView)

        binding.recyclerView.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                Timber.e(">>>>>>>>>>>>>>Page selected in ViewPager2: $position")
                bottomBar.selectTabAt(position)
            }
        })

        val safeTabIndex = tabToLand?.takeIf { it in 0 until totalTabs } ?: 0
        Timber.e(">>>>>>>>>>>>>>Setting initial tab: $safeTabIndex")
        bottomBar.selectTabAt(safeTabIndex)
        binding.recyclerView.setCurrentItem(safeTabIndex, false)
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
                    Timber.e(">>>>>>>>>>>>>>AppBar expanded")
                }

                abs(verticalOffset) == totalScrollRange -> {
                    searchBarPinned.fadeIn()
                    searchBarFloating.fadeOut()
                    Timber.e(">>>>>>>>>>>>>>AppBar collapsed")
                }

                else -> {
                    searchBarPinned.fadeIn()
                    searchBarFloating.fadeOut()
                    Timber.e(">>>>>>>>>>>>>>AppBar in between states")
                }
            }
        }
    }

    private fun createFragmentMap(): Map<String, Fragment> {
        Timber.d(">>>>>>>>>>>>>>Creating fragment map")
        return mapOf(
            "Home" to HomeFragment(),
            "Home2" to HomeFragment(),
            "Home3" to HomeFragment(),
            "Home4" to HomeFragment()
        )
    }
}
