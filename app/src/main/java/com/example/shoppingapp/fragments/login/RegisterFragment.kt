package com.example.shoppingapp.fragments.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
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
                onRegisterClick()
            }
            tvSubTitle.setOnClickListener {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }
        observeResource(viewModel.register, ::onSuccess, ::onError, ::onLoading)
        viewLifecycleOwner.apply {
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.validation.collect {
                        if (it.email is RegisterValidation.Failed) {
                            handleFieldError(binding.edtEmail, it.email.message)
                        }
                        if (it.password is RegisterValidation.Failed) {
                            handleFieldError(binding.edtPassword, it.password.message)
                        }
                    }
                }
            }
        }
    }
    private suspend fun handleFieldError(field: View, errorMessage: String) {
        withContext(Dispatchers.Main) {
            when (field) {
                is EditText -> {
                    field.apply {
                        requestFocus()
                        error = errorMessage
                    }
                }
                else -> {}
            }
        }
    }
    private fun <T> observeResource(
        flow: Flow<Resource<T>>,
        onSuccess: (Resource<T>) -> Unit,
        onError: (String?) -> Unit,
        onLoading: () -> Unit
    ) {
        viewLifecycleOwner.apply {
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
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

    private fun onError(message: String?) {
        binding.btnRegister.revertAnimation()
        Log.e("RegisterFragment", "onError: $message")
        onRegisterResponse("Registration Error: $message")

    }

    private fun onSuccess(it: Resource<User>) {
        binding.btnRegister.revertAnimation()
        Log.d("RegisterFragment", "onSuccess: ${it.data}")
        onRegisterResponse("Registration Successful")
    }

    private fun onLoading() {
        binding.btnRegister.startAnimation()
    }

    private fun onRegisterResponse(message: String) {
        Snackbar.make(
            requireView(), message, Snackbar.LENGTH_LONG
        ).show()
    }
    private fun FragmentRegisterBinding.onRegisterClick() {
        val user = User(
            edtFirstName.text.toString().trim(),
            edtLastName.text.toString().trim(),
            edtEmail.text.toString().trim()
        )
        val password = edtPassword.text.toString()
        viewModel.createUser(user, password)
    }
}