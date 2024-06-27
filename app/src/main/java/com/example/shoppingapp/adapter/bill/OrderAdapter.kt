package com.example.shoppingapp.adapter.bill

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingapp.R
import com.example.shoppingapp.data.order.Order
import com.example.shoppingapp.data.order.OrderStatus
import com.example.shoppingapp.data.order.OrderStatus.Canceled.getOrderStatus
import com.example.shoppingapp.databinding.OrderItemBinding

class OrderAdapter : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {
    private val diffCallback = object : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.orderId == newItem.orderId
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, diffCallback)
    var onItemClickListener: ((Order) -> Unit)? = null

    inner class OrderViewHolder(val binding: OrderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            binding.apply {
                tvOrderDate.text = order.date
                tvOrderId.text = order.orderId.toString()
                val resource = itemView.resources
                val colorDrawable = when (getOrderStatus(order.orderStatus)) {
                    is OrderStatus.Canceled -> {
                        resource.getColor(R.color.g_red, null)
                    }

                    is OrderStatus.Delivered -> {
                        resource.getColor(R.color.g_green, null)
                    }

                    is OrderStatus.Confirmed -> {
                        resource.getColor(R.color.g_green, null)
                    }

                    is OrderStatus.Shipped -> {
                        resource.getColor(R.color.g_red, null)
                    }

                    is OrderStatus.Returned -> {
                        resource.getColor(R.color.g_red, null)
                    }

                    is OrderStatus.Ordered -> {
                        resource.getColor(R.color.g_orange_yellow, null)
                    }
                }
                imageOrderState.setImageDrawable(ColorDrawable(colorDrawable))

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        return OrderViewHolder(
            OrderItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = differ.currentList[position]
        holder.bind(order)
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(order)
        }
    }
}