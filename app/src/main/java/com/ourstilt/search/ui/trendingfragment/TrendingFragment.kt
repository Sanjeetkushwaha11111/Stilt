package com.ourstilt.search.ui.trendingfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ourstilt.base.ui.BaseFragment
import com.ourstilt.databinding.FragmentTrendingBinding
import com.ourstilt.search.ui.SearchViewModel

class TrendingFragment : BaseFragment() {

    private lateinit var binding: FragmentTrendingBinding

    private val viewModel: SearchViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrendingBinding.inflate(inflater, container, false)
        setupRecyclerView()
        viewModelObserver()
        viewModel.fetchTrendingSections()
        return binding.root
    }

    private fun viewModelObserver() {
        viewModel.trendingSections.observe(viewLifecycleOwner) { trendingSections ->
            trendingSections?.let {
                (binding.rvTrendingSections.adapter as TrendingSectionAdapter).submitList(
                    trendingSections
                )
            }
        }
    }

    private fun setupRecyclerView() {
        val adapter = TrendingSectionAdapter()
        binding.rvTrendingSections.adapter = adapter
        binding.rvTrendingSections.layoutManager = LinearLayoutManager(context)
    }


}