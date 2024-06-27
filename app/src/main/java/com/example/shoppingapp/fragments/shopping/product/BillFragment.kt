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
import com.example.shoppingapp.adapter.bill.AddressAdapter
import com.example.shoppingapp.adapter.bill.BillProductAdapter
import com.example.shoppingapp.data.Address
import com.example.shoppingapp.data.Cart
import com.example.shoppingapp.data.order.Order
import com.example.shoppingapp.data.order.OrderStatus
import com.example.shoppingapp.databinding.FragmentBillBinding
import com.example.shoppingapp.dialog.showConfirmationDialog
import com.example.shoppingapp.utils.Resource
import com.example.shoppingapp.viewmodel.BillViewModel
import com.example.shoppingapp.viewmodel.OrderViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BillFragment : Fragment() {
    private lateinit var binding: FragmentBillBinding
    private val addressAdapter by lazy { AddressAdapter() }
    private val billProductAdapter by lazy { BillProductAdapter() }
    private val viewModel by viewModels<BillViewModel>()
    private val orderViewModel by viewModels<OrderViewModel>()
    private val navArgs by navArgs<BillFragmentArgs>()
    private var products = emptyList<Cart>()
    private var totalPrice = 0f
    private var selectedAddress: Address? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        products = navArgs.products.toList()
        totalPrice = navArgs.totalPrice
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentBillBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (products.isEmpty()||totalPrice==0f) {
            binding.apply {
                buttonPlaceOrder.visibility = View.INVISIBLE
                totalBoxContainer.visibility = View.INVISIBLE
                bottomLine.visibility = View.INVISIBLE
                middleLine.visibility = View.INVISIBLE
            }

        }
        setupAdapter(
            binding.rvAddress, addressAdapter,
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        )
        setupAdapter(
            binding.rvProducts, billProductAdapter,
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        )
        binding.imageAddAddress.setOnClickListener {
            findNavController().navigate(R.id.action_billFragment_to_addressFragment)
        }
        addressAdapter.onItemClickListener = {
            selectedAddress = it
        }
        binding.buttonPlaceOrder.setOnClickListener {
            if (selectedAddress == null) {
                Toast.makeText(requireContext(), "Please select an address", Toast.LENGTH_SHORT)
                    .show()
            } else {
                showConfirmationDialog()
            }
        }
        observeResource(viewModel.address,
            onLoading = { binding.progressbarAddress.visibility = View.VISIBLE },
            onSuccess = {
                binding.progressbarAddress.visibility = View.INVISIBLE
                addressAdapter.differ.submitList(it.data)
            },
            onError = {
                binding.progressbarAddress.visibility = View.INVISIBLE
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            })
        observeResource(orderViewModel.order,
            onLoading = { binding.buttonPlaceOrder.startAnimation() },
            onSuccess = {
                binding.buttonPlaceOrder.revertAnimation()
                findNavController().navigateUp()
                Snackbar.make(
                    requireView(),
                    "Order placed successfully",
                    Snackbar.LENGTH_SHORT
                ).show()
            },
            onError = {
                binding.buttonPlaceOrder.revertAnimation()
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            })
        billProductAdapter.differ.submitList(products)
        binding.tvTotalPrice.text = getString(R.string.price_format, totalPrice.toString())
    }

    private fun showConfirmationDialog() {
        requireContext().showConfirmationDialog(
            "Place Order",
            "Are you sure you want to place this order?",
            onPositiveClick = {
                val order = Order(
                    OrderStatus.Ordered.status,
                    totalPrice.toDouble(),
                    products,
                    selectedAddress!!
                )
                orderViewModel.placeOrder(order)
            }
        )
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

    private fun <T> observeResource(
        flow: Flow<Resource<T>>,
        onSuccess: (Resource<T>) -> Unit,
        onError: (String?) -> Unit,
        onLoading: () -> Unit,
    ) {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collect { resource ->
                    when (resource) {
                        is Resource.Success -> onSuccess(resource)
                        is Resource.Error -> onError(resource.message.toString())
                        is Resource.Loading -> onLoading()
                        else -> Unit
                    }
                }
            }
        }
    }
}