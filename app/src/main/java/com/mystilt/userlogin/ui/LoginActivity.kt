package com.mystilt.userlogin.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.mystilt.databinding.ActivityLoginBinding
import com.mystilt.deeplink.DeepLinkResponse
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context, deepLinkResponse: DeepLinkResponse?=null): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }
    private lateinit var binding: ActivityLoginBinding

    private val viewModel: LandingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        window.decorView.clipToOutline = true
        setContentView(binding.root)
    }
}

