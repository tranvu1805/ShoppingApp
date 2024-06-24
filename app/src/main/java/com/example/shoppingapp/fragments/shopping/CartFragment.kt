package com.example.shoppingapp.fragments.shopping

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppingapp.R
import com.example.shoppingapp.adapter.cart.CartAdapter
import com.example.shoppingapp.data.Cart
import com.example.shoppingapp.databinding.FragmentCartBinding
import com.example.shoppingapp.firebase.FirebaseUtils
import com.example.shoppingapp.utils.Resource
import com.example.shoppingapp.viewmodel.CartViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding
    private val viewModel: CartViewModel by activityViewModels()
    private val cartAdapter by lazy { CartAdapter(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCartAdapter()
        cartAdapter.onItemClickListener = {
            onItemClick(it)
        }
        cartAdapter.onIncreaseClickListener = {
            viewModel.changeQuantity(it, FirebaseUtils.ChangingState.INCREASE)
        }
        cartAdapter.onDecreaseClickListener = {
            viewModel.changeQuantity(it, FirebaseUtils.ChangingState.DECREASE)
        }
        calculatePriceOnFlow()
        flowCollect()
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.deleteFlow.collectLatest {
                    val alert = AlertDialog.Builder(requireContext()).apply {
                        setTitle("Delete")
                        setMessage("Do you want to delete this product?")
                        setPositiveButton("Yes") { dialog, _ ->
                            viewModel.deleteProduct(it)
                            dialog.dismiss()
                        }
                        setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }
                    }
                    alert.create()
                    alert.show()
                }
            }
        }
    }

    private fun flowCollect() {
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cartProduct.collectLatest {
                    when (it) {
                        is Resource.Loading -> binding.progressbarCart.visibility = View.VISIBLE
                        is Resource.Success -> handleOnSuccess(it)
                        is Resource.Error -> handleOnError(it)
                        else -> Unit
                    }
                }

            }
        }
    }

    private fun handleOnError(it: Resource<List<Cart>>) {
        binding.progressbarCart.visibility = View.INVISIBLE
        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
    }

    private fun handleOnSuccess(it: Resource<List<Cart>>) {
        binding.progressbarCart.visibility = View.INVISIBLE
        if (it.data.isNullOrEmpty()) {
            showEmptyCart()
        } else {
            hideEmptyCart()
            cartAdapter.differ.submitList(it.data)
        }
    }

    private fun calculatePriceOnFlow() {
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.totalPrice.collectLatest {
                    binding.tvTotalPrice.text = getString(R.string.price_format, it)
                }
            }
        }
    }

    private fun onItemClick(it: Cart) {
        val bundle = Bundle().apply { putParcelable("product", it.product) }
        findNavController().navigate(R.id.action_homeFragment_to_productDetailFragment, bundle)
    }

    private fun showEmptyCart() {
        binding.apply {
            layoutCartEmpty.visibility = View.VISIBLE
            rvCart.visibility = View.GONE
            totalBoxContainer.visibility = View.GONE
            btnCheckOut.visibility = View.GONE
        }
    }

    private fun hideEmptyCart() {
        binding.apply {
            layoutCartEmpty.visibility = View.INVISIBLE
            rvCart.visibility = View.VISIBLE
            totalBoxContainer.visibility = View.VISIBLE
            btnCheckOut.visibility = View.VISIBLE
        }
    }

    private fun setupCartAdapter() {
        binding.rvCart.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = cartAdapter

        }
    }
}