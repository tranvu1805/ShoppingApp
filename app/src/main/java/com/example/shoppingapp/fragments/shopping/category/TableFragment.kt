package com.example.shoppingapp.fragments.shopping.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.shoppingapp.data.Category
import com.example.shoppingapp.databinding.FragmentCategoryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TableFragment : CategoryFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        category = Category.Table
    }


    override fun onOfferPagingRequest() {

    }

    override fun onBestPagingRequest() {

    }


}
