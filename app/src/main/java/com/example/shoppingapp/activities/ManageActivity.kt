package com.example.shoppingapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.shoppingapp.databinding.ActivityManageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManageActivity : AppCompatActivity() {
    private val binding by lazy { ActivityManageBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}