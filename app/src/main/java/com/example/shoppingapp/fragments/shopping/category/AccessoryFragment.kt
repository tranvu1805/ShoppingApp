package com.example.shoppingapp.fragments.shopping.category

import android.os.Bundle
import com.example.shoppingapp.data.Category
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccessoryFragment : CategoryFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        category = Category.Accessory
    }
}