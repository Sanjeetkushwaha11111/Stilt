package com.mystilt.homepage.ui

import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.mystilt.databinding.ItemHomeAllFragmentsBinding
import com.mystilt.homepage.data.ApiData

class HomeAllFragmentsViewHolder(
    private var item: ItemHomeAllFragmentsBinding, private val viewModel: HomeViewModel
) : RecyclerView.ViewHolder(item.root) {
    fun bindTo(data: List<ApiData>, fragmentManager: FragmentManager, screenName: String) {

    }
}