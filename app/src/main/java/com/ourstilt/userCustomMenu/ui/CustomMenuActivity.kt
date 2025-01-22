package com.ourstilt.userCustomMenu.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ourstilt.common.vibrateOnClick
import com.ourstilt.databinding.ActivityCustomMenuBinding
import com.simform.refresh.SSPullToRefreshLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs

@AndroidEntryPoint
class CustomMenuActivity : AppCompatActivity() {

    private val binding by lazy { ActivityCustomMenuBinding.inflate(layoutInflater) }

    private val viewModel: CustomMenuViewModel by viewModels()

    private val customMenuAdapter by lazy {
        CustomMenuAdapter(viewModel)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        setupToolbar()
        setupPullToRefresh()
        setupRecyclerView()
        clickListeners()
        viewModel.getMenuPageData()
        observerViewModel()
    }

    private fun setupPullToRefresh() {
        binding.pullToRefresh.apply {
            setLottieAnimation("lottie_loader_potato.lottie")
            setRepeatCount(SSPullToRefreshLayout.RepeatCount.INFINITE)
            setRefreshStyle(SSPullToRefreshLayout.RefreshStyle.NORMAL)
            setOnRefreshListener {
                if (isEnabled) {
                    isEnabled = false
                    viewModel.getMenuPageData(true)
                }
            }
        }
    }



    private fun clickListeners() {
        binding.btnBack.setOnClickListener {
            onBackPressed()
            it.vibrateOnClick()
        }
    }

    private fun setupToolbar() {
        binding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val percentage = abs(verticalOffset).toFloat() / appBarLayout.totalScrollRange
            binding.toolbarLayout.alpha = percentage
        }
    }

    private fun observerViewModel() {
        viewModel.customMenuPageData.observe(this) {
            it?.let {
                binding.title.text = it.welcomeText
                viewModel.menuList.observe(this) { list ->
                    customMenuAdapter.submitList(list)
                }
            }
        }
        viewModel.loading.observe(this) {
            binding.pullToRefresh.apply {
                if (it) {
                    setRefreshing(true)
                    isEnabled = false
                } else {
                    setRefreshing(false )
                    isEnabled = true
                }
            }
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = customMenuAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }
}