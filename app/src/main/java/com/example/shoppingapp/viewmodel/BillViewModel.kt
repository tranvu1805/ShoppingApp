package com.example.shoppingapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.data.Address
import com.example.shoppingapp.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BillViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
) : ViewModel() {
    private val _address = MutableStateFlow<Resource<List<Address>>>(Resource.Undefined())
    val address = _address.asStateFlow()

    init {
        getAddress()
    }

    private fun getAddress() {
        viewModelScope.launch {
            _address.emit(Resource.Loading())
        }
        firestore.collection("users").document(firebaseAuth.uid!!)
            .collection("addresses")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    viewModelScope.launch {
                        _address.emit(Resource.Error(error.message.toString()))
                    }
                    return@addSnapshotListener
                }
                val addresses = value?.toObjects(Address::class.java)
                viewModelScope.launch {
                    _address.emit(Resource.Success(addresses!!))
                }
            }
    }

}