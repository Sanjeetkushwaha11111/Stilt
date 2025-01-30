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
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class TrendingFragment : BaseFragment() {

    private lateinit var binding: FragmentTrendingBinding

    private val viewModel: SearchViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrendingBinding.inflate(inflater, container, false)
        setupRecyclerView()
        viewModelObserver()
        viewModel.getTrendingPageData(true)
        return binding.root
    }

    private fun viewModelObserver() {
        viewModel.trendingPageData.observe(viewLifecycleOwner) { trendingPageData ->
            trendingPageData?.let {
                (binding.rvTrendingSections.adapter as TrendingSectionAdapter).submitList(
                    trendingPageData.trendingSections
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