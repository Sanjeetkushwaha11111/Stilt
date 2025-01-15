package com.ourstilt.base.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter


class BaseViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    private var fragments = arrayListOf<FragmentList>()

    fun getPageTitle(position: Int): String? {
        return if (position in 0 until fragments.size) {
            fragments[position].title
        } else {
            null
        }
    }

    fun getFragment(position: Int): Fragment? {
        return if (position in 0 until fragments.size) {
            fragments[position].fragment
        } else {
            null
        }
    }

    fun addFragment(fragment: Fragment?, title: String) {
        val fragmentList = FragmentList()
        if (fragment != null)
            fragmentList.fragment = fragment
        fragmentList.title = title
        fragments.add(fragmentList)
    }


    fun clearData() {
        fragments.clear()
    }


    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        return fragments[position].fragment

    }
}

data class FragmentList(var fragment: Fragment = Fragment(), var title: String = "")
