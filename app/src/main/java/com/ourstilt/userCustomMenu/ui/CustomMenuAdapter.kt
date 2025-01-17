// CustomMenuAdapter.kt
package com.ourstilt.userCustomMenu.ui

import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.core.animation.addListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ourstilt.databinding.CustomMenuItemsBinding
import com.ourstilt.userCustomMenu.data.CustomMenus

class CustomMenuAdapter(
    private val viewModel: CustomMenuViewModel
) : ListAdapter<CustomMenus, CustomMenuAdapter.CustomMenuViewHolder>(CustomMenuDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomMenuViewHolder {
        return CustomMenuViewHolder(
            CustomMenuItemsBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: CustomMenuViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CustomMenuViewHolder(private val binding: CustomMenuItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val menuItemsAdapter = MenuSubItemAdapter()
        private var initialHeight = 0
        private var isExpanded = false
        private var currentAnimator: ValueAnimator? = null

        init {
            binding.menuItemsRecyclerView.apply {
                adapter = menuItemsAdapter
                layoutManager = LinearLayoutManager(context)
                isNestedScrollingEnabled = false
            }
        }

        fun bind(menu: CustomMenus) {
            binding.apply {
                menuName.text = menu.menuName
                priceMain.text = "₹${menu.menuTotalPrice}"
                menuItemsAdapter.submitList(menu.menuItems)

                if (initialHeight == 0) {
                    menuItemsRecyclerView.post {
                        initialHeight = measureRecyclerViewHeight()
                    }
                }

                binding.expandedMenu.setOnClickListener {
                    currentAnimator?.cancel()
                    isExpanded = !isExpanded
                    animateExpandCollapse(isExpanded)
                }
            }
        }

        private fun measureRecyclerViewHeight(): Int {
            binding.menuItemsRecyclerView.measure(
                View.MeasureSpec.makeMeasureSpec(binding.root.width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            return binding.menuItemsRecyclerView.measuredHeight
        }
        private fun animateExpandCollapse(expand: Boolean) {
            val rotationStart = if (expand) 0f else 180f
            val rotationEnd = if (expand) 180f else 0f

            binding.apply {
                val cardView = root
                val originalHeight = cardView.height
                val targetHeight = originalHeight + (if (expand) initialHeight else -initialHeight)

                if (expand) {
                    menuItemsRecyclerView.visibility = View.VISIBLE
                }

                ValueAnimator.ofInt(originalHeight, targetHeight).apply {
                    addUpdateListener { animator ->
                        cardView.layoutParams = cardView.layoutParams.apply {
                            height = animator.animatedValue as Int
                        }
                    }
                    addListener(onEnd = {
                        if (!expand) {
                            menuItemsRecyclerView.visibility = View.GONE
                        }
                    })
                    duration = 300
                    interpolator = OvershootInterpolator(0.8f)
                    currentAnimator = this
                    start()
                }
                ValueAnimator.ofFloat(rotationStart, rotationEnd).apply {
                    addUpdateListener { animator ->
                        binding.expandedMenu.rotation = animator.animatedValue as Float
                    }
                    duration = 200
                    interpolator = AccelerateDecelerateInterpolator()
                    start()
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
}