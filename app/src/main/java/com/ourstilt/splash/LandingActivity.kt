package com.ourstilt.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.ourstilt.base.ui.BaseViewPagerAdapter
import com.ourstilt.common.startWithSlideUp
import com.ourstilt.databinding.ActivitySplashBinding
import com.ourstilt.homepage.ui.HomeActivity
import com.ourstilt.userlogin.ui.LoginActivity
import com.ourstilt.userlogin.ui.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LandingActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }

    private val pagerAdapter by lazy {
        BaseViewPagerAdapter(supportFragmentManager, lifecycle)
    }

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            viewModel.isUserLoggedIn.collect { isLoggedIn ->
                splashScreen.setOnExitAnimationListener { splashScreenViewProvider ->
                    splashScreenViewProvider.remove()
                    val intent = if (isLoggedIn) {
                        Intent(this@LandingActivity, HomeActivity::class.java)
                    } else {
                        Intent(this@LandingActivity, LoginActivity::class.java)
                    }
                    startWithSlideUp(intent)
                    finish()
                }
            }
        }
    }
}