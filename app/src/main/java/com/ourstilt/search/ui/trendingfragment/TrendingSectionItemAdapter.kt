package com.ourstilt.search.ui.trendingfragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ourstilt.databinding.CurrentlyTrendingItemBinding
import com.ourstilt.databinding.MostOrderedItemBinding
import com.ourstilt.search.data.TrendingItem

class ItemAdapter<T : RecyclerView.ViewHolder>(
    private val viewHolderClass: Class<T>
) : ListAdapter<TrendingItem, T>(ItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): T {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewHolderClass) {
            CurrentlyTrendingViewHolder::class.java -> CurrentlyTrendingViewHolder(
                CurrentlyTrendingItemBinding.inflate(
                    inflater, parent, false
                )
            ) as T

            //dslkncd;asjknfasfadf
            MostOrderedViewHolder::class.java -> MostOrderedViewHolder(
                MostOrderedItemBinding.inflate(
                    inflater, parent, false
                )
            ) as T

            else -> throw IllegalArgumentException("Unknown ViewHolder class")
        }
    }

    override fun onBindViewHolder(holder: T, position: Int) {
        when (holder) {
            is CurrentlyTrendingViewHolder -> holder.bind(getItem(position))
            is MostOrderedViewHolder -> holder.bind(getItem(position))
        }
    }
}

class ItemDiffCallback : DiffUtil.ItemCallback<TrendingItem>() {
    override fun areItemsTheSame(oldItem: TrendingItem, newItem: TrendingItem) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: TrendingItem, newItem: TrendingItem) =
        oldItem == newItem
}
