package com.example.shoppingapp.adapter.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoppingapp.data.Product
import com.example.shoppingapp.databinding.SpecialItemBinding

class SpecialProductAdapter :
    RecyclerView.Adapter<SpecialProductAdapter.SpecialProductViewHolder>() {
    var onItemClickListener: ((Product) -> Unit)? = null

    inner class SpecialProductViewHolder(
        private val binding: SpecialItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindItem(product: Product) {
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(imgProductSpecial)
                tvProductName.text = product.name
                tvProductPrice.text = product.price.toString()
            }
            binding.btnAddToCart.setOnClickListener {
                onItemClickListener?.invoke(product)
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialProductViewHolder {
        return SpecialProductViewHolder(
            SpecialItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: SpecialProductViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bindItem(product)
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(product)
        }
    }
}