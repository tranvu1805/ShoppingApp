package com.example.shoppingapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.data.Category
import com.example.shoppingapp.data.Product
import com.example.shoppingapp.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val fireStore: FirebaseFirestore,
    private val category: Category,
) : ViewModel() {
    private val _offerProduct = MutableStateFlow<Resource<List<Product>>>(Resource.Undefined())
    val offerProduct: StateFlow<Resource<List<Product>>>
        get() = _offerProduct

    private val _bestProduct = MutableStateFlow<Resource<List<Product>>>(Resource.Undefined())
    val bestProduct: StateFlow<Resource<List<Product>>>
        get() = _bestProduct

    private val queryOfferProduct = fireStore.collection("Products")
        .whereEqualTo("category", category.categoryType)
        .whereNotEqualTo("offerPercentage", null)

    private val queryBestProduct = fireStore.collection("Products")
        .whereEqualTo("category", category.categoryType)
        .whereEqualTo("offerPercentage", null)

    init {
        fetchProducts(_offerProduct, queryOfferProduct)
        fetchProducts(_bestProduct, queryBestProduct)
    }


    private fun fetchProducts(
        mutableFlow: MutableStateFlow<Resource<List<Product>>>,
        query: Query,
    ) {
        viewModelScope.launch {
            mutableFlow.emit(Resource.Loading())
        }
        query.get().addOnSuccessListener {
            val products = it.toObjects(Product::class.java)
            viewModelScope.launch {
                mutableFlow.emit(Resource.Success(products))
            }

        }.addOnFailureListener {
            viewModelScope.launch {
                mutableFlow.emit(Resource.Error(it.message.toString()))
            }
        }
    }
}
