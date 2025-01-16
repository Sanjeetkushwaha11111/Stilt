package com.ourstilt.search.ui.trendingfragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.ourstilt.databinding.TrendingSectionItemBinding
import com.ourstilt.search.data.TrendingSection


class TrendingSectionAdapter :
    ListAdapter<TrendingSection, TrendingSectionViewHolder>(SectionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingSectionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = TrendingSectionItemBinding.inflate(inflater, parent, false)
        return TrendingSectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrendingSectionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class SectionDiffCallback : DiffUtil.ItemCallback<TrendingSection>() {
    override fun areItemsTheSame(oldItem: TrendingSection, newItem: TrendingSection) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: TrendingSection, newItem: TrendingSection) =
        oldItem == newItem
}