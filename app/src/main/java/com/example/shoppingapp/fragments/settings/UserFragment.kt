package com.example.shoppingapp.fragments.settings

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.shoppingapp.data.User
import com.example.shoppingapp.databinding.FragmentUserBinding
import com.example.shoppingapp.utils.Resource
import com.example.shoppingapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserFragment : Fragment() {
    private lateinit var binding: FragmentUserBinding
    private val viewModel: UserViewModel by viewModels()
    private var imageUri: Uri? = null
    private lateinit var imageActivityResultLauncher: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                imageUri = it.data?.data
                Glide.with(requireContext()).load(imageUri).into(binding.imageUser)
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeResource(viewModel.user,
            onLoading = {
                showLoading()
            },
            onSuccess = {
                hideLoading()
                showUserInfo(it.data!!)
            },
            onError = {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            })
        observeResource(viewModel.updateUser,
            onLoading = {
                binding.buttonSave.startAnimation()
            },
            onSuccess = {
                binding.buttonSave.revertAnimation()
                findNavController().navigateUp()
            },
            onError = {
                binding.buttonSave.revertAnimation()
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            })
        binding.buttonSave.setOnClickListener {
            val user = User(
                binding.edFirstName.text.toString().trim(),
                binding.edLastName.text.toString().trim(),
                binding.edEmail.text.toString().trim(),
            )
            viewModel.updateUser(user, imageUri)
        }
        binding.imageEdit.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            imageActivityResultLauncher.launch(intent)
        }
        binding.imageCloseUserAccount.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    private fun showUserInfo(data: User) {
        binding.apply {
            Glide.with(requireContext()).load(data.imagePath).error(ColorDrawable(Color.BLACK))
                .into(imageUser)
            edFirstName.setText(data.firstName)
            edLastName.setText(data.lastName)
            edEmail.setText(data.email)
        }
    }

    private fun showLoading() {
        binding.apply {
            progressbarAccount.visibility = View.VISIBLE
            buttonSave.visibility = View.INVISIBLE
            imageUser.visibility = View.INVISIBLE
            edFirstName.visibility = View.INVISIBLE
            edLastName.visibility = View.INVISIBLE
            edEmail.visibility = View.INVISIBLE
            tvUpdatePassword.visibility = View.INVISIBLE
            imageEdit.visibility = View.INVISIBLE
        }
    }

    private fun hideLoading() {
        binding.apply {
            progressbarAccount.visibility = View.INVISIBLE
            buttonSave.visibility = View.VISIBLE
            imageUser.visibility = View.VISIBLE
            edFirstName.visibility = View.VISIBLE
            edLastName.visibility = View.VISIBLE
            edEmail.visibility = View.VISIBLE
            tvUpdatePassword.visibility = View.VISIBLE
            imageEdit.visibility = View.VISIBLE
        }
    }

    private fun <T> observeResource(
        flow: Flow<Resource<T>>,
        onSuccess: (Resource<T>) -> Unit,
        onError: (String?) -> Unit,
        onLoading: () -> Unit,
    ) {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collectLatest { resource ->
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