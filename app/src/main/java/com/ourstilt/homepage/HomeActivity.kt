package com.ourstilt.homepage

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ourstilt.common.Constants.tabToLand
import com.ourstilt.common.fadeIn
import com.ourstilt.common.fadeOut
import com.ourstilt.common.hide
import com.ourstilt.common.loggableFormat
import com.ourstilt.common.show
import com.ourstilt.databinding.ActivityHomeBinding
import timber.log.Timber
import kotlin.math.abs

class HomeActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityHomeBinding.inflate(layoutInflater)
    }

    private val homeViewModel: HomeViewModel by viewModels()
    private var currentTabPosition = 0
    private var screenName = "HomePage"

    private val homeFragment by lazy {
        HomeFragment()
    }
    private val homeFragment2 by lazy {
        HomeFragment()
    }
    private val homeFragment3 by lazy {
        HomeFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        setupScreen()
        setupAppBarBehavior()
        homeViewModel.getHomeActivityData()
        val tabToLand = intent.getIntExtra(tabToLand, -1)
        viewModelObserver(tabToLand)
    }

    private fun viewModelObserver(tabToLand: Int) {
        homeViewModel.homeData.observe(this) {

        }
    }


    private fun setUpViewPager(tabData: Any, tabToLand: Int) {

    }


    private fun setupScreen() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
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
