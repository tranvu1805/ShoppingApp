package com.example.shoppingapp.adapter.home

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingapp.databinding.ColorItemBinding

class ColorAdapter : RecyclerView.Adapter<ColorAdapter.ColorViewHolder>() {
    private var selectedPosition = -1

    private val diffCallback = object : DiffUtil.ItemCallback<Int>() {
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    var onItemClickListener: ((Int) -> Unit)? = null

    inner class ColorViewHolder(val binding: ColorItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun selectedColor(color: Int, position: Int) {
            binding.imgColor.setImageDrawable(ColorDrawable(color))
            if (position == selectedPosition) {
                binding.apply {
                    imgShadow.visibility = View.VISIBLE
                    imgCheck.visibility = View.VISIBLE
                }
            } else {
                binding.apply {
                    imgShadow.visibility = View.INVISIBLE
                    imgCheck.visibility = View.INVISIBLE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        return ColorViewHolder(
            ColorItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val color = differ.currentList[position]
        holder.selectedColor(color, position)
        holder.itemView.setOnClickListener {
            if (selectedPosition >= 0) {
                notifyItemChanged(selectedPosition)
            }
            selectedPosition = holder.adapterPosition
            notifyItemChanged(selectedPosition)
            onItemClickListener?.invoke(color)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}