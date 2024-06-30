package com.example.shoppingapp.fragments.shopping.category

import android.os.Bundle
import com.example.shoppingapp.data.Category
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CupboardFragment : com.example.shoppingapp.fragments.shopping.category.CategoryFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        category = Category.Cupboard
    }
}