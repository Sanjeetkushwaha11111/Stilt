package com.ourstilt.homepage.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ourstilt.R
import com.ourstilt.databinding.HomeTopItemBinding
import com.ourstilt.databinding.ShopItemBinding
import com.ourstilt.homepage.data.HomeTopItem
import timber.log.Timber


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
            Timber.e(">>>>>>>HomeTopItemViewHolder bind called")
            Timber.e(">>>>>HomeTopItemViewHolder bind called ${homeTopItem.itemSlug}")
            binding.itemImg.setImageResource(R.drawable.chai_normal)
            binding.itemText.text = homeTopItem.itemText
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