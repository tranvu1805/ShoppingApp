package com.example.shoppingapp.fragments.shopping.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppingapp.R
import com.example.shoppingapp.adapter.bill.BillProductAdapter
import com.example.shoppingapp.data.order.OrderStatus
import com.example.shoppingapp.databinding.FragmentOrderDetailBinding

class OrderDetailFragment : Fragment() {
    private lateinit var binding: FragmentOrderDetailBinding
    private val billProductAdapter by lazy { BillProductAdapter() }
    private val args: OrderDetailFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentOrderDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val order = args.order
        binding.apply {
            tvOrderId.text = getString(R.string.order_id, order.orderId.toString())
            tvFullName.text = order.address.fullName
            tvAddress.text = getString(
                R.string.address_detail,
                order.address.street,
                order.address.city,
                order.address.state
            )
            tvPhoneNumber.text = order.address.phone
            tvTotalPrice.text = getString(R.string.price_format, order.totalPrice.toString())
            stepView.setSteps(
                mutableListOf(
                    OrderStatus.Ordered.status,
                    OrderStatus.Confirmed.status,
                    OrderStatus.Shipped.status,
                    OrderStatus.Delivered.status
                )
            )
            val currentStep = when (order.orderStatus) {
                OrderStatus.Ordered.status -> 0
                OrderStatus.Confirmed.status -> 1
                OrderStatus.Shipped.status -> 2
                OrderStatus.Delivered.status -> 3
                else -> 0
            }
            stepView.go(currentStep, false)
            if (currentStep == 3) {
                stepView.done(true)
            }
        }
        setupAdapter()
        billProductAdapter.differ.submitList(order.products)
    }

    private fun setupAdapter() {
        binding.rvProducts.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = billProductAdapter
        }
    }
}