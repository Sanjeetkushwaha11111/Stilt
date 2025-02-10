package com.mystilt.search.ui.trendingfragment

import android.graphics.Color
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mystilt.databinding.TrendingSectionItemBinding
import com.mystilt.search.data.SectionType
import com.mystilt.search.data.Section

class TrendingSectionViewHolder(private val binding: TrendingSectionItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(section: Section) {
        binding.tvSectionTitle.text = section.sectionTitle
        section.sectionBg?.let {
            binding.sectionParentCl.setBackgroundColor(Color.parseColor(it))
        }?:{
            binding.sectionParentCl.setBackgroundColor(Color.parseColor("#770000"))
        }
        val firstSubSection = section.sectionItems?.firstOrNull()

        firstSubSection?.let {
            val itemAdapter = SubSectionItemAdapter(it.sectionType ?: SectionType.ALL_ITEMS)
            binding.rvSectionItems.adapter = itemAdapter
            binding.rvSectionItems.layoutManager = LinearLayoutManager(
                binding.root.context, LinearLayoutManager.HORIZONTAL, false
            )
            itemAdapter.submitList(it.subSectionItems)
        }
    }
}
