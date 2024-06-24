package com.example.shoppingapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.data.Cart
import com.example.shoppingapp.firebase.FirebaseUtils
import com.example.shoppingapp.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseUtils: FirebaseUtils,
) : ViewModel() {
    private val _cartProduct = MutableStateFlow<Resource<Cart>>(Resource.Undefined())
    val cartProduct = _cartProduct.asStateFlow()
    fun updateCart(cart: Cart) {
        viewModelScope.launch {
            _cartProduct.emit(Resource.Loading())
        }
        fireStore.collection("users").document(auth.uid!!)
            .collection("carts").whereEqualTo("product.id", cart.product.id).get()
            .addOnSuccessListener {
                it.documents.let { list ->
                    if (list.isEmpty()) {
                        addNewProductToCart(cart)
                    } else {
                        val product = list[0].toObject(Cart::class.java)
                        if (product?.product == cart.product) {
                            val documentId = list[0].id
                            increaseProductQuantity(documentId, cart)
                        } else {
                            addNewProductToCart(cart)
                        }
                    }
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _cartProduct.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    private fun addNewProductToCart(cart: Cart) {
        firebaseUtils.addProductToCart(cart) { product, exception ->
            viewModelScope.launch {
                if (exception == null) {
                    _cartProduct.emit(Resource.Success(product!!))
                } else {
                    _cartProduct.emit(Resource.Error(exception.message.toString()))
                }
            }
        }
    }

    private fun increaseProductQuantity(documentId: String, cart: Cart) {
        firebaseUtils.increaseProductQuantity(documentId) { _, exception ->
            viewModelScope.launch {
                if (exception == null) {
                    _cartProduct.emit(Resource.Success(cart))
                } else {
                    _cartProduct.emit(Resource.Error(exception.message.toString()))
                }
            }
        }
    }
}