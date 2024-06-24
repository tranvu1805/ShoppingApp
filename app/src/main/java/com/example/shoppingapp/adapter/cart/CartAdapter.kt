package com.example.shoppingapp.adapter.cart

import android.content.Context
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
import com.example.shoppingapp.databinding.CartItemBinding
import com.example.shoppingapp.helper.getProductPrice

class CartAdapter(
    private val context: Context,
) : RecyclerView.Adapter<CartAdapter.CartAdapterViewHolder>() {
    var onItemClickListener: ((Cart) -> Unit)? = null
    var onIncreaseClickListener: ((Cart) -> Unit)? = null
    var onDecreaseClickListener: ((Cart) -> Unit)? = null

    inner class CartAdapterViewHolder(
        val binding: CartItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindItem(cart: Cart) {
            binding.apply {
                Glide.with(itemView).load(cart.product.images[0]).into(imageCartProduct)
                tvProductCartName.text = cart.product.name
                tvCartProductQuantity.text = cart.quantity.toString()
                val priceAfterOffer = cart.product.offerPercentage.getProductPrice(cart.product.price)
                tvProductCartPrice.text =
                    context.getString(R.string.price_format, priceAfterOffer.toString())
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
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(oldItem: Cart, newItem: Cart): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartAdapterViewHolder {
        return CartAdapterViewHolder(
            CartItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: CartAdapterViewHolder, position: Int) {
        val cart = differ.currentList[position]
        holder.bindItem(cart)
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(cart)
        }
        holder.binding.imagePlus.setOnClickListener {
            onIncreaseClickListener?.invoke(cart)
        }
        holder.binding.imageMinus.setOnClickListener {
            onDecreaseClickListener?.invoke(cart)
        }
    }
}