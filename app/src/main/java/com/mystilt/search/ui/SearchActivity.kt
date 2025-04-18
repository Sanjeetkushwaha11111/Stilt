package com.mystilt.search.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.mystilt.R
import com.mystilt.base.ui.BaseViewPagerAdapter
import com.mystilt.common.showKeyboard
import com.mystilt.common.showToastShort
import com.mystilt.databinding.ActivitySearchBinding
import com.mystilt.deeplink.DeepLinkResponse
import com.mystilt.homepage.ui.fragments.HomeFragment
import com.mystilt.homepage.data.TabData
import com.mystilt.search.ui.trendingfragment.TrendingFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_DEEP_LINK_RESPONSE = "extra_deep_link_response"
        fun newIntent(context: Context, deepLinkResponse: DeepLinkResponse): Intent {
            val intent = Intent(context, SearchActivity::class.java)
            intent.putExtra(EXTRA_DEEP_LINK_RESPONSE, deepLinkResponse)
            return intent
        }
    }

    private val binding by lazy { ActivitySearchBinding.inflate(layoutInflater) }
    private val pagerAdapter by lazy {
        BaseViewPagerAdapter(supportFragmentManager, lifecycle)
    }
    private val viewModel: SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        viewModel.searchPageData(true)
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
                    binding.root.transitionToEnd()
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
                if (it.isNullOrEmpty()) {
                    binding.root.transitionToStart()
                } else {
                    binding.root.transitionToEnd()
                }
            }
        }

        binding.clearSearch.setOnClickListener {
            binding.root.transitionToStart()
            binding.searchEt.text?.clear()
            binding.searchEt.clearFocus()
            hideKeyboard()
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
        viewModel.searchPageData.observe(this) {
            it?.let {
                setUpTabView(it.tabsData, it.tabToShow)
            }
        }
    }

    private fun setUpTabView(tabsData: List<TabData>?, tabToShow: String?) {
        pagerAdapter.clearData()
        pagerAdapter.apply {
            tabsData?.let {
                for (item in it) {
                    when (item.tabSlug) {
                        "trending" -> {
                            addFragment(TrendingFragment(), item.tabName.toString())
                        }

                        "recommended" -> {
                            addFragment(HomeFragment(), item.tabName.toString())
                        }

                        "3" -> {
                            addFragment(HomeFragment(), item.tabName.toString())
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