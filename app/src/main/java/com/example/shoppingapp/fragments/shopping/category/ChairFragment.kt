package com.example.shoppingapp.fragments.shopping.category

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.shoppingapp.data.Category
import com.example.shoppingapp.utils.Resource
import com.example.shoppingapp.viewmodel.CategoryViewModel
import com.example.shoppingapp.viewmodel.factory.CategoryViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChairFragment: CategoryFragment() {
    @Inject
    lateinit var fireStore: FirebaseFirestore

    private val viewModel by viewModels<CategoryViewModel> {
        CategoryViewModelFactory(fireStore, Category.Chair)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        flowCollectData(viewModel.offerProduct) {
            specialProductAdapter.differ.submitList(it.data)
        }


    }

    private fun <T> flowCollectData(
        flow: StateFlow<Resource<T>>,
        onSuccess: (Resource.Success<T>) -> Unit,
    ) {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> Unit
                        is Resource.Success -> onSuccessCollect(onSuccess, resource)
                        is Resource.Error -> onFailureCollect(resource)
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun <T> onFailureCollect(resource: Resource<T>) {
        Log.e("Chair Fragment", resource.message.toString())
        Snackbar.make(
            requireView(),
            "Error: ${resource.message}",
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun <T> onSuccessCollect(
        onSuccess: (Resource.Success<T>) -> Unit,
        resource: Resource.Success<T>,
    ) {
        onSuccess(resource)
    }

}