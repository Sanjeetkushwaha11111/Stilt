package com.ourstilt.search.ui.trendingfragment

import android.graphics.Color
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ourstilt.databinding.TrendingSectionItemBinding
import com.ourstilt.search.data.SectionType
import com.ourstilt.search.data.Section
import timber.log.Timber

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
