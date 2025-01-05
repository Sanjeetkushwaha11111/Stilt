package com.ourstilt.homepage

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import com.ourstilt.common.Constants.tabToLand
import com.ourstilt.common.fadeIn
import com.ourstilt.common.fadeOut
import com.ourstilt.common.hide
import com.ourstilt.common.show
import com.ourstilt.databinding.ActivityHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.math.abs

class HomeActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityHomeBinding.inflate(layoutInflater)
    }


    private val homeViewModel: HomeViewModel by viewModels()
    private var currentTabPosition = 0
    private var screenName = "HomePage"

    private val pagerAdapter by lazy {
        BaseViewPagerAdapter(supportFragmentManager, lifecycle)
    }


    private val bottomBar by lazy {
        binding.bottomBar
    }

    private val homeFragment1 by lazy {
        HomeFragment()
    }
    private val homeFragment2 by lazy {
        HomeFragment()
    }
    private val homeFragment3 by lazy {
        HomeFragment()
    }
    private val homeFragment4 by lazy {
        HomeFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        setupScreen()
        val tabToLand = intent.getIntExtra(tabToLand, -1)
        viewModelObserver(tabToLand)
        homeViewModel.getHomeActivityData()

    }

    private fun viewModelObserver(tabToLand: Int) {
        homeViewModel.homeData.observe(this) {
            it?.let {
                addBottomBarTabsWithPager(it.tabsData, it.tabToLand)
            }
        }
    }



    private fun addBottomBarTabsWithPager(
        tabsData: List<TabData>?, tabToLand: Int?
    ) {
        if (tabsData.isNullOrEmpty()) {
            Timber.e("Tabs data is empty. Cannot proceed.")
            return
        }

        val fragmentMap = mapOf(
            "Home" to homeFragment1,
            "Home2" to homeFragment2,
            "Home3" to homeFragment3,
            "Home4" to homeFragment4
        )

        lifecycleScope.launch(Dispatchers.Main) {
            tabsData.forEachIndexed { index, tabData ->
                val tabFragment = fragmentMap[tabData.tabName] ?: run {
                    Timber.e("No fragment found for tab: ${tabData.tabName}")
                    return@forEachIndexed
                }

                val icon = withContext(Dispatchers.IO) {
                    tabData.tabImage?.let { url ->
                        Glide.with(this@HomeActivity).asDrawable().load(url).submit().get()
                    } ?: ContextCompat.getDrawable(this@HomeActivity, R.drawable.location)
                }

                addTabAndFragment(index, tabData, icon, tabFragment)
            }

            setupBottomBarWithPager()


            val safeTabIndex = tabToLand?.takeIf { it in tabsData.indices } ?: 0
            bottomBar.selectTabAt(safeTabIndex)
            binding.recyclerView.setCurrentItem(safeTabIndex, false)
        }
    }
    private fun setupBottomBarWithPager() {
        binding.recyclerView.isUserInputEnabled = true
        binding.recyclerView.adapter = pagerAdapter

        bottomBar.setupWithViewPager2(binding.recyclerView)

        binding.recyclerView.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                bottomBar.selectTabAt(position)
            }
        })
    }

    private fun addTabAndFragment(
        index: Int, tabData: TabData, icon: Drawable?, fragment: Fragment
    ) {
        val tab = bottomBar.createTab(icon = icon, title = tabData.tabName ?: "", id = index)
        bottomBar.addTab(tab)

        if (!tabData.badgeCount.isNullOrEmpty()) {
            val badge = AnimatedBottomBar.Badge(tabData.badgeCount)
            bottomBar.setBadgeAtTabIndex(index, badge)
        }

        pagerAdapter.addFragment(fragment, tabData.tabName ?: "")
        binding.recyclerView.adapter = pagerAdapter
    }

    private fun setupScreen() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupAppBarBehavior()
    }
    private fun setupAppBarBehavior() {
        val searchBarPinned = binding.searchBarPinned
        val searchBarFloating = binding.searchBarFloating
        binding.appbar.addOnOffsetChangedListener { _, verticalOffset ->
            val totalScrollRange = binding.appbar.totalScrollRange
            // Detect when the AppBar just starts expanding
            if (verticalOffset > -10 && verticalOffset < 0) {
                searchBarFloating.show()
                searchBarFloating.fadeIn(150)
                searchBarPinned.fadeOut(150) {
                    searchBarPinned.hide()
                }
            } else if (abs(verticalOffset) in 1 until totalScrollRange) {
                searchBarPinned.show()
                searchBarPinned.fadeIn(300)
                searchBarFloating.fadeOut(300)
            } else if (abs(verticalOffset) == totalScrollRange) {
                searchBarPinned.fadeIn(300)
                searchBarFloating.fadeOut(300)
            } else if (verticalOffset == 0) {
                searchBarFloating.fadeIn(300)
                searchBarPinned.fadeOut(300) {
                    searchBarPinned.hide()
                }
            }
        }
    }
}
