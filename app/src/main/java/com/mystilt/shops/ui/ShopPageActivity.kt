package com.mystilt.shops.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
    private val homeViewModel: ShopsViewModel by viewModels()
    private var deepLinkResponse: DeepLinkResponse? = null

    private val binding by lazy { ActivityShopPageBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        setupToolbar()

    }

    private fun setupToolbar() {
        binding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val percentage = abs(verticalOffset).toFloat() / appBarLayout.totalScrollRange
            binding.toolbarLayout.alpha = percentage
        }
    }
}