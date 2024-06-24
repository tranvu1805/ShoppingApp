package com.example.shoppingapp.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.shoppingapp.data.Category
import com.example.shoppingapp.viewmodel.CategoryViewModel
import com.google.firebase.firestore.FirebaseFirestore

@Suppress("UNCHECKED_CAST")
class CategoryViewModelFactory(
    private val fireStore: FirebaseFirestore,
    private val category: Category,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CategoryViewModel(fireStore, category) as T
    }

}