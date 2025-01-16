package com.ourstilt.search.ui.trendingfragment

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ourstilt.databinding.TrendingSectionItemBinding
import com.ourstilt.search.data.SectionType
import com.ourstilt.search.data.TrendingSection
import timber.log.Timber


class TrendingSectionViewHolder(private val binding: TrendingSectionItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(section: TrendingSection) {
        binding.tvSectionTitle.text = section.title
        Timber.e(">>>>>>>>>>>>>> in TrendingSectionViewHolder ${section.title}")

        val itemAdapter = when (section.type) {
            SectionType.CurrentlyTrending -> ItemAdapter(CurrentlyTrendingViewHolder::class.java)
            SectionType.MostOrdered -> ItemAdapter(MostOrderedViewHolder::class.java)
            null -> TODO()
        }
        binding.rvSectionItems.adapter = itemAdapter
        binding.rvSectionItems.layoutManager = LinearLayoutManager(
            binding.root.context, LinearLayoutManager.HORIZONTAL, false
        )
        itemAdapter.submitList(section.items)
    }
}
