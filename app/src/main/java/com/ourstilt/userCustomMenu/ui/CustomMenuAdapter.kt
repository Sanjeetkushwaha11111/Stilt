package com.ourstilt.userCustomMenu.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ourstilt.R
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
        private var isExpanded = false
        private var initialHeight = 0

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
                menuPrice.text = "₹${menu.menuTotalPrice}"

                // Reset states
                isExpanded = false
                expandIcon.rotation = 0f
                menuItemsRecyclerView.visibility = View.GONE
                divider.alpha = 0f

                menuItemsAdapter.submitList(menu.menuItems)

                expandIcon.setOnClickListener {
                    isExpanded = !isExpanded
                    animateDropdown(isExpanded)
                }

                // Pre-measure RecyclerView height
                menuItemsRecyclerView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        menuItemsRecyclerView.viewTreeObserver.removeOnPreDrawListener(this)
                        initialHeight = menuItemsRecyclerView.measuredHeight
                        menuItemsRecyclerView.layoutParams.height = 0
                        return true
                    }
                })
            }
        }

        private fun animateDropdown(expand: Boolean) {
            binding.apply {
                val valueAnimator = if (expand) {
                    menuItemsRecyclerView.visibility = View.VISIBLE
                    ValueAnimator.ofInt(0, initialHeight)
                } else {
                    ValueAnimator.ofInt(initialHeight, 0)
                }

                valueAnimator.addUpdateListener { animator ->
                    menuItemsRecyclerView.layoutParams.height = animator.animatedValue as Int
                    menuItemsRecyclerView.requestLayout()
                }

                valueAnimator.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        if (!expand) {
                            menuItemsRecyclerView.visibility = View.GONE
                        }
                    }
                })

                // Rotate icon animation
                val rotateAnimator = if (expand) {
                    ValueAnimator.ofFloat(0f, 180f)
                } else {
                    ValueAnimator.ofFloat(180f, 0f)
                }

                rotateAnimator.addUpdateListener { animator ->
                    expandIcon.rotation = animator.animatedValue as Float
                }

                // Divider fade animation
                val dividerAnimator = if (expand) {
                    ValueAnimator.ofFloat(0f, 1f)
                } else {
                    ValueAnimator.ofFloat(1f, 0f)
                }

                dividerAnimator.addUpdateListener { animator ->
                    divider.alpha = animator.animatedValue as Float
                }

                // Create animation set
                val animatorSet = AnimatorSet().apply {
                    playTogether(valueAnimator, rotateAnimator, dividerAnimator)
                    duration = 300
                    interpolator = OvershootInterpolator(0.8f)
                }

                // Start animations
                animatorSet.start()
            }
        }
    }

    // DiffCallback remains the same
    class CustomMenuDiffCallback : DiffUtil.ItemCallback<CustomMenus>() {
        override fun areItemsTheSame(oldItem: CustomMenus, newItem: CustomMenus): Boolean {
            return oldItem.slug == newItem.slug
        }

        override fun areContentsTheSame(oldItem: CustomMenus, newItem: CustomMenus): Boolean {
            return oldItem == newItem
        }
    }
}