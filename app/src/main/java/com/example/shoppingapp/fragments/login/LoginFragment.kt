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
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnLogin.setOnClickListener {
                onLoginLClick()
            }
            tvSubTitle.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
            tvForgetPassword.setOnClickListener {
                onForgetPasswordClick()
            }
        }
        observeResource(viewModel.login, ::onLoginSuccess, ::onLoginError, ::onLoginLoading)
        observeResource(viewModel.resetPassword,
            { onResetResponse("Reset link sent to your email") },
            { message -> onResetResponse(message.toString()) },
            {})
    }


    private fun <T> observeResource(
        flow: Flow<Resource<T>>,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit,
        onLoading: () -> Unit
    ) {
        viewLifecycleOwner.apply {
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    flow.collect { resource ->
                        when (resource) {
                            is Resource.Success -> onSuccess()
                            is Resource.Error -> onError(resource.message.toString())
                            is Resource.Loading -> onLoading()
                            else -> Unit
                        }
                    }
                }
            }
        }
    }


    private fun onLoginLoading() {
        binding.btnLogin.startAnimation()
    }

    private fun onLoginError(it: String?) {
        binding.btnLogin.revertAnimation()
        Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        Log.d("LoginFragment", "onViewCreated: $it")
    }

    private fun onLoginSuccess() {
        binding.btnLogin.revertAnimation()
        Intent(
            requireActivity(), ShoppingActivity::class.java
        ).also { intent ->
            intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            )
            startActivity(intent)
        }
    }

    private fun onResetResponse(message: String) {
        Snackbar.make(
            requireView(), message, Snackbar.LENGTH_LONG
        ).show()
    }

    private fun onForgetPasswordClick() {
        setupBottomSheetDialog { email ->
            viewModel.resetPassword(email)
        }
    }

    private fun FragmentLoginBinding.onLoginLClick() {
        viewModel.loginUser(
            edtEmail.text.toString().trim(), edtPassword.text.toString()
        )
    }
}