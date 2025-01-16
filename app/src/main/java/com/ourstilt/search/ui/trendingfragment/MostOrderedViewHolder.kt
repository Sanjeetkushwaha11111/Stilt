package com.ourstilt.search.ui.trendingfragment

import androidx.recyclerview.widget.RecyclerView
import com.ourstilt.databinding.MostOrderedItemBinding
import com.ourstilt.search.data.TrendingItem


class MostOrderedViewHolder(private val binding: MostOrderedItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: TrendingItem) {
        binding.tvItemTitle.text = item.title
        binding.tvItemType.text = "Type: Most Ordered"
    }
}