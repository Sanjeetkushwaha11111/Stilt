package com.ourstilt.homepage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.ourstilt.base.ui.BaseFragment
import com.ourstilt.databinding.FragmentHomeBinding


class HomeFragment : BaseFragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private val homeViewModel by lazy {
        HomeViewModel()
    }


    private val homeAdapter by lazy {
        HomeAllFragmentsAdapter(childFragmentManager, mutableListOf(), homeViewModel, "Home")
    }

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.getHomeFragmentData()
        setAdapter()
        viewModelObserver()
    }

    private fun viewModelObserver() {
        homeViewModel.homeFragmentData.observe(viewLifecycleOwner) { homeDataModel ->
            homeDataModel.data?.let {
                homeAdapter.updatePageData(it)
            }
        }
    }


    private fun setAdapter() {
        binding.rvHomeFragment.apply {
            adapter = homeAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
}