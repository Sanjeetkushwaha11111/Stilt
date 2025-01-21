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
import timber.log.Timber

class MenuSubItemAdapter(
    private val viewModel: CustomMenuViewModel,
    private val menu: CustomMenus
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

        @SuppressLint("SetTextI18n")
        fun bind(item: MenuItems) {
            binding.apply {
                removalAnimator?.cancel()
                root.translationX = 0f
                root.alpha = 1f

                name.text = item.foodName
                description.text = "₹${item.foodPrice} - ${item.foodDescription}"

                viewModel.menuStates.observe(binding.root.context as LifecycleOwner) { states ->
                    val state = states?.get(menu.slug.toString())
                    val newItemCount = state?.itemCounts?.get(item.itemSlug)?.toString() ?: "0"
                    itemCount.animateTextChangeIfDifferent(itemCount.text.toString(), newItemCount,50,false)
                    if (!pendingRemovals.contains(item.itemSlug)) {
                        itemCount.animateTextChangeIfDifferent(
                            itemCount.text.toString(), newItemCount, 50, false
                        )
                        motionLayout.apply {
                            if (newItemCount == "0") {
                                binding.itemTopText.show()
                                transitionToEnd()
                            } else {
                                binding.itemTopText.hide()
                                transitionToStart()
                            }
                        }
                    }
                }

                addItem.setOnClickListener {
                    viewModel.updateItemCount(menu.slug!!, item.itemSlug!!, 1)
                }

                minusItem.setOnClickListener {
                    viewModel.updateItemCount(menu.slug!!, item.itemSlug!!, -1)
                }

                removeItem.setOnClickListener {
                    if (pendingRemovals.contains(item.itemSlug)) return@setOnClickListener
                    item.itemSlug?.let { slug ->
                        pendingRemovals.add(slug)
                        animateRemoval {
                            viewModel.removeMenuItem(menu.slug!!, item.itemSlug!!)
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
