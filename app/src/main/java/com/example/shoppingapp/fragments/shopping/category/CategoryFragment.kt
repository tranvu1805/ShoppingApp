package com.example.shoppingapp.fragments.shopping.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingapp.adapter.home.BestProductAdapter
import com.example.shoppingapp.adapter.home.SpecialProductAdapter
import com.example.shoppingapp.data.Category
import com.example.shoppingapp.databinding.FragmentCategoryBinding
import com.example.shoppingapp.utils.Resource
import com.example.shoppingapp.viewmodel.CategoryViewModel
import com.example.shoppingapp.viewmodel.factory.CategoryViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class CategoryFragment : Fragment() {
    protected lateinit var binding: FragmentCategoryBinding
    protected lateinit var category: Category
    private val specialProductAdapter by lazy { SpecialProductAdapter() }
    private val bestProductAdapter by lazy { BestProductAdapter(requireContext()) }

    @Inject
    lateinit var fireStore: FirebaseFirestore

    protected val viewModel by viewModels<CategoryViewModel> {
        CategoryViewModelFactory(fireStore, category)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        category = Category.Chair
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        flowCollectData(viewModel.offerProduct) {
            specialProductAdapter.differ.submitList(it.data)
        }
        flowCollectData(viewModel.bestProduct) {
            bestProductAdapter.differ.submitList(it.data)
        }
        setupAdapter(
            binding.rvSpecialProducts,
            specialProductAdapter,
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        )
        setupAdapter(
            binding.rvBestProducts,
            bestProductAdapter,
            GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        )
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
                        is Resource.Success -> onSuccess(resource)
                        is Resource.Error -> onFailure(resource)
                        else -> {}
                    }
                }
            }
        }
    }

    private fun <T> onFailure(resource: Resource<T>) {
        Snackbar.make(requireView(), "Error: ${resource.message}", Snackbar.LENGTH_LONG).show()
    }

    private fun <T : RecyclerView.Adapter<*>> setupAdapter(
        recyclerView: RecyclerView,
        adapter: T,
        layoutManager: RecyclerView.LayoutManager,
    ) {
        recyclerView.apply {
            this.layoutManager = layoutManager
            this.adapter = adapter
        }
    }

    open fun onOfferPagingRequest() {}

    open fun onBestPagingRequest() {}
}
