package com.ourstilt.homepage.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ourstilt.base.ui.BaseFragment
import com.ourstilt.databinding.FragmentHomeBinding
import com.ourstilt.homepage.ui.HomeAllFragmentsAdapter
import com.ourstilt.homepage.ui.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private val homeViewModel: HomeViewModel by viewModels()


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
        setAdapter()
    }


    private fun setAdapter() {
        binding.rvHomeFragment.apply {
            adapter = homeAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
}