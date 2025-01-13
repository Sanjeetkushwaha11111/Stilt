package com.ourstilt.userlogin.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ourstilt.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val viewModel by lazy {
        LoginViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        window.decorView.clipToOutline = true
        setContentView(binding.root)
    }
}

