package com.ourstilt.userCustomMenu.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ourstilt.databinding.CustomMenuItemsBinding
import com.ourstilt.userCustomMenu.data.CustomMenus

class CustomMenuAdapter(
    private val homeViewModel: CustomMenuViewModel, private val screenName: String
) : ListAdapter<CustomMenus, CustomMenuAdapter.CustomMenuViewHolder>(CustomMenuDiffCallback()) {

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): CustomMenuViewHolder {
        return CustomMenuViewHolder(
            CustomMenuItemsBinding.inflate(
                LayoutInflater.from(viewGroup.context), viewGroup, false
            )
        )
    }

    override fun onBindViewHolder(viewHolder: CustomMenuViewHolder, position: Int) {
        viewHolder.bindView(getItem(position))
    }

    inner class CustomMenuViewHolder(private val binding: CustomMenuItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(data: CustomMenus) {
            binding.apply {
                this.text2.text = data.menuName
            }
        }
    }
}

class CustomMenuDiffCallback : DiffUtil.ItemCallback<CustomMenus>() {
    override fun areItemsTheSame(oldItem: CustomMenus, newItem: CustomMenus): Boolean {
        return oldItem.slug == newItem.slug
    }

    override fun areContentsTheSame(oldItem: CustomMenus, newItem: CustomMenus): Boolean {
        return oldItem == newItem
    }
}



