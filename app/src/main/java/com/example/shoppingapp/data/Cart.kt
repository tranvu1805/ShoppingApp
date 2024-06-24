package com.example.shoppingapp.data

data class Cart(
    val product: Product,
    val quantity: Int,
    val color: Int? = null,
    val size: String? = null,
){
    constructor():this(Product(),1,null,null)
}
