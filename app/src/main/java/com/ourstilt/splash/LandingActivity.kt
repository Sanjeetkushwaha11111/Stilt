package com.ourstilt.splash

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.ourstilt.base.ui.BaseViewPagerAdapter
import com.ourstilt.common.startWithSlideUp
import com.ourstilt.databinding.ActivitySplashBinding
import com.ourstilt.deeplink.DeepLinkHandler
import com.ourstilt.homepage.ui.HomeActivity
import com.ourstilt.userlogin.ui.LandingViewModel
import com.ourstilt.userlogin.ui.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class LandingActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }

    private val pagerAdapter by lazy {
        BaseViewPagerAdapter(supportFragmentManager, lifecycle)
    }

    private val viewModel: LandingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        val deepLink = intent.data
        observeNavigationEvent()
        Timber.e(">>>>>>>DeepLink: $deepLink")
        viewModel.processDeepLink(deepLink)
        splashScreen.setOnExitAnimationListener { splashScreenViewProvider ->
            splashScreenViewProvider.remove()
            handleDeepLinkAndLanding()
        }
    }

    private fun observeNavigationEvent() {
        viewModel.navigationEvent.observe(this) { event ->
            event.getContentIfNotHandled()?.let { navigation ->
                when (navigation) {
                    is LandingViewModel.SplashNavigation.NavigateToTarget -> {
                        val intent = DeepLinkHandler.createIntent(
                            this@LandingActivity, navigation.deepLinkResponse
                        )
                        intent?.let {
                            startWithSlideUp(it)
                        }
                    }

                    is LandingViewModel.SplashNavigation.NavigateToLogin -> {
                        startWithSlideUp(LoginActivity.newIntent(this@LandingActivity))
                    }

                    is LandingViewModel.SplashNavigation.NavigateToHome -> {
                        startWithSlideUp(HomeActivity.newIntent(this@LandingActivity))
                    }
                }
                finish()
            }
        }
    }

    private fun handleDeepLinkAndLanding() {
        lifecycleScope.launch {
            viewModel.isUserLoggedIn.collect { isLoggedIn ->
                if (isLoggedIn) {
                    startWithSlideUp(HomeActivity.newIntent(this@LandingActivity))
                } else {
                    startWithSlideUp(LoginActivity.newIntent(this@LandingActivity))
                }
            }
        }
    }
}