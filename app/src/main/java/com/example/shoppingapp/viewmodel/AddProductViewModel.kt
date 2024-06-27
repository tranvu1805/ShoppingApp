package com.example.shoppingapp.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.data.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class AddProductViewModel @Inject constructor(
    application: Application,
    private val firestore: FirebaseFirestore,
    private val firebaseStorage: StorageReference,
) : AndroidViewModel(application) {
    private val _selectedImages = MutableLiveData<MutableList<Uri>>(mutableListOf())
    val selectedImages: LiveData<MutableList<Uri>> = _selectedImages

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _colors = MutableLiveData<MutableList<Int>>(mutableListOf())
    val colors: LiveData<MutableList<Int>> = _colors
    fun validation(name: String, price: String, category: String): Boolean {
        return name.isNotEmpty() && price.isNotEmpty() && category.isNotEmpty()
                && !selectedImages.value.isNullOrEmpty()
    }

    fun clearImages() {
        _selectedImages.value = mutableListOf()
    }

    fun addImages(uri: Uri) {
        _selectedImages.value?.apply {
            add(uri)
            _selectedImages.value = this
        }
    }

    fun addColor(color: Int) {
        _colors.value?.apply {
            add(color)
            _colors.value = this
        }
    }

    @SuppressLint("NewApi")
    fun saveProduct(
        name: String,
        price: String,
        category: String,
        description: String,
        sizes: String,
        offerPercentage: String,
        onResult: (Boolean) -> Unit,
    ) {
        val size = getSizeList(sizes)
        val images = mutableListOf<String>()
        val productDescription = description.trim()
        val productCategory = category.trim()
        val productPrice = price.trim()
        val productOfferPercentage = offerPercentage.trim()
        val imagesByteArray = getImagesByteArray()
        viewModelScope.launch {
            _isLoading.value = true
            try {
                async {
                    imagesByteArray.forEach {
                        val id = UUID.randomUUID().toString()
                        launch {
                            val imgStorage = firebaseStorage.child("products/images/$id")
                            val result = imgStorage.putBytes(it).await()
                            val downloadUrl = result.storage.downloadUrl.await().toString()
                            images.add(downloadUrl)
                        }
                    }
                }.await()
            } catch (e: Exception) {
                _isLoading.value = false
                onResult(false)
            }
            val product = Product(
                UUID.randomUUID().toString(),
                name,
                productCategory,
                productPrice.toFloat(),
                if (productOfferPercentage.isEmpty()) null else productOfferPercentage.toFloat(),
                productDescription.ifEmpty { null },
                _colors.value ?: mutableListOf(),
                size,
                images
            )
            firestore.collection("Products").add(product)
                .addOnSuccessListener {
                    onResult(true)
                    _isLoading.value = false
                }
                .addOnFailureListener {
                    onResult(false)
                    _isLoading.value = false
                    Log.e("AddProductViewModel", it.message.toString())
                }

        }

    }

    @Suppress("DEPRECATION")
    @RequiresApi(Build.VERSION_CODES.P)
    private fun getImagesByteArray(): List<ByteArray> {
        val imagesByteArray = mutableListOf<ByteArray>()
        _selectedImages.value?.forEach {
            val stream = ByteArrayOutputStream()
            val imgBitmap: Bitmap
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val sourceImg =
                    ImageDecoder.createSource(getApplication<Application>().contentResolver, it)
                imgBitmap = ImageDecoder.decodeBitmap(sourceImg)
            } else {
                imgBitmap = MediaStore.Images.Media.getBitmap(
                    getApplication<Application>().contentResolver,
                    it
                )
            }

            if (imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)) {
                imagesByteArray.add(stream.toByteArray())
            }
        }
        return imagesByteArray

    }

    private fun getSizeList(sizes: String): List<String> {
        if (sizes.isEmpty()) return emptyList()
        return sizes.split(",").map { it.trim() }
    }
}