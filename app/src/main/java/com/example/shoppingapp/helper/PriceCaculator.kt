package com.example.shoppingapp.helper

fun Float?.getProductPrice(price: Float): Float {
    if (this == null) {
        return price
    }
    val remainPercentage = 1f - this
    return price * remainPercentage
}