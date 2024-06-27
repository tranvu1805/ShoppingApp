package com.example.shoppingapp.data.order

import android.os.Parcelable
import com.example.shoppingapp.data.Address
import com.example.shoppingapp.data.Cart
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random.Default.nextLong

@Parcelize
data class Order(
    val orderStatus: String,
    val totalPrice: Double,
    val products: List<Cart>,
    val address: Address,
    val orderId: Long = nextLong(0, 100_000_000_000) + totalPrice.toLong(),
    val date: String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
) : Parcelable {
    constructor() : this("", 0.0, emptyList(), Address())
}
