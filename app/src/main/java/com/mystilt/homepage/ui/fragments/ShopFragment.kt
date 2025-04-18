package com.mystilt.homepage.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mystilt.base.ui.BaseFragment
import com.mystilt.databinding.FragmentShopBinding
import com.mystilt.homepage.data.Shop
import com.mystilt.homepage.ui.HomeViewModel
import com.mystilt.shops.ui.ShopPageActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ShopFragment : BaseFragment() {

    private lateinit var binding: FragmentShopBinding

    private val homeViewModel: HomeViewModel by viewModels()

    private val shopListAdapter: ShopListAdapter by lazy {
        ShopListAdapter(homeViewModel, screenName) {
            openShopPage(shop = it)
        }
    }

    private fun openShopPage(shop: Shop) {
        val intent = Intent(requireContext(), ShopPageActivity::class.java)
        startActivity(intent)

    }

    private val screenName = "ShopItems"

    companion object {
        fun newInstance() = ShopFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentShopBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.getShopsListData(true)
        setUpRecyclerView()
        observeShopList()
    }

    private fun setUpRecyclerView() {
        binding.rvShopFragment.apply {
            adapter = shopListAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
        }
    }

    private fun observeShopList() {
        homeViewModel.shopPageData.observe(viewLifecycleOwner) { shopPageData ->
            shopPageData.shops?.let {
                shopListAdapter.submitList(it)
            }
        }
    }
}
