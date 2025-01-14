package com.ourstilt.homepage

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.ourstilt.R
import com.ourstilt.base.BaseViewPagerAdapter
import com.ourstilt.common.hide
import com.ourstilt.common.hideWithPopInEffect
import com.ourstilt.common.show
import com.ourstilt.common.showKeyboard
import com.ourstilt.common.showToastShort
import com.ourstilt.common.showWithPopupEffect
import com.ourstilt.databinding.ActivitySearchBinding
import com.ourstilt.homepage.data.TabData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class SearchActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySearchBinding.inflate(layoutInflater) }
    private val pagerAdapter by lazy {
        BaseViewPagerAdapter(supportFragmentManager, lifecycle)
    }
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        homeViewModel.searchPageData()
        viewModelObserver()
        lifecycleScope.launch {
            delay(1000)
            setUpSearchListener()
        }
    }

    private fun setUpSearchListener() {
        binding.searchEt.apply {
            requestFocus()
            showKeyboard()
            this@SearchActivity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MODE_CHANGED)
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    binding.clearSearch.showWithPopupEffect()
                }
            }
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch()
                    true
                } else {
                    false
                }
            }
            addTextChangedListener {
                binding.clearSearch.apply {
                    if (it.isNullOrEmpty()){
                        hideWithPopInEffect()
                    }else{
                        showWithPopupEffect()
                    }
                }
            }
        }

        binding.clearSearch.setOnClickListener {
            binding.searchEt.text?.clear()
            binding.searchEt.clearFocus()
            hideKeyboard()
            binding.clearSearch.hideWithPopInEffect()
        }

        binding.searchIcon.setOnClickListener {
            performSearch()
        }
    }

    private fun performSearch() {
        val searchText = binding.searchEt.text.toString().trim()
        if (searchText.isNotEmpty()) {
            this.showToastShort("Search for: $searchText")
        }
        binding.searchEt.clearFocus()
        hideKeyboard()
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchEt.windowToken, 0)
    }

    private fun viewModelObserver() {
        homeViewModel.searchPageData.observe(this) {
            it?.let {
                setUpTabView(it.tabsData, it.tabToShow)
            }
        }
    }

    private fun setUpTabView(tabsData: ArrayList<TabData>?, tabToShow: String?) {
        pagerAdapter.clearData()

        pagerAdapter.apply {
            tabsData?.let {
                for (item in it) {
                    when (item.tabSlug) {
                        "1" -> {
                            addFragment(HomeFragment(), item.tabName.toString())
                            Timber.e(">>>>>>>>Setting shops fragment")
                        }

                        "2" -> {
                            addFragment(HomeFragment(), item.tabName.toString())
                            Timber.e(">>>>>>>>Setting Categories fragment")
                        }

                        "3" -> {
                            addFragment(HomeFragment(), item.tabName.toString())
                            Timber.e(">>>>>>>>Setting Others fragment")
                        }
                    }
                }
            }
        }


        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT

        tabToShow?.toIntOrNull()?.let { index ->
            if (index in 0 until (tabsData?.size ?: 0)) {
                binding.viewPager.setCurrentItem(index, false)
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(index))
            } else {
                binding.viewPager.setCurrentItem(0, false)
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(0))
            }
        }


        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = pagerAdapter.getPageTitle(position)
        }.attach()

        setUpTabListner()
    }

    private fun setUpTabListner() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {}

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

}