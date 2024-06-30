package com.example.shoppingapp.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.shoppingapp.adapter.home.HomeViewPagerAdapter
import com.example.shoppingapp.databinding.FragmentHomeBinding
import com.example.shoppingapp.fragments.shopping.category.AccessoryFragment
import com.example.shoppingapp.fragments.shopping.category.ChairFragment
import com.example.shoppingapp.fragments.shopping.category.FurnitureFragment
import com.example.shoppingapp.fragments.shopping.category.HomeCategoryFragment
import com.example.shoppingapp.fragments.shopping.category.TableFragment
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var categories: ArrayList<Fragment>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categories = arrayListOf(
            HomeCategoryFragment(),
            ChairFragment(),
            TableFragment(),
            FurnitureFragment(),
            AccessoryFragment()
        )
        val viewPagerAdapter = HomeViewPagerAdapter(categories, childFragmentManager, lifecycle)
        binding.viewPager.adapter = viewPagerAdapter
        binding.viewPager.isUserInputEnabled = false
        TabLayoutMediator(binding.tabHome, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Home"
                1 -> tab.text = "Chair"
                2 -> tab.text = "Table"
                3 -> tab.text = "Furniture"
                4 -> tab.text = "Accessory"
            }
        }.attach()

    }

}