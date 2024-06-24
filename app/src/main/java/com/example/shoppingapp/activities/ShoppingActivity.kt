package com.example.shoppingapp.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment

import androidx.navigation.ui.setupWithNavController
import com.example.shoppingapp.R
import com.example.shoppingapp.data.Cart
import com.example.shoppingapp.databinding.ActivityShoppingBinding
import com.example.shoppingapp.utils.Resource
import com.example.shoppingapp.viewmodel.CartViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ShoppingActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityShoppingBinding.inflate(layoutInflater)
    }
    private val viewModel by viewModels<CartViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        binding.navHome.setupWithNavController(navController)
//        observeResource(viewModel.cartProduct)
        observeResource()
    }

    private fun observeResource() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cartProduct.collect { resource ->
                    when (resource) {
                        is Resource.Success -> onSuccess(resource)
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun onSuccess(resource: Resource<List<Cart>>) {
        val count = resource.data?.size ?: 0
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navHome)
        bottomNavigationView.getOrCreateBadge(R.id.cartFragment).apply {
            number = count
            backgroundColor = ContextCompat.getColor(this@ShoppingActivity, R.color.g_blue)
        }
    }


//    private fun <T> observeResource(
//        flow: Flow<Resource<T>>,
//    ) {
//        lifecycleScope.launch {
//            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                flow.collect { resource ->
//                    when (resource) {
//                        is Resource.Success -> {
//                            val count = resource.data?:0
//                        }
//
//                        is Resource.Error -> onError(resource.message.toString())
//                        is Resource.Loading -> onLoading()
//                        else -> Unit
//                    }
//                }
//            }
//        }
//    }
//
//    private fun onLoading() {
//        TODO("Not yet implemented")
//    }
//
//    private fun onError(error: String): (String?) -> Unit {
//
//    }
//
//    private fun onSuccess(): () -> Unit {
//
//    }
}