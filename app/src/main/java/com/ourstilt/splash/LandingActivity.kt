package com.ourstilt.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.ourstilt.base.BaseViewPagerAdapter
import com.ourstilt.common.startWithSlideUp
import com.ourstilt.databinding.ActivitySplashBinding
import com.ourstilt.homepage.HomeActivity
import com.ourstilt.userlogin.ui.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LandingActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }

    private val pagerAdapter by lazy {
        BaseViewPagerAdapter(supportFragmentManager, lifecycle)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setOnExitAnimationListener { splashScreenViewProvider ->
            splashScreenViewProvider.remove()
            val intent = Intent(this, HomeActivity::class.java)
            startWithSlideUp(intent)
            finish()
        }
    }
}