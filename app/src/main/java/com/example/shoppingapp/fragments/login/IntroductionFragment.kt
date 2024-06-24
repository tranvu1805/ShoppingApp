package com.example.shoppingapp.fragments.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.shoppingapp.R
import com.example.shoppingapp.activities.ShoppingActivity
import com.example.shoppingapp.databinding.FragmentIntroductionBinding
import com.example.shoppingapp.viewmodel.IntroductionViewModel
import com.example.shoppingapp.viewmodel.IntroductionViewModel.Companion.ACCOUNT_OPTION_FRAGMENT
import com.example.shoppingapp.viewmodel.IntroductionViewModel.Companion.SHOPPING_APP
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class IntroductionFragment : Fragment() {
    private lateinit var binding: FragmentIntroductionBinding
    private val viewModel: IntroductionViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIntroductionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnStart.setOnClickListener {
            viewModel.startButtonClick()
            findNavController().navigate(R.id.action_introductionFragment_to_welcomeFragment)
        }
        observedNavigation();
    }

    private fun observedNavigation() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.navigation.collect {
                    when (it) {
                        SHOPPING_APP -> onLoginSuccess()
                        ACCOUNT_OPTION_FRAGMENT -> findNavController().navigate(it)
                    }
                }
            }
        }
    }

    private fun onLoginSuccess() {
        Intent(
            requireActivity(), ShoppingActivity::class.java
        ).also { intent ->
            intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            )
            startActivity(intent)
        }
    }
}