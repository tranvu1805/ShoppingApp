package com.example.shoppingapp.fragments.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.example.shoppingapp.R
import com.example.shoppingapp.activities.ShoppingActivity
import com.example.shoppingapp.databinding.FragmentLoginBinding
import com.example.shoppingapp.dialog.setupBottomSheetDialog
import com.example.shoppingapp.utils.Resource
import com.example.shoppingapp.viewmodel.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnLogin.setOnClickListener {
                viewModel.loginUser(
                    edtEmail.text.toString().trim(),
                    edtPassword.text.toString()
                )
            }
            tvSubTitle.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
            tvForgetPassword.setOnClickListener {
                setupBottomSheetDialog { email ->
                    viewModel.resetPassword(email)
                }
            }
        }
        viewLifecycleOwner.apply {
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.login.collect {
                        when (it) {
                            is Resource.Success -> {
                                binding.btnLogin.revertAnimation()
                                Intent(
                                    requireActivity(),
                                    ShoppingActivity::class.java
                                ).also { intent ->
                                    intent.addFlags(
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                or Intent.FLAG_ACTIVITY_NEW_TASK
                                    )
                                    startActivity(intent)
                                }

                            }

                            is Resource.Error -> {
                                binding.btnLogin.revertAnimation()
                                Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG)
                                    .show()
                                Log.d("LoginFragment", "onViewCreated: ${it.message}")
                            }

                            is Resource.Loading -> {
                                binding.btnLogin.startAnimation()
                            }

                            else -> Unit
                        }
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.resetPassword.collect {
                    when (it) {
                        is Resource.Success -> {
                            Snackbar.make(
                                requireView(),
                                "Reset link was sent to your email",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                        is Resource.Error -> {
                            Snackbar.make(
                                requireView(),
                                "Error : ${it.message}",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                        is Resource.Loading -> {}
                        else -> Unit
                    }
                }
            }
        }
    }
}