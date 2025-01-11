package com.ourstilt.userlogin.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.ourstilt.R
import com.ourstilt.databinding.ActivityLoginBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding
    private val navController by lazy {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navHostFragment.navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        // Ensure window clipping
        window.decorView.clipToOutline = true
        window.setBackgroundDrawableResource(R.drawable.fragment_rounded_background)
        setContentView(binding.root)
        lifecycleScope.launch {
            delay(3000)
            setUpFragmentsNav()
        }

    }

    private fun setUpFragmentsNav() {
        navController.navigate(R.id.loginFragment)
    }
}

