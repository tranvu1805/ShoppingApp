package com.example.shoppingapp.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.shoppingapp.adapter.home.HomeViewPagerAdapter
import com.example.shoppingapp.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var categories: List<ArrayList<String>>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categories = listOf(
            arrayListOf("Item 1.1", "Item 1.2", "Item 1.3"),
            arrayListOf("Item 2.1", "Item 2.2", "Item 2.3"),
            arrayListOf("Item 3.1", "Item 3.2", "Item 3.3")
        )
        val viewPagerAdapter = HomeViewPagerAdapter(categories, childFragmentManager, lifecycle)
        binding.viewPager.adapter = viewPagerAdapter
        TabLayoutMediator(binding.tabHome, binding.viewPager) { tab, position ->
           when(position){
               0 -> tab.text = "Home"
               1 -> tab.text = "Chair"
               2 -> tab.text = "Cupboard"
           }
        }.attach()
    }

}