package com.example.shoppingapp.fragments.shopping.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingapp.R
import com.example.shoppingapp.adapter.home.ColorAdapter
import com.example.shoppingapp.adapter.home.SizeAdapter
import com.example.shoppingapp.adapter.home.ViewPagerImageAdapter
import com.example.shoppingapp.data.Cart
import com.example.shoppingapp.data.Product
import com.example.shoppingapp.databinding.FragmentProductDetailBinding
import com.example.shoppingapp.utils.Resource
import com.example.shoppingapp.viewmodel.ProductDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductDetailFragment : Fragment() {
    private lateinit var binding: FragmentProductDetailBinding
    private val args by navArgs<ProductDetailFragmentArgs>()
    private val viewPagerImageAdapter by lazy { ViewPagerImageAdapter() }
    private val colorAdapter by lazy { ColorAdapter() }
    private val sizeAdapter by lazy { SizeAdapter() }
    private var selectedColor: Int? = null
    private var selectedSize: String? = null
    private val viewModel by viewModels<ProductDetailViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val product = args.product
        binding.apply {
            tvProductName.text = product.name
            tvProductOldPrice.text = getString(R.string.price_format, product.price.toString())
            tvProductCategory.text = product.category
            if (product.offerPercentage == null) {
                tvProductNewPrice.visibility = View.INVISIBLE
            }
            if (product.sizes.isNullOrEmpty()) {
                tvSize.visibility = View.INVISIBLE
            }
            if (product.colors.isNullOrEmpty()) {
                tvColor.visibility = View.INVISIBLE
            }

        }
        setupAdapter(
            binding.rvColors,
            colorAdapter,
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        )

        setupAdapter(
            binding.rvSizes,
            sizeAdapter,
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        )

        binding.apply {
            vpImg.adapter = viewPagerImageAdapter
        }

        viewPagerImageAdapter.differ.submitList(product.images)

        product.colors?.let {
            colorAdapter.differ.submitList(it)
        }

        product.sizes?.let {
            sizeAdapter.differ.submitList(it)
        }

        colorAdapter.onItemClickListener = {
            selectedColor = it
        }

        sizeAdapter.onItemClickListener = {
            selectedSize = it
        }
        binding.btnAddToCart.setOnClickListener {
            onAddToCartClick(product)
        }
        binding.btnClose.setOnClickListener {
            findNavController().popBackStack()
        }
        flowCollect()
    }

    private fun flowCollect() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cartProduct.collectLatest {
                    when (it) {
                        is Resource.Loading -> binding.btnAddToCart.startAnimation()
                        is Resource.Success -> {
                            binding.btnAddToCart.revertAnimation()
                            Toast.makeText(requireContext(), "Added to cart", Toast.LENGTH_SHORT)
                                .show()
                        }

                        is Resource.Error -> {
                            binding.btnAddToCart.stopAnimation()
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    private fun onAddToCartClick(product: Product) {
        val hasSizesOrColors = !product.sizes.isNullOrEmpty() || !product.colors.isNullOrEmpty()
        val isSelectionValid = selectedColor != null || selectedSize != null
        if (hasSizesOrColors && !isSelectionValid) {
            Toast.makeText(
                requireContext(),
                "Please select size and color",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            viewModel.updateCart(Cart(product, 1, selectedColor, selectedSize))
        }
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