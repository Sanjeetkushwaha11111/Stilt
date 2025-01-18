package com.ourstilt.userCustomMenu.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ourstilt.common.animateTextChangeIfDifferent
import com.ourstilt.databinding.MenuSubItemBinding
import com.ourstilt.userCustomMenu.data.CustomMenus
import com.ourstilt.userCustomMenu.data.MenuItems

class MenuSubItemAdapter(
    private val viewModel: CustomMenuViewModel, private val menu: CustomMenus
) : ListAdapter<MenuItems, MenuSubItemAdapter.MenuItemViewHolder>(MenuItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemViewHolder {
        return MenuItemViewHolder(
            MenuSubItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MenuItemViewHolder, position: Int) {
        holder.bind(getItem(position), viewModel, menu)
    }

    class MenuItemViewHolder(private val binding: MenuSubItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: MenuItems, viewModel: CustomMenuViewModel, menu: CustomMenus) {
            binding.apply {
                name.text = item.foodName
                description.text = "₹${item.foodPrice} - ${item.foodDescription}"

                viewModel.menuStates.observe(binding.root.context as LifecycleOwner) { states ->
                    val state = states?.get(menu.slug.toString())
                    val newItemCount = state?.itemCounts?.get(item.slug)?.toString() ?: "0"
                    itemCount.animateTextChangeIfDifferent(itemCount.text.toString(), newItemCount,50,false)
                }

                addItem.setOnClickListener {
                    viewModel.updateItemCount(menu.slug!!, item.slug!!, 1)
                }

                minusItem.setOnClickListener {
                    viewModel.updateItemCount(menu.slug!!, item.slug!!, -1)
                }
            }
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
