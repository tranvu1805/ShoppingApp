package com.example.shoppingapp.fragments.shopping.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.shoppingapp.R
import com.example.shoppingapp.databinding.FragmentCategoryBinding

class CategoryFragment : Fragment() {
    private lateinit var binding: FragmentCategoryBinding
    private lateinit var data: List<String>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            data = it.getStringArrayList(ARG_LIST) ?: listOf()
        }
    }

    companion object {
        private const val ARG_LIST = "data_list"

        @JvmStatic
        fun newInstance(data: ArrayList<String>): CategoryFragment {
            val fragment = CategoryFragment()
            val args = Bundle()
            args.putStringArrayList(ARG_LIST, data)
            fragment.arguments = args
            return fragment
        }
    }
}