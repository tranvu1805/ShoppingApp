package com.example.shoppingapp.fragments.shopping.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingapp.R
import com.example.shoppingapp.adapter.home.BestProductAdapter
import com.example.shoppingapp.adapter.home.SpecialProductAdapter
import com.example.shoppingapp.databinding.FragmentCategoryBinding
import dagger.hilt.android.AndroidEntryPoint


open class CategoryFragment : Fragment(R.layout.fragment_category) {
    protected lateinit var binding: FragmentCategoryBinding
    private lateinit var data: List<String>
    protected val specialProductAdapter by lazy { SpecialProductAdapter() }
    protected val bestProductAdapter by lazy { BestProductAdapter(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        binding.rvSpecialProducts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1) && dx != 0) {
                    onOfferPagingRequest()
                }
            }
        })
        binding.nestedScrollView.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { nestedScrollView, _, scrollY, _, _ ->
                if (nestedScrollView.getChildAt(0).bottom <= (nestedScrollView.height + scrollY)) {
                    onBestPagingRequest()
                }
            }
        )
    }

    open fun onOfferPagingRequest() {

    }

    open fun onBestPagingRequest() {

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