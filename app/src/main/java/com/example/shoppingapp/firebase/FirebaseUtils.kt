package com.example.shoppingapp.firebase

import com.example.shoppingapp.data.Cart
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseUtils(
    private val fireStore: FirebaseFirestore,
    private val auth: FirebaseAuth,
) {
    val cartCollection =
        fireStore.collection("users").document(auth.uid!!).collection("carts")

    fun addProductToCart(cart: Cart, onResult: (Cart?, Exception?) -> Unit) {
        cartCollection.document().set(cart)
            .addOnSuccessListener {
                onResult(cart, null)
            }
            .addOnFailureListener {
                onResult(null, it)
            }
    }

    fun increaseProductQuantity(documentId: String, onResult: (String?, Exception?) -> Unit) {
        fireStore.runTransaction { transaction ->
            val documentRef = cartCollection.document(documentId)
            val document = transaction.get(documentRef)
            val product = document.toObject(Cart::class.java)
            product?.let {
                val newQuantity = it.quantity + 1
                val newProduct = it.copy(quantity = newQuantity)
                transaction.set(documentRef, newProduct)
            }
        }.addOnSuccessListener {
            onResult(documentId, null)
        }.addOnFailureListener {
            onResult(null, it)
        }

    }

    fun decreaseProductQuantity(documentId: String, onResult: (String?, Exception?) -> Unit) {
        fireStore.runTransaction { transaction ->
            val documentRef = cartCollection.document(documentId)
            val document = transaction.get(documentRef)
            val product = document.toObject(Cart::class.java)
            product?.let {
                val newQuantity = it.quantity - 1
                val newProduct = it.copy(quantity = newQuantity)
                transaction.set(documentRef, newProduct)
            }
        }.addOnSuccessListener {
            onResult(documentId, null)
        }.addOnFailureListener {
            onResult(null, it)
        }

    }

    enum class ChangingState {
        INCREASE, DECREASE
    }

}
