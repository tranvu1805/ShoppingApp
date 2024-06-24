package com.example.shoppingapp.adapter.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingapp.databinding.SizeItemBinding

class SizeAdapter : RecyclerView.Adapter<SizeAdapter.SizeViewHolder>() {
    private var selectedPosition = -1
    private val diffCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    var onItemClickListener: ((String) -> Unit)? = null

    inner class SizeViewHolder(val binding: SizeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun selectedColor(size: String, position: Int) {
            binding.tvSize.text = size
            if (position == selectedPosition) {
                binding.apply {
                    imgShadow.visibility = View.VISIBLE
                }
            } else {
                binding.apply {
                    imgShadow.visibility = View.INVISIBLE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizeViewHolder {
        return SizeViewHolder(
            SizeItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SizeViewHolder, position: Int) {
        val size = differ.currentList[position]
        holder.selectedColor(size, position)
        holder.itemView.setOnClickListener {
            if (selectedPosition >= 0) {
                notifyItemChanged(selectedPosition)
            }
            selectedPosition = holder.adapterPosition
            notifyItemChanged(selectedPosition)
            onItemClickListener?.invoke(size)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}