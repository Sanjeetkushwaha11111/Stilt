package com.mystilt.homepage.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mystilt.R
import com.mystilt.databinding.HomeTopItemBinding
import com.mystilt.homepage.data.HomeTopItem


class HomeTopItemViewAdapter(
    private val viewModel: HomeViewModel, private val screenName: String
) : ListAdapter<HomeTopItem, HomeTopItemViewAdapter.HomeTopItemViewHolder>(CustomShopDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeTopItemViewHolder {
        return HomeTopItemViewHolder(
            HomeTopItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: HomeTopItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HomeTopItemViewHolder(private val binding: HomeTopItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(homeTopItem: HomeTopItem) {
            binding.itemImg.setImageResource(R.drawable.chai_normal)
            binding.itemText.text = "Chai"
        }
    }
}

class CustomShopDiffCallback : DiffUtil.ItemCallback<HomeTopItem>() {
    override fun areItemsTheSame(oldItem: HomeTopItem, newItem: HomeTopItem): Boolean {
        return oldItem.itemSlug == newItem.itemSlug
    }

    override fun areContentsTheSame(oldItem: HomeTopItem, newItem: HomeTopItem): Boolean {
        return oldItem == newItem
    }
}