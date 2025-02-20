package com.mystilt.shops.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mystilt.databinding.MenuCategoryLayoutBinding

class MenuSubCategoryAdapter : RecyclerView.Adapter<MenuSubCategoryAdapter.SubCategoryViewHolder>() {
    private var subCategories: List<MenuSubCategory> = emptyList()
    private var onItemClickListener: ((MenuItem) -> Unit)? = null

    fun setData(newSubCategories: List<MenuSubCategory>) {
        subCategories = newSubCategories
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: (MenuItem) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubCategoryViewHolder {
        val binding = MenuCategoryLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SubCategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubCategoryViewHolder, position: Int) {
        holder.bind(subCategories[position])
    }

    override fun getItemCount(): Int = subCategories.size

    inner class SubCategoryViewHolder(
        private val binding: MenuCategoryLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val menuItemAdapter = MenuItemAdapter()

        init {
            binding.itemsRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = menuItemAdapter
            }

            menuItemAdapter.setOnItemClickListener { menuItem ->
                onItemClickListener?.invoke(menuItem)
            }
        }

        fun bind(subCategory: MenuSubCategory) {
            binding.categoryName.text = subCategory.name
            binding.itemsRecyclerView.visibility = View.GONE
            // Set orientation dynamically
            val orientation = if (subCategory.viewOrientation == "horizontal") {
                LinearLayoutManager.HORIZONTAL
            } else {
                LinearLayoutManager.VERTICAL
            }

            binding.itemsRecyclerView.layoutManager = LinearLayoutManager(binding.root.context, orientation, false)
            binding.itemsRecyclerView.adapter = menuItemAdapter

            binding.root.setOnClickListener {
                subCategory.expanded = !(subCategory.expanded ?: false)
                toggleExpansion(subCategory)
            }

            toggleExpansion(subCategory)
        }

        private fun toggleExpansion(subCategory: MenuSubCategory) {
            val isExpanded = subCategory.expanded ?: false

            binding.expandIcon.animate()
                .rotation(if (isExpanded) 180f else 0f)
                .setDuration(300)
                .start()

            binding.itemsRecyclerView.visibility = if (isExpanded) View.VISIBLE else View.GONE
            if (isExpanded) {
                menuItemAdapter.setData(subCategory.items ?: emptyList())
            }
        }
    }
}
