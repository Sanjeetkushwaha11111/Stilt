package com.mystilt.shops.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mystilt.databinding.MenuItemLayoutType1RectBinding

class MenuItemAdapter : RecyclerView.Adapter<MenuItemAdapter.MenuItemViewHolder>() {
    private var items: List<MenuItem> = emptyList()
    private var onItemClickListener: ((MenuItem) -> Unit)? = null

    fun setData(newItems: List<MenuItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: (MenuItem) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemViewHolder {
        val binding = MenuItemLayoutType1RectBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MenuItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun setRecyclerViewOrientation(recyclerView: RecyclerView, orientation: String?) {
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context,
            if (orientation == "horizontal") LinearLayoutManager.HORIZONTAL else LinearLayoutManager.VERTICAL,
            false
        )
    }

    inner class MenuItemViewHolder(
        private val binding: MenuItemLayoutType1RectBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MenuItem) {
//            binding.apply {
//                itemName.text = item.name
//                itemDescription.text = item.description
//                itemPrice.text = "₹ ${item.price}"
//
//                // Glide.with(itemImage).load(item.imageUrl).into(itemImage)
//
//                addButton.setOnClickListener {
//                    onItemClickListener?.invoke(item)
//                }
//            }
        }
    }
}