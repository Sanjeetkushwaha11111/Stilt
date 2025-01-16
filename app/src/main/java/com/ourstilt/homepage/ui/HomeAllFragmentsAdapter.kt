package com.ourstilt.homepage.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.ourstilt.databinding.ItemHomeAllFragmentsBinding
import com.ourstilt.homepage.data.ApiData

class HomeAllFragmentsAdapter(
    private val manager: FragmentManager,
    var data: MutableList<ApiData>,
    private val viewModel: HomeViewModel,
    private val screenName: String,
) : RecyclerView.Adapter<HomeAllFragmentsViewHolder>() {


    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        position: Int
    ): HomeAllFragmentsViewHolder {
        val itemBinding = ItemHomeAllFragmentsBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )
        return HomeAllFragmentsViewHolder(itemBinding, viewModel)
    }

    override fun onBindViewHolder(courseViewholder: HomeAllFragmentsViewHolder, position: Int) {
        courseViewholder.bindTo(data, manager, screenName)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun updateBeginData(data: MutableList<ApiData>) {
        this.data = data
        notifyDataSetChanged()
    }

    fun updatePageData(data: MutableList<ApiData>) {
        this.data.addAll(data)
        notifyDataSetChanged()
    }


    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun clearData() {
        data.clear()
        notifyDataSetChanged()
    }

}