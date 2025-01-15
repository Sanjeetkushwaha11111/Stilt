package com.ourstilt.base.data

import androidx.datastore.preferences.core.booleanPreferencesKey

// PreferencesKeys.kt
object PreferencesKeys {
    val IS_USER_LOGGED = booleanPreferencesKey("is_user_logged")
    val IS_PROFILE_CREATED = booleanPreferencesKey("is_profile_created")
}
