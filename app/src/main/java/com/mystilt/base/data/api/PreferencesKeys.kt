package com.mystilt.base.data.api

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

// PreferencesKeys.kt
object PreferencesKeys {
    val IS_USER_LOGGED = booleanPreferencesKey("is_user_logged")
    val IS_PROFILE_CREATED = booleanPreferencesKey("is_profile_created")
    val FCM_TOKEN = stringPreferencesKey("fcm_token")
}
