package com.ourstilt.userCustomMenu.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ourstilt.databinding.ActivityCustomMenuBinding
import kotlin.math.abs

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
        viewModel.getMenusData()
        observerViewModel()
    }

    private fun setupToolbar() {
        supportActionBar?.hide()
        binding.title.text = "Your Custom Menus"

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val percentage = abs(verticalOffset).toFloat() / appBarLayout.totalScrollRange
            binding.toolbarLayout.alpha = percentage
        }
    }

    private fun observerViewModel() {
        viewModel.customMenuData.observe(this) {
            it?.let {
                customMenuAdapter.submitList(it)
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