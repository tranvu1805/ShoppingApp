package com.example.shoppingapp.adapter.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.shoppingapp.fragments.shopping.category.CategoryFragment
import com.example.shoppingapp.fragments.shopping.category.HomeCategoryFragment

class HomeViewPagerAdapter(
    private val categoryIndex: List<ArrayList<String>>,
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return categoryIndex.size
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            HomeCategoryFragment()
        } else {
            CategoryFragment.newInstance(categoryIndex[position])
        }
    }
}