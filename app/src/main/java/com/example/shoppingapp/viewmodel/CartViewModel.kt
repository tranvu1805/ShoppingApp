package com.example.shoppingapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.data.Cart
import com.example.shoppingapp.firebase.FirebaseUtils
import com.example.shoppingapp.helper.getProductPrice
import com.example.shoppingapp.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseUtils: FirebaseUtils,
) : ViewModel() {
    private val _cartProduct = MutableStateFlow<Resource<List<Cart>>>(Resource.Undefined())
    val cartProduct = _cartProduct.asStateFlow()
    private var cartDocument = emptyList<DocumentSnapshot>()
    val totalPrice = cartProduct.map {
        when (it) {
            is Resource.Success -> calculateTotalPrice(it.data!!)
            else -> null
        }
    }

    private val _deleteFlow = MutableSharedFlow<Cart>()
    val deleteFlow = _deleteFlow.asSharedFlow()

    private fun calculateTotalPrice(data: List<Cart>): Float {
        return data.sumOf {
            (it.product.offerPercentage.getProductPrice(it.product.price) * it.quantity).toDouble()
        }.toFloat()
    }

    init {
        getCart()
    }

    private fun getCart() {
        onLoading()
        fireStore.collection("users").document(auth.uid!!).collection("carts")
            .addSnapshotListener { value, error ->
                if (error != null || value == null) {
                    viewModelScope.launch {
                        _cartProduct.emit(Resource.Error(error?.message.toString()))
                    }
                } else {
                    cartDocument = value.documents
                    val cartProducts = value.toObjects(Cart::class.java)
                    viewModelScope.launch { _cartProduct.emit(Resource.Success(cartProducts)) }
                }
            }
    }

    fun changeQuantity(cart: Cart, state: FirebaseUtils.ChangingState) {
        val index = getIndexFromCart(cart)
        if (isNotNullAndAvailable(index)) {
            val documentId = getDocumentId(index!!)
            when (state) {
                FirebaseUtils.ChangingState.INCREASE -> increaseProductQuantity(documentId)
                FirebaseUtils.ChangingState.DECREASE -> decreaseProductQuantity(documentId, cart)
            }
        }
    }

    fun deleteProduct(cart: Cart) {
        val index = getIndexFromCart(cart)
        if (isNotNullAndAvailable(index)) {
            val documentId = getDocumentId(index!!)
            firebaseUtils.cartCollection.document(documentId).delete()
        }
    }

    private fun isNotNullAndAvailable(index: Int?) = index != null && index != -1

    private fun getDocumentId(index: Int) = cartDocument[index].id

    private fun getIndexFromCart(cart: Cart) = cartProduct.value.data?.indexOf(cart)

    private fun decreaseProductQuantity(documentId: String, cart: Cart) {
        if (cart.quantity == 1) {
            viewModelScope.launch {
                _deleteFlow.emit(cart)
            }
            return
        }
        onLoading()
        firebaseUtils.decreaseProductQuantity(documentId) { _, exception ->
            handleException(exception)
        }
    }

    private fun increaseProductQuantity(documentId: String) {
        onLoading()
        firebaseUtils.increaseProductQuantity(documentId) { _, exception ->
            handleException(exception)
        }
    }

    private fun onLoading() {
        viewModelScope.launch {
            _cartProduct.emit(Resource.Loading())
        }
    }

    private fun handleException(exception: Exception?) {
        if (exception != null) {
            viewModelScope.launch {
                _cartProduct.emit(Resource.Error(exception.message.toString()))
            }
        }
    }
}