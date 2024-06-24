package com.example.shoppingapp.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.R
import com.example.shoppingapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroductionViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    private val _navigation = MutableStateFlow(0)
    val navigation get() = _navigation

    init {
        val isButtonClicked = sharedPreferences.getBoolean(Constants.INTRODUCTION_KEY, false)
        val user = firebaseAuth.currentUser
        if (user != null) {
            emitFlow(_navigation, SHOPPING_APP)
        } else if (isButtonClicked) {
            emitFlow(_navigation, ACCOUNT_OPTION_FRAGMENT)
        } else Unit
    }

    private fun emitFlow(flow: MutableStateFlow<Int>, value: Int) {
        viewModelScope.launch {
            flow.emit(value)
        }
    }

    fun startButtonClick() {
        sharedPreferences.edit().putBoolean(Constants.INTRODUCTION_KEY, true).apply()
    }

    companion object {
        const val SHOPPING_APP = 111
        var ACCOUNT_OPTION_FRAGMENT = R.id.action_introductionFragment_to_welcomeFragment
    }
}
