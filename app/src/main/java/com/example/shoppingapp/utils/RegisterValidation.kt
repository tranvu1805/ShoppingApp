package com.example.shoppingapp.utils

sealed class RegisterValidation {
    data object Success : RegisterValidation()
    data class Failed(
        val message: String,
    ) : RegisterValidation()
}

data class RegisterState(
    val email: RegisterValidation,
    val password: RegisterValidation,
)