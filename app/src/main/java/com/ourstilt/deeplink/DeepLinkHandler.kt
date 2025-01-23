package com.ourstilt.deeplink

import android.content.Context
import android.content.Intent
import com.ourstilt.homepage.ui.HomeActivity
import com.ourstilt.userCustomMenu.ui.CustomMenuActivity
import com.ourstilt.userlogin.ui.LoginActivity

object DeepLinkHandler {
    fun createIntent(context: Context, deepLinkResponse: DeepLinkResponse): Intent? {
        return when (deepLinkResponse.type) {
            "home" -> HomeActivity.newIntent(context,deepLinkResponse)
            "login" -> LoginActivity.newIntent(context,deepLinkResponse)
            "user_menu" -> CustomMenuActivity.newIntent(context, deepLinkResponse)
            else -> null
        }
    }
}