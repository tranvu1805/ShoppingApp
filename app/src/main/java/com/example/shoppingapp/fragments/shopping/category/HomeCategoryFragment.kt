package com.example.shoppingapp.fragments.shopping.category

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingapp.R
import com.example.shoppingapp.adapter.home.BestDealProductAdapter
import com.example.shoppingapp.adapter.home.BestProductAdapter
import com.example.shoppingapp.adapter.home.SpecialProductAdapter
import com.example.shoppingapp.data.Product
import com.example.shoppingapp.databinding.FragmentHomeCategoryBinding
import com.example.shoppingapp.utils.Resource
import com.example.shoppingapp.viewmodel.HomeCategoryViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeCategoryFragment : Fragment() {
    private lateinit var binding: FragmentHomeCategoryBinding
    private lateinit var specialProductAdapter: SpecialProductAdapter
    private lateinit var bestDealAdapter: BestDealProductAdapter
    private lateinit var bestProductAdapter: BestProductAdapter
    private val viewModel by viewModels<HomeCategoryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        specialProductAdapter = SpecialProductAdapter()
        setupAdapter(
            binding.rvSpecialProducts,
            specialProductAdapter,
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        )
        flowCollectData(viewModel.specialProduct) {
            specialProductAdapter.differ.submitList(it.data)
        }

        bestProductAdapter = BestProductAdapter(requireContext())
        setupAdapter(
            binding.rvBestProducts,
            bestProductAdapter,
            GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        )
        flowCollectData(viewModel.bestProduct) {
            bestProductAdapter.differ.submitList(it.data)
        }

        bestDealAdapter = BestDealProductAdapter(requireContext())
        setupAdapter(
            binding.rvBestDeals,
            bestDealAdapter,
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        )
        flowCollectData(viewModel.bestDealProduct) {
            bestDealAdapter.differ.submitList(it.data)
        }

        binding.nestedScrollView.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { nestedScrollView, _, scrollY, _, _ ->
                if (nestedScrollView.getChildAt(0).bottom <= (nestedScrollView.height + scrollY)) {
                    viewModel.loadNewProducts()
                }
            }
        )
        bestProductAdapter.onItemClickListener = {
            onItemClick(it)
        }
        specialProductAdapter.onItemClickListener = {
            onItemClick(it)
        }
        bestDealAdapter.onItemClickListener = {
            onItemClick(it)
        }
    }

    private fun onItemClick(it: Product) {
        val bundle = Bundle().apply { putParcelable("product", it) }
        findNavController().navigate(R.id.action_homeFragment_to_productDetailFragment, bundle)
    }

    private fun <T> flowCollectData(
        flow: StateFlow<Resource<T>>,
        onSuccess: (Resource.Success<T>) -> Unit,
    ) {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> showLoading()
                        is Resource.Success -> onSuccessCollect(onSuccess, resource)
                        is Resource.Error -> onFailureCollect(resource)
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun <T> onFailureCollect(resource: Resource<T>) {
        hideLoading()
        Log.e("HomeCategoryFragment", resource.message.toString())
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
        hideLoading()
        onSuccess(resource)
    }

    private fun hideLoading() {
        binding.bottomProgressBar.visibility = View.INVISIBLE
    }

    private fun showLoading() {
        binding.bottomProgressBar.visibility = View.VISIBLE
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
}