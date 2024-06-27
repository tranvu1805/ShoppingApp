package com.example.shoppingapp.adapter.bill

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoppingapp.R
import com.example.shoppingapp.data.Cart
import com.example.shoppingapp.databinding.BillingProductsRvItemBinding
import com.example.shoppingapp.helper.getProductPrice

class BillProductAdapter : RecyclerView.Adapter<BillProductAdapter.BillProductViewHolder>() {

    inner class BillProductViewHolder(val binding: BillingProductsRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(cart: Cart) {
            binding.apply {
                Glide.with(itemView).load(cart.product.images[0]).into(imageCartProduct)
                tvProductCartName.text = cart.product.name
                tvBillingProductQuantity.text = cart.quantity.toString()
                val priceAfterDiscount =
                    cart.product.offerPercentage.getProductPrice(cart.product.price)
                tvProductCartPrice.text =
                    itemView.context.getString(R.string.price_format, priceAfterDiscount.toString())
                imageCartProductColor.setImageDrawable(
                    ColorDrawable(
                        cart.color ?: Color.TRANSPARENT
                    )
                )
                tvCartProductSize.text = cart.size ?: "".also {
                    imageCartProductSize.setImageDrawable(ColorDrawable(Color.TRANSPARENT))
                }
            }
        }

    }

    private val diffCallback = object : DiffUtil.ItemCallback<Cart>() {
        override fun areItemsTheSame(oldItem: Cart, newItem: Cart): Boolean {
            return oldItem.product == newItem.product
        }

        override fun areContentsTheSame(oldItem: Cart, newItem: Cart): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, diffCallback)
    var onItemClickListener: ((Cart) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillProductViewHolder {
        return BillProductViewHolder(
            BillingProductsRvItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BillProductViewHolder, position: Int) {
        val billProduct = differ.currentList[position]
        holder.bind(billProduct)
    }
}