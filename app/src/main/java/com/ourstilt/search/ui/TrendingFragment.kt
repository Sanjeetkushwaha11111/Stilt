package com.ourstilt.search.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.ourstilt.base.ui.BaseFragment
import com.ourstilt.common.loggableFormat
import com.ourstilt.databinding.FragmentTrendingBinding
import timber.log.Timber

class TrendingFragment : BaseFragment() {

    private lateinit var binding: FragmentTrendingBinding

    private val viewModel: SearchViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchTrendingSections()
        viewModelObserver()
    }

    private fun viewModelObserver() {
        viewModel.trendingSections.observe(this) { trendingSections ->
            trendingSections?.let {
                Timber.e(">>>>>>>>>>.${it.loggableFormat()}")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrendingBinding.inflate(layoutInflater)
        return binding.root
    }

}