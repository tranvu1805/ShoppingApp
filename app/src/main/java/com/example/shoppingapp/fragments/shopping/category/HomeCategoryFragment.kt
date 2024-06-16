package com.example.shoppingapp.fragments.shopping.category

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppingapp.adapter.home.SpecialProductAdapter
import com.example.shoppingapp.databinding.FragmentHomeCategoryBinding
import com.example.shoppingapp.utils.Resource
import com.example.shoppingapp.viewmodel.HomeCategoryViewmodel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeCategoryFragment : Fragment() {
    private lateinit var binding: FragmentHomeCategoryBinding
    private lateinit var specialProductAdapter: SpecialProductAdapter
    private val viewModel by viewModels<HomeCategoryViewmodel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSpecialAdapter()
        viewLifecycleOwner.apply {
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.specialProduct.collectLatest {
                        when (it) {
                            is Resource.Loading -> {
                                showLoading()
                            }

                            is Resource.Success -> {
                                hideLoading()
                                specialProductAdapter.differ.submitList(it.data)
                            }

                            is Resource.Error -> {
                                hideLoading()
                                Log.e("HomeCategoryFragment", it.message.toString())
                                Snackbar.make(
                                    requireView(),
                                    "Error: ${it.message}",
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }

                            else -> Unit
                        }
                    }
                }
            }
        }

    }

    private fun hideLoading() {
       binding.progressBar.visibility = View.INVISIBLE
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun setupSpecialAdapter() {
        specialProductAdapter = SpecialProductAdapter()
        binding.rvSpecialProducts.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = specialProductAdapter
        }
    }
}