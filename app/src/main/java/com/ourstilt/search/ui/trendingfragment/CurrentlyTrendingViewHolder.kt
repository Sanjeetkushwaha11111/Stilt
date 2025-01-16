package com.ourstilt.search.ui.trendingfragment

import androidx.recyclerview.widget.RecyclerView
import com.ourstilt.databinding.CurrentlyTrendingItemBinding
import com.ourstilt.search.data.TrendingItem

class CurrentlyTrendingViewHolder(private val binding: CurrentlyTrendingItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: TrendingItem) {
        binding.tvItemTitle.text = item.title
        binding.tvItemType.text = "Type: Currently Trending"
    }
}