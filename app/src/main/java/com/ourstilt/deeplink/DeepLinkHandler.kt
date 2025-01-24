package com.ourstilt.deeplink

import android.content.Context
import android.content.Intent
import com.ourstilt.homepage.ui.HomeActivity
import com.ourstilt.search.ui.SearchActivity
import com.ourstilt.userCustomMenu.ui.CustomMenuActivity
import com.ourstilt.userlogin.ui.LoginActivity
import timber.log.Timber

object DeepLinkHandler {
    fun createIntent(context: Context, deepLinkResponse: DeepLinkResponse): Intent? {
        return when (deepLinkResponse.type) {
            "home" -> {
                HomeActivity.newIntent(context, deepLinkResponse)
            }

            "login" -> {
                LoginActivity.newIntent(context, deepLinkResponse)
            }

            "user_menu" -> {
                CustomMenuActivity.newIntent(context, deepLinkResponse)
            }

            "search_page" -> {
                SearchActivity.newIntent(context, deepLinkResponse)
            }

            else -> {
                Timber.e(">>>>>>>>>>> Unknown deep link type: ${deepLinkResponse.type}")
                null
            }
        }
    }
}