package com.mystilt.homepage.ui.fragments

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mystilt.customViews.autoscrollrecyclerview.CircularAdapter
import com.mystilt.databinding.ShopBottomItemBinding
import com.mystilt.homepage.data.Review

class ShopItemBottomRvAdapter() : CircularAdapter<Review>() {
    private lateinit var context: Context
    override fun createItemViewHolder(parent: ViewGroup, viewType: Int): ShopItemBottomViewModel {
        context = parent.context
        return ShopItemBottomViewModel(
            ShopBottomItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun bindItemViewHolder(
        holder: RecyclerView.ViewHolder, item: Review, actualPosition: Int, position: Int
    ) {
        if(holder is ShopItemBottomViewModel){
            holder.binding.apply {
                itemTextview.text = item.comment
            }
        }
    }

    inner class ShopItemBottomViewModel(var binding: ShopBottomItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}