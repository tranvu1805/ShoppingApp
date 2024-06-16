package com.example.shoppingapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.data.Product
import com.example.shoppingapp.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeCategoryViewmodel @Inject constructor(
    private val fireStore: FirebaseFirestore
) : ViewModel() {
    private val _specialProduct = MutableStateFlow<Resource<List<Product>>>(Resource.Undefined())
    val specialProduct: StateFlow<Resource<List<Product>>>
        get() = _specialProduct
    init {
        fetchSpecialProducts()
    }
    private fun fetchSpecialProducts() {
        viewModelScope.launch {
            _specialProduct.value = Resource.Loading()
        }
        fireStore.collection("Products").whereEqualTo("category", "Special")
            .get().addOnSuccessListener {
                val products = it.toObjects(Product::class.java)
                viewModelScope.launch {
                    _specialProduct.value = Resource.Success(products)
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _specialProduct.value = Resource.Error(it.message.toString())
                }
            }
    }
}