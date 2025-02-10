package com.mystilt.deeplink

import android.content.Context
import android.content.Intent
import com.mystilt.homepage.ui.HomeActivity
import com.mystilt.search.ui.SearchActivity
import com.mystilt.userCustomMenu.ui.CustomMenuActivity
import com.mystilt.userlogin.ui.LoginActivity
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