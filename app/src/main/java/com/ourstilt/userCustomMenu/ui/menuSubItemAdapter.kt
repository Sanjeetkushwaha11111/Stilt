package com.ourstilt.userCustomMenu.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ourstilt.databinding.MenuSubItemBinding
import com.ourstilt.userCustomMenu.data.MenuItems

class MenuSubItemAdapter : ListAdapter<MenuItems, MenuSubItemAdapter.MenuItemViewHolder>(MenuItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemViewHolder {
        return MenuItemViewHolder(
            MenuSubItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MenuItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MenuItemViewHolder(private val binding: MenuSubItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MenuItems) {
            binding.apply {
//                itemName.text = item.foodName
//                itemDescription.text = item.foodDescription
//                itemPrice.text = "₹${item.foodPrice}"
            }
        }
    }

    class MenuItemDiffCallback : DiffUtil.ItemCallback<MenuItems>() {
        override fun areItemsTheSame(oldItem: MenuItems, newItem: MenuItems): Boolean {
            return oldItem.slug == newItem.slug
        }

        override fun areContentsTheSame(oldItem: MenuItems, newItem: MenuItems): Boolean {
            return oldItem == newItem
        }
    }
}