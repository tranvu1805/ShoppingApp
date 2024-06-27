package com.example.shoppingapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.data.order.Order
import com.example.shoppingapp.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderedViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
) : ViewModel() {
    private val _ordered = MutableStateFlow<Resource<List<Order>>>(Resource.Undefined())
    val ordered = _ordered.asStateFlow()

    init {
        getOrdered()
    }

    private fun getOrdered() {
        viewModelScope.launch {
            _ordered.emit(Resource.Loading())
        }
        firestore.collection("users").document(firebaseAuth.uid!!)
            .collection("Orders").get()
            .addOnSuccessListener {
                val orders = it.toObjects(Order::class.java)
                viewModelScope.launch {
                    _ordered.emit(Resource.Success(orders))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _ordered.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}