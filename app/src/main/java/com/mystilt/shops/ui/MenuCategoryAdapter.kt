package com.mystilt.shops.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mystilt.databinding.MenuCategoryLayoutBinding
import timber.log.Timber


class MenuCategoryAdapter : RecyclerView.Adapter<MenuCategoryAdapter.CategoryViewHolder>() {
    private var categories: List<MenuCategory> = emptyList()
    private var onItemClickListener: ((MenuItem) -> Unit)? = null

    fun setData(newCategories: List<MenuCategory>) {
        categories = newCategories
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: (MenuItem) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = MenuCategoryLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size

    inner class CategoryViewHolder(
        private val binding: MenuCategoryLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val subCategoryAdapter = MenuSubCategoryAdapter()
        private val menuItemAdapter = MenuItemAdapter()

        init {
            binding.subCategoriesRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = subCategoryAdapter
            }

            binding.itemsRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = menuItemAdapter
            }

            menuItemAdapter.setOnItemClickListener { menuItem ->
                onItemClickListener?.invoke(menuItem)
            }

            subCategoryAdapter.setOnItemClickListener { menuItem ->
                onItemClickListener?.invoke(menuItem)
            }
        }

        fun bind(category: MenuCategory) {
            binding.categoryName.text = category.name
            Timber.e(">>>>>>>>>>>>1")
            val orientation = if (category.viewOrientation == "horizontal") {
                Timber.e(">>>>>>>>>>>>2")
                LinearLayoutManager.HORIZONTAL
            } else {
                Timber.e(">>>>>>>>>>>>3")
                LinearLayoutManager.VERTICAL
            }
            binding.subCategoriesRecyclerView.layoutManager = LinearLayoutManager(binding.root.context, orientation, false)
            binding.itemsRecyclerView.layoutManager = LinearLayoutManager(binding.root.context, orientation, false)

            // Set up click listener for expand/collapse
            binding.root.setOnClickListener {
                category.expanded = !(category.expanded ?: false)
                toggleExpansion(category)
            }

            // Initial state
            toggleExpansion(category)
        }

        private fun toggleExpansion(category: MenuCategory) {
            val isExpanded = category.expanded ?: false

            // Animate rotation of expand icon
            binding.expandIcon.animate()
                .rotation(if (isExpanded) 180f else 0f)
                .setDuration(300)
                .start()

            // Show/hide content with animation
            if (category.hasSubcategories == true) {
                binding.subCategoriesRecyclerView.visibility = if (isExpanded) View.VISIBLE else View.GONE
                if (isExpanded) {
                    subCategoryAdapter.setData(category.subcategories ?: emptyList())
                }
            } else {
                binding.itemsRecyclerView.visibility = if (isExpanded) View.VISIBLE else View.GONE
                if (isExpanded) {
                    menuItemAdapter.setData(category.items ?: emptyList())
                }
            }
        }
    }
}


