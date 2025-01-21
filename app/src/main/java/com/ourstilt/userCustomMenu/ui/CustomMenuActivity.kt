package com.ourstilt.userCustomMenu.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ourstilt.databinding.ActivityCustomMenuBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
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
        setupRecyclerView()
        viewModel.getMenuPageData()
        observerViewModel()
    }

    private fun setupToolbar() {
        supportActionBar?.hide()

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

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
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = customMenuAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }
}