package com.example.shoppingapp.adapter.home

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoppingapp.R
import com.example.shoppingapp.data.Product
import com.example.shoppingapp.databinding.BestDealItemBinding
import com.example.shoppingapp.helper.getProductPrice

class BestDealProductAdapter(
    private val context: Context,
) :
    RecyclerView.Adapter<BestDealProductAdapter.BestDealProductViewHolder>() {
    var onItemClickListener: ((Product) -> Unit)? = null

    inner class BestDealProductViewHolder(
        private val binding: BestDealItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindItem(product: Product) {
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(imgProductBest)
                tvProductNameBest.text = product.name
                tvOldPriceBest.text =
                    context.getString(R.string.price_format, product.price.toString())
                tvOldPriceBest.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                val priceAfterOffer = product.price.getProductPrice(product.price)
                tvNewPriceBest.text =
                    context.getString(R.string.price_format, priceAfterOffer.toString())
                if (product.offerPercentage == null) {
                    tvNewPriceBest.visibility = ViewGroup.INVISIBLE
                }
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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestDealProductViewHolder {
        return BestDealProductViewHolder(
            BestDealItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BestDealProductViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bindItem(product)
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(product)
        }
    }
}