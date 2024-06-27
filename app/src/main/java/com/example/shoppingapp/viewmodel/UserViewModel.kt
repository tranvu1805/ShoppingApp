package com.example.shoppingapp.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.data.User
import com.example.shoppingapp.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject

@Suppress("DEPRECATION")
@HiltViewModel
class UserViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    application: Application,
    private val storage: StorageReference,
) : AndroidViewModel(application) {
    private val _user = MutableStateFlow<Resource<User>>(Resource.Undefined())
    val user = _user.asStateFlow()

    private val _updateUser = MutableStateFlow<Resource<User>>(Resource.Undefined())
    val updateUser = _updateUser.asStateFlow()

    init {
        getUser()
    }

    private fun getUser() {
        viewModelScope.launch {
            _user.emit(Resource.Loading())
        }
        firestore.collection("users").document(firebaseAuth.uid!!).get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                user?.let {
                    viewModelScope.launch {
                        _user.emit(Resource.Success(user))

                    }
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _user.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun updateUser(user: User, image: Uri?) {
        val isValidate =
            user.firstName.trim().isNotEmpty() && user.lastName.trim().isNotEmpty()
        if (isValidate) {
            viewModelScope.launch {
                _updateUser.emit(Resource.Loading())
            }
            if (image == null) {
                saveUserInformation(user, true)
            } else {
                saveUserInformationWithNewImages(user, image)
            }
        } else {
            viewModelScope.launch {
                _updateUser.emit(Resource.Error("Please check all fields is valid"))
            }
        }
    }

    private fun saveUserInformationWithNewImages(user: User, image: Uri) {
        viewModelScope.launch {
            try {
                val stream = ByteArrayOutputStream()
                val imgBitmap: Bitmap
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val sourceImg =
                        ImageDecoder.createSource(
                            getApplication<Application>().contentResolver,
                            image
                        )
                    imgBitmap = ImageDecoder.decodeBitmap(sourceImg)
                } else {
                    imgBitmap = MediaStore.Images.Media.getBitmap(
                        getApplication<Application>().contentResolver,
                        image
                    )
                }
                imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                val imagesByteArray = stream.toByteArray()
                val imageDirectory =
                    storage.child("images/${firebaseAuth.uid}/${UUID.randomUUID()}")
                val result = imageDirectory.putBytes(imagesByteArray).await()
                val downloadUrl = result.storage.downloadUrl.await().toString()
                val newUser = user.copy(imagePath = downloadUrl)
                saveUserInformation(newUser, false)
            } catch (e: Exception) {
                viewModelScope.launch {
                    _updateUser.emit(Resource.Error(e.message.toString()))
                }
            }

        }
    }

    private fun saveUserInformation(user: User, isOldImage: Boolean) {
        firestore.runTransaction {
            val documentRef = firestore.collection("users").document(firebaseAuth.uid!!)
            if (isOldImage) {
                val currentUser = it.get(documentRef).toObject(User::class.java)
                val newUser = user.copy(imagePath = currentUser?.imagePath ?: "")
                it.set(documentRef, newUser)
            } else {
                it.set(documentRef, user)
            }
        }.addOnSuccessListener {
            viewModelScope.launch {
                _updateUser.emit(Resource.Success(user))

            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _updateUser.emit(Resource.Error("Please check all fields is valid"))
            }
        }
    }

}