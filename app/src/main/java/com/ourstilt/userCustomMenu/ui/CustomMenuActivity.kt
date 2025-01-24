package com.ourstilt.userCustomMenu.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ourstilt.common.vibrateOnClick
import com.ourstilt.databinding.ActivityCustomMenuBinding
import com.ourstilt.deeplink.DeepLinkResponse
import com.ourstilt.userCustomMenu.data.CustomMenus
import com.simform.refresh.SSPullToRefreshLayout
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import kotlin.math.abs

@AndroidEntryPoint
class CustomMenuActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_DEEP_LINK_RESPONSE = "extra_deep_link_response"
        fun newIntent(context: Context, deepLinkResponse: DeepLinkResponse): Intent {
            val intent = Intent(context, CustomMenuActivity::class.java)
            intent.putExtra(EXTRA_DEEP_LINK_RESPONSE, deepLinkResponse)
            return intent
        }
    }
    private var deepLinkResponse: DeepLinkResponse? = null

    private val binding by lazy { ActivityCustomMenuBinding.inflate(layoutInflater) }

    private val viewModel: CustomMenuViewModel by viewModels()

    private val customMenuAdapter by lazy {
        CustomMenuAdapter(viewModel) {
            placeOrder(customMenu = it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        getDeepLinkDataIfAvail()
        setupToolbar()
        setupPullToRefresh()
        setupRecyclerView()
        clickListeners()
        viewModel.getMenuPageData()
        observerViewModel()
    }

    private fun getDeepLinkDataIfAvail() {
        deepLinkResponse = intent.getParcelableExtra(EXTRA_DEEP_LINK_RESPONSE)
        deepLinkResponse?.let {
            Timber.e(">>>>>>>>>Deep link response received in CustomMenuActivity: $it")
        }
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
        viewModel.pageReLoading.observe(this) {
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

    private fun placeOrder(customMenu: CustomMenus) {
        viewModel.orderFood(customMenu)
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = customMenuAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }
}