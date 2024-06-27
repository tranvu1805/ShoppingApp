package com.example.shoppingapp.data.order

sealed class OrderStatus(val status: String) {
    data object Ordered : OrderStatus("Ordered")
    data object Canceled : OrderStatus("Canceled")
    data object Confirmed : OrderStatus("Confirmed")
    data object Shipped : OrderStatus("Shipped")
    data object Delivered : OrderStatus("Delivered")
    data object Returned : OrderStatus("Returned")

    fun getOrderStatus(status: String): OrderStatus {
        return when (status) {
            "Ordered" -> Ordered
            "Canceled" -> Canceled
            "Confirmed" -> Confirmed
            "Shipped" -> Shipped
            "Delivered" -> Delivered
            else -> Returned
        }
    }
}