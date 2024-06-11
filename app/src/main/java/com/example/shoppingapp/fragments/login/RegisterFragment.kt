package com.example.shoppingapp.fragments.login

import android.os.Bundle
import android.util.Log
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
import com.example.shoppingapp.data.User
import com.example.shoppingapp.databinding.FragmentRegisterBinding
import com.example.shoppingapp.utils.RegisterValidation
import com.example.shoppingapp.utils.Resource
import com.example.shoppingapp.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnRegister.setOnClickListener {
                val user = User(
                    edtFirstName.text.toString().trim(),
                    edtLastName.text.toString().trim(),
                    edtEmail.text.toString().trim()
                )
                val password = edtPassword.text.toString()
                viewModel.createUser(user, password)
            }
            tvSubTitle.setOnClickListener {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }
        viewLifecycleOwner.apply {
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.register.collect {
                        when (it) {
                            is Resource.Loading -> {
                                binding.btnRegister.startAnimation()
                            }

                            is Resource.Success -> {
                                Log.d("RegisterFragment", "onSuccess: ${it.data}")
                                binding.btnRegister.revertAnimation()
                            }

                            is Resource.Error -> {
                                binding.btnRegister.revertAnimation()
                                Log.e("RegisterFragment", "onError: ${it.message}")
                            }

                            is Resource.Undefined -> Unit
                        }
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.validation.collect {
                    if (it.email is RegisterValidation.Failed) {
                        withContext(Dispatchers.Main) {
                            binding.edtEmail.apply {
                                requestFocus()
                                error = it.email.message
                            }
                        }
                    }
                    if (it.password is RegisterValidation.Failed) {
                        withContext(Dispatchers.Main) {
                            binding.edtPassword.apply {
                                requestFocus()
                                error = it.password.message
                            }
                        }
                    }
                }
            }

        }
    }
}