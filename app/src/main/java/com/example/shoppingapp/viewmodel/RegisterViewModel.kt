package com.example.shoppingapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.shoppingapp.data.User
import com.example.shoppingapp.utils.Constants.USER_COLLECTION
import com.example.shoppingapp.utils.RegisterState
import com.example.shoppingapp.utils.RegisterValidation
import com.example.shoppingapp.utils.Resource
import com.example.shoppingapp.utils.validateEmail
import com.example.shoppingapp.utils.validatePassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFireStore: FirebaseFirestore
) : ViewModel() {
    private val _register = MutableStateFlow<Resource<User>>(Resource.Undefined())
    val register: Flow<Resource<User>> = _register

    private val _validation = Channel<RegisterState>()
    val validation = _validation.receiveAsFlow()
    fun createUser(user: User, password: String) {
        if (checkInput(user, password)) {
            runBlocking {
                _register.value = Resource.Loading()
            }
            firebaseAuth.createUserWithEmailAndPassword(user.email, password)
                .addOnSuccessListener {
                    it.user?.let { firebaseUser ->
                       saveUserToFireStore(firebaseUser.uid, user)
                    }
                }
                .addOnFailureListener {
                    _register.value = Resource.Error(it.message.toString())
                }
        } else {
            val registerState = RegisterState(
                validateEmail(user.email),
                validatePassword(password)
            )
            runBlocking {
                _validation.send(registerState)
            }
        }
    }

    private fun saveUserToFireStore(userId: String, user: User) {
        firebaseFireStore.collection(USER_COLLECTION)
            .document(userId)
            .set(user)
            .addOnSuccessListener {
                _register.value = Resource.Success(user)
            }
            .addOnFailureListener {
                _register.value = Resource.Error(it.message.toString())
            }
    }

    private fun checkInput(user: User, password: String): Boolean {
        val emailValidation = validateEmail(user.email)
        val passwordValidation = validatePassword(password)
        return emailValidation is RegisterValidation.Success
                && passwordValidation is RegisterValidation.Success
    }
}