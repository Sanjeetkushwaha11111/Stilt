package com.mystilt.search.ui.trendingfragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mystilt.databinding.MostOrderedItemBinding
import com.mystilt.databinding.SingleItemWithNameBinding
import com.mystilt.search.data.SectionType
import com.mystilt.search.data.SubSectionItem

class SubSectionItemAdapter(
    private val sectionType: SectionType
) : ListAdapter<SubSectionItem, RecyclerView.ViewHolder>(SubSectionItemDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_ALL_ITEMS = 1
        private const val VIEW_TYPE_MOST_ORDERED = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (sectionType) {
            SectionType.ALL_ITEMS -> VIEW_TYPE_ALL_ITEMS
            SectionType.MOST_ORDERED -> VIEW_TYPE_MOST_ORDERED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_ALL_ITEMS -> {
                val binding = SingleItemWithNameBinding.inflate(inflater, parent, false)
                AllItemsViewHolder(binding)
            }
            VIEW_TYPE_MOST_ORDERED -> {
                val binding = MostOrderedItemBinding.inflate(inflater, parent, false)
                MostOrderedViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is AllItemsViewHolder -> holder.bind(item)
            is MostOrderedViewHolder -> holder.bind(item)
        }
    }
}
class SubSectionItemDiffCallback : DiffUtil.ItemCallback<SubSectionItem>() {
    override fun areItemsTheSame(oldItem: SubSectionItem, newItem: SubSectionItem) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: SubSectionItem, newItem: SubSectionItem) =
        oldItem == newItem
}
