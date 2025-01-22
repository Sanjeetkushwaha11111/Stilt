package com.ourstilt.userCustomMenu.ui

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.addListener
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ourstilt.common.animateTextChangeIfDifferent
import com.ourstilt.common.hide
import com.ourstilt.common.show
import com.ourstilt.databinding.MenuSubItemBinding
import com.ourstilt.userCustomMenu.data.CustomMenus
import com.ourstilt.userCustomMenu.data.MenuItems

class MenuSubItemAdapter(
    private val viewModel: CustomMenuViewModel,
    private val menuSlug: String
) : ListAdapter<MenuItems, MenuSubItemAdapter.MenuItemViewHolder>(MenuItemDiffCallback()) {
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

    inner class MenuItemViewHolder(private val binding: MenuSubItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val pendingRemovals = mutableSetOf<String>()
        private var removalAnimator: ValueAnimator? = null
        private var currentItemSlug: String? = null

        @SuppressLint("SetTextI18n")
        fun bind(item: MenuItems) {
            currentItemSlug = item.itemSlug
            binding.apply {
                removalAnimator?.cancel()
                root.translationX = 0f
                root.alpha = 1f
                name.text = item.foodName
                description.text = "₹${item.foodPrice.toInt()} - ${item.foodDescription}"
                viewModel.menuStates.observe(binding.root.context as LifecycleOwner) { states ->
                    // Only update if this is still the same menu and item
                    if (currentItemSlug == item.itemSlug) {
                        val state = states?.get(menuSlug)
                        val newItemCount = state?.itemCounts?.get(item.itemSlug)?.toString() ?: "0"

                        if (!pendingRemovals.contains(item.itemSlug)) {
                            itemCount.animateTextChangeIfDifferent(
                                itemCount.text.toString(),
                                newItemCount,
                                50,
                                false
                            )

                            motionLayout.apply {
                                if (newItemCount == "0") {
                                    itemTopText.show()
                                    transitionToEnd()
                                } else {
                                    itemTopText.hide()
                                    transitionToStart()
                                }
                            }
                        }
                    }
                }

                addItem.setOnClickListener {
                    menuSlug.let { menuSlug ->
                        item.itemSlug?.let { itemSlug ->
                            viewModel.updateItemCount(
                                menuSlug, itemSlug, 1
                            )
                        }
                    }
                }

                minusItem.setOnClickListener {
                    menuSlug.let { menuSlug ->
                        item.itemSlug?.let { itemSlug ->
                            viewModel.updateItemCount(
                                menuSlug, itemSlug, -1
                            )
                        }
                    }
                }

                removeItem.setOnClickListener {
                    item.itemSlug?.let { slug ->
                        if (pendingRemovals.contains(slug)) return@setOnClickListener
                        pendingRemovals.add(slug)
                        animateRemoval {
                            menuSlug.let { menuSlug -> viewModel.removeMenuItem(menuSlug, slug) }
                        }
                    }
                }
            }
        }

        private fun animateRemoval(onAnimationEnd: () -> Unit) {
            val width = binding.root.width.toFloat()
            removalAnimator = ValueAnimator.ofFloat(0f, width).apply {
                duration = 300
                interpolator = AccelerateDecelerateInterpolator()
                addUpdateListener { animator ->
                    binding.root.translationX = -(animator.animatedValue as Float)
                    binding.root.alpha = 1 - (animator.animatedValue as Float) / width
                }
                addListener(onEnd = {
                    onAnimationEnd()
                })
                start()
            }
        }

    }
}

class MenuItemDiffCallback : DiffUtil.ItemCallback<MenuItems>() {
    override fun areItemsTheSame(oldItem: MenuItems, newItem: MenuItems): Boolean {
        return oldItem.itemSlug == newItem.itemSlug
    }

    override fun areContentsTheSame(oldItem: MenuItems, newItem: MenuItems): Boolean {
        return oldItem == newItem
    }
}
