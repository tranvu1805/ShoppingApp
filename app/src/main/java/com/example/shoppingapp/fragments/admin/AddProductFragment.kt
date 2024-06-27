package com.example.shoppingapp.fragments.admin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.shoppingapp.R
import com.example.shoppingapp.databinding.FragmentAddProductBinding
import com.example.shoppingapp.viewmodel.AddProductViewModel
import com.google.android.material.snackbar.Snackbar
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddProductFragment : Fragment() {
    private lateinit var binding: FragmentAddProductBinding
    private val viewModel: AddProductViewModel by viewModels()
    private lateinit var selectImagesActivityResult: ActivityResultLauncher<Intent>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAddProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerForActivityResult()
        binding.tvColorPicker.setOnClickListener {
            onClickPickColor()
        }
        binding.tvImagesPicker.setOnClickListener {
            onClickPickImages()
        }
        binding.btnSubmit.setOnClickListener {
            onClickSubmit()
        }
        viewModel.colors.observe(viewLifecycleOwner) {
            updateColorTextView(it)
        }
        viewModel.selectedImages.observe(viewLifecycleOwner) {
            updateImagesTextView(it)
        }
        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) showLoading() else hideLoading()
        }
    }

    private fun onClickSubmit() {
        val checkInput = viewModel.validation(
            binding.edName.text.toString(),
            binding.edPrice.text.toString(),
            binding.edCategory.text.toString()
        )
        if (checkInput) {
            viewModel.saveProduct(
                binding.edName.text.toString(),
                binding.edPrice.text.toString(),
                binding.edCategory.text.toString(),
                binding.edDescription.text.toString(),
                binding.edSizes.text.toString(),
                binding.edOfferPercentage.text.toString()
            ) {
                if (it) {
                    Snackbar.make(binding.root, "Product added successfully", Snackbar.LENGTH_SHORT)
                        .show()
                } else {
                    Snackbar.make(binding.root, "Error adding product", Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun hideLoading() {
        binding.progressbar.visibility = View.INVISIBLE
    }

    private fun showLoading() {
        binding.progressbar.visibility = View.VISIBLE
    }

    private fun onClickPickImages() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        selectImagesActivityResult.launch(intent)
        viewModel.clearImages()
    }

    private fun registerForActivityResult() {
        selectImagesActivityResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                if (intent?.clipData != null) {
                    val count = intent.clipData?.itemCount ?: 0
                    (0 until count).forEach {
                        val imageUri = intent.clipData?.getItemAt(it)?.uri
                        imageUri?.let { uri ->
                            viewModel.addImages(uri)
                        }
                    }
                } else {
                    val imageUri = intent?.data
                    imageUri?.let {
                        viewModel.addImages(it)
                    }
                }
            }
        }
    }

    private fun onClickPickColor() {
        ColorPickerDialog.Builder(requireContext()).setTitle("Product Color")
            .setPositiveButton("Select", object : ColorEnvelopeListener {
                override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                    envelope?.let {
                        viewModel.addColor(it.color)
                    }
                }
            })
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }


    private fun updateColorTextView(colors: List<Int>) {
        var colorString = ""
        colors.forEach {
            colorString += "#${Integer.toHexString(it)} "
        }
        binding.tvColors.text = colorString
    }

    private fun updateImagesTextView(images: List<Uri>) {
        binding.tvNumberImages.text = getString(R.string.number_of_images, images.size.toString())
    }
}