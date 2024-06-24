package com.example.shoppingapp.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
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
class HomeCategoryViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val app: Application,
) : AndroidViewModel(app) {

    private val _specialProduct = MutableStateFlow<Resource<List<Product>>>(Resource.Undefined())
    val specialProduct: StateFlow<Resource<List<Product>>>
        get() = _specialProduct

    private val _bestDealProduct = MutableStateFlow<Resource<List<Product>>>(Resource.Undefined())
    val bestDealProduct: StateFlow<Resource<List<Product>>>
        get() = _bestDealProduct

    private val _bestProduct = MutableStateFlow<Resource<List<Product>>>(Resource.Undefined())
    val bestProduct: StateFlow<Resource<List<Product>>>
        get() = _bestProduct

    private val pagingInfo = PagingInfo()

    //Chưa phân loại sản phẩm theo danh mục,
    init {
        fetchProducts(_specialProduct, "Products", "category", "Best")
        fetchProducts(_bestDealProduct, "Products", "category", "Deal")
        fetchProducts(_bestProduct, "Products", "category", "Special")
    }

    fun loadNewProducts() {
        fetchProducts(_bestProduct, "Products", "category", "Special")
    }

    private fun fetchProducts(
        mutableFlow: MutableStateFlow<Resource<List<Product>>>,
        path: String,
        category: String,
        value: String,
    ) {
        if (!pagingInfo.isLastProduct) {
            viewModelScope.launch {
                mutableFlow.emit(Resource.Loading())
            }
            fireStore.collection(path).whereEqualTo(category, value)
                .limit(pagingInfo.productToFetch * 10)
                .get()
                .addOnSuccessListener {
                    val products = it.toObjects(Product::class.java)
                    if (pagingInfo.currentProducts == products.size) {
                        pagingInfo.isLastProduct = true
                    }
                    pagingInfo.currentProducts = products.size
                    viewModelScope.launch {
                        mutableFlow.emit(Resource.Success(products))
                    }
                    pagingInfo.productToFetch++
                }
                .addOnFailureListener {
                    viewModelScope.launch {
                        mutableFlow.emit(Resource.Error(it.message.toString()))
                    }
                }
        } else {
            Toast.makeText(app, "No more products", Toast.LENGTH_SHORT).show()
        }
    }


    internal data class PagingInfo(
        var productToFetch: Long = 1,
        var isLastProduct: Boolean = false,
        var currentProducts: Int = 0,
    )
}