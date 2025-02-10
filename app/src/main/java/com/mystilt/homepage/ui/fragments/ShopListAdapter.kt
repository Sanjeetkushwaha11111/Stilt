package com.mystilt.homepage.ui.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mystilt.common.show
import com.mystilt.databinding.ShopItemBinding
import com.mystilt.homepage.data.Review
import com.mystilt.homepage.data.Shop
import com.mystilt.homepage.ui.HomeViewModel

class ShopListAdapter(
    private val viewModel: HomeViewModel, private val screenName: String
) : ListAdapter<Shop, ShopListAdapter.ShopListViewHolder>(CustomShopDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopListViewHolder {
        return ShopListViewHolder(
            ShopItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ShopListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ShopListViewHolder(private val binding: ShopItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.shopBottomRv.apply {
                setAdapter(ShopItemBottomRvAdapter())
                setAutoScrollDelay(2000)
                isHalfVisible(false)
                autoScrollEnabled(true)
                show()
            }
        }

        fun bind(shop: Shop) {
            binding.shopBottomRv.setItems(shop.reviews as ArrayList<Review>,true, showDots = false)
            binding.apply {
                shopName.text = shop.name
            }
        }
    }
}

class CustomShopDiffCallback : DiffUtil.ItemCallback<Shop>() {
    override fun areItemsTheSame(oldItem: Shop, newItem: Shop): Boolean {
        return oldItem.slug == newItem.slug
    }

    override fun areContentsTheSame(oldItem: Shop, newItem: Shop): Boolean {
        return oldItem == newItem
    }
}