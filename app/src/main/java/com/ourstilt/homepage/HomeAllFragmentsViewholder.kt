package com.ourstilt.homepage

import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.ourstilt.databinding.ItemHomeAllFragmentsBinding

class HomeAllFragmentsViewHolder(
    private var item: ItemHomeAllFragmentsBinding, private val viewModel: HomeViewModel
) : RecyclerView.ViewHolder(item.root) {
    fun bindTo(data: List<ApiData>, fragmentManager: FragmentManager, screenName: String) {

    }
}