package com.mystilt.search.ui.trendingfragment

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mystilt.databinding.SingleItemWithNameBinding
import com.mystilt.search.data.SubSectionItem


class AllItemsViewHolder(private val binding: SingleItemWithNameBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: SubSectionItem) {
        item.let { subSection ->
            subSection.imageUrl?.let {
                Glide.with(binding.foodImg.context).load(it).into(binding.foodImg)
                binding.foodName.text = subSection.title
            }
        }
    }
}