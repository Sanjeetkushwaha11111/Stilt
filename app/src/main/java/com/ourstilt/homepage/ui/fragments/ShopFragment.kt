package com.ourstilt.homepage.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ourstilt.base.ui.BaseFragment
import com.ourstilt.databinding.FragmentShopBinding
import com.ourstilt.homepage.ui.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ShopFragment : BaseFragment() {

    private lateinit var binding: FragmentShopBinding

    private val homeViewModel: HomeViewModel by viewModels()

    private val shopListAdapter: ShopListAdapter by lazy {
        ShopListAdapter(homeViewModel, screenName)
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
