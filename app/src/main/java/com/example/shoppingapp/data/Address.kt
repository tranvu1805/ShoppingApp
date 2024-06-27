package com.example.shoppingapp.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Address(
    val addressTitle: String,
    val fullName: String,
    val street: String,
    val city: String,
    val state: String,
    val phone: String,
) : Parcelable {
    constructor() : this("", "", "", "", "", "")
}
