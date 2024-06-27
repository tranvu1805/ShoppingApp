package com.example.shoppingapp.dependenceinjection

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.example.shoppingapp.firebase.FirebaseUtils
import com.example.shoppingapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()


    @Provides
    @Singleton
    fun provideFirebaseFireStore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()


    @Provides
    @Singleton
    fun provideStorageReference(): StorageReference = Firebase.storage.reference

    @Provides
    @Singleton
    fun provideSharePreferences(application: Application): SharedPreferences {
        return application.getSharedPreferences(
            Constants.INTRODUCTION_SHARED_PREFERENCES,
            MODE_PRIVATE
        )
    }

    @Provides
    @Singleton
    fun provideFirebaseUtils(
        fireStore: FirebaseFirestore,
        auth: FirebaseAuth,
    ) = FirebaseUtils(fireStore, auth)
}