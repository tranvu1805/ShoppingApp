package com.example.shoppingapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.data.Address
import com.example.shoppingapp.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val auth: FirebaseAuth,
) : ViewModel() {
    private val _address = MutableStateFlow<Resource<Address>>(Resource.Undefined())
    val address = _address.asStateFlow()

    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    fun saveAddress(address: Address) {
        val validateAddress = validateAddress(address)
        if (validateAddress) {
            viewModelScope.launch {
                _address.emit(Resource.Loading())
            }
            fireStore.collection("users").document(auth.uid!!)
                .collection("addresses").document().set(address)
                .addOnSuccessListener {
                    viewModelScope.launch {
                        _address.emit(Resource.Success(address))
                    }
                }
                .addOnFailureListener {
                    viewModelScope.launch {
                        _address.emit(Resource.Error(it.message.toString()))
                    }
                }
        } else {
            viewModelScope.launch {
                _error.emit("Please fill all fields")
            }
        }


    }

    private fun validateAddress(address: Address): Boolean {
        return address.addressTitle.trim().isNotEmpty() &&
                address.fullName.trim().isNotEmpty() &&
                address.street.trim().isNotEmpty() &&
                address.city.trim().isNotEmpty() &&
                address.state.trim().isNotEmpty() &&
                address.phone.trim().isNotEmpty()
    }
}