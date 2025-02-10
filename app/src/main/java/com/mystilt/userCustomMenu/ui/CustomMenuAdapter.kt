// CustomMenuAdapter.kt
package com.mystilt.userCustomMenu.ui

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.core.animation.addListener
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mystilt.common.animateTextChangeIfDifferent
import com.mystilt.databinding.CustomMenuItemsBinding
import com.mystilt.userCustomMenu.data.CustomMenus

class CustomMenuAdapter(
    private val viewModel: CustomMenuViewModel,
    private val orderMenuClicked: (CustomMenus) -> Unit
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


        private var initialHeight = 0
        private var isExpanded = false
        private var currentAnimator: ValueAnimator? = null
        private var currentMenuSlug: String? = null

        init {
            binding.menuItemsRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                isNestedScrollingEnabled = false
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(menu: CustomMenus) {
            currentMenuSlug = menu.slug
            val menuItemsAdapter = menu.slug?.let {
                MenuSubItemAdapter(
                    viewModel,
                    it
                )
            }
            binding.menuItemsRecyclerView.adapter = menuItemsAdapter

            binding.apply {
                root.alpha = 1f
                root.translationX = 0f
                menuItemsRecyclerView.visibility = if (isExpanded) View.VISIBLE else View.GONE
                menuName.text = menu.menuName
                description.text = menu.menuDescription
                viewModel.menuStates.observe(binding.root.context as LifecycleOwner) { states ->
                    if (currentMenuSlug == menu.slug) {
                        val state = states?.get(menu.slug.toString())
                        val newPrice = "₹${state?.totalPrice?.toInt() ?: 0}"
                        priceMain.animateTextChangeIfDifferent(
                            priceMain.text.toString(),
                            newPrice,
                            300,
                            true
                        )
                    }
                }
                menuItemsAdapter?.submitList(menu.menuItems) {
                    adjustHeightOnDataChange()
                }

                expandedMenu.setOnClickListener {
                    currentAnimator?.cancel()
                    isExpanded = !isExpanded
                    animateExpandCollapse(isExpanded)
                }
                binding.orderNow.apply {
                    setOnClickListener {
                        triggerOrdering(menu.slug.toString())
                    }
                }
            }
        }

        private fun triggerOrdering(menuSlug: String) {
            viewModel.getMenuBySlug(menuSlug)?.let { customMenus ->
                orderMenuClicked(customMenus)
            }
        }

        private fun adjustHeightOnDataChange() {
            binding.menuItemsRecyclerView.post {
                val newHeight = measureRecyclerViewHeight()
                if (isExpanded) {
                    val heightDifference = newHeight - initialHeight
                    adjustCardHeight(heightDifference)
                }
                initialHeight = newHeight
            }
        }

        private fun measureRecyclerViewHeight(): Int {
            binding.menuItemsRecyclerView.measure(
                View.MeasureSpec.makeMeasureSpec(binding.root.width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            return binding.menuItemsRecyclerView.measuredHeight
        }

        private fun adjustCardHeight(heightDifference: Int) {
            val cardView = binding.root
            ValueAnimator.ofInt(cardView.height, cardView.height + heightDifference).apply {
                addUpdateListener { animator ->
                    cardView.layoutParams = cardView.layoutParams.apply {
                        height = animator.animatedValue as Int
                    }
                }
                duration = 300
                interpolator = AccelerateDecelerateInterpolator()
                start()
            }
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