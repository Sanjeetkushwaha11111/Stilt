package com.ourstilt.homepage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ourstilt.databinding.HomeFilterItemBinding
import com.ourstilt.homepage.data.FilterData

class FilterAdapter(
    private var list: ArrayList<FilterData>,
    private var viewModel: HomeViewModel,
    private var screenNAme: String,
    private val listener: (Int) -> Unit
) : RecyclerView.Adapter<FilterAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(
            HomeFilterItemBinding.inflate(
                LayoutInflater.from(viewGroup.context), viewGroup, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindTo(list[position])
    }

    override fun getItemCount(): Int = list.size


    inner class ViewHolder(private var binding: HomeFilterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindTo(item: FilterData) {


        }
    }
}