package com.mystilt.search.ui.trendingfragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.mystilt.databinding.TrendingSectionItemBinding
import com.mystilt.search.data.Section


class TrendingSectionAdapter :
    ListAdapter<Section, TrendingSectionViewHolder>(TrendingSectionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingSectionViewHolder {
        val binding = TrendingSectionItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TrendingSectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrendingSectionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class TrendingSectionDiffCallback : DiffUtil.ItemCallback<Section>() {
    override fun areItemsTheSame(oldItem: Section, newItem: Section) =
        oldItem.sectionId == newItem.sectionId

    override fun areContentsTheSame(oldItem: Section, newItem: Section) =
        oldItem == newItem
}
