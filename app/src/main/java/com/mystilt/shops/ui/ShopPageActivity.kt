package com.mystilt.shops.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.mystilt.databinding.ActivityShopPageBinding
import com.mystilt.deeplink.DeepLinkResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs

@AndroidEntryPoint
class ShopPageActivity : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context, deepLinkResponse: DeepLinkResponse? = null): Intent {
            return Intent(context, ShopPageActivity::class.java)
        }
    }

    private val appBarOffsetListener =
        AppBarLayout.OnOffsetChangedListener() { appBarLayout, verticalOffset ->
            val percentage = abs(verticalOffset).toFloat() / appBarLayout.totalScrollRange
            binding.toolbarLayout.alpha = percentage
        }
    private var shopPageData: ShopPageModel? = null
    private val menuCategoryAdapter = MenuCategoryAdapter()
    private val shopsViewModel: ShopsViewModel by viewModels()

    private var deepLinkResponse: DeepLinkResponse? = null

    private val binding by lazy { ActivityShopPageBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        setupToolbar()
        setupMenuRecyclerView()
        setupPullToRefresh()
        shopsViewModel.getShopPageData(true)
        setupObservers()
    }

    private fun setupObservers() {
        shopsViewModel.shopPageData.observe(this) { shopPageData ->
            shopPageData?.let {
                bindData(it)
            }
        }
    }


    private fun setupMenuRecyclerView() {
        binding.menuRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = menuCategoryAdapter
        }

        menuCategoryAdapter.setOnItemClickListener { menuItem ->
            handleMenuItemClick(menuItem)
        }
    }

    private fun bindData(shopPageData: ShopPageModel) {
        shopPageData.let { data ->
            binding.apply {
                shopName.text = data.name
                shopDescription.text = data.cuisineTypes?.joinToString(", ")
                shopRating.text = data.rating?.toString() ?: "-"
            }

            data.menu?.categories?.let { categories ->
                menuCategoryAdapter.setData(categories)
            }
        }
    }

    private fun handleMenuItemClick(menuItem: MenuItem) {

    }

    private fun setupPullToRefresh() {
        binding.pullToRefresh.setOnRefreshListener {
            // Implement your refresh logic here
            binding.pullToRefresh.setRefreshing(false)
        }
    }


    private fun setupToolbar() {
        binding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val percentage = abs(verticalOffset).toFloat() / appBarLayout.totalScrollRange
            binding.toolbarLayout.alpha = percentage
        }
    }
}