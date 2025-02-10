package com.mystilt.base.data.repository


import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.mystilt.base.data.api.PreferencesKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    //data store repo
    val isUserLoggedIn: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.IS_USER_LOGGED] ?: false
    }

    val isProfileCreated: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.IS_PROFILE_CREATED] ?: false
    }

    suspend fun setUserLoggedIn(isLoggedIn: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_USER_LOGGED] = isLoggedIn
        }
    }

    suspend fun setProfileCreated(isCreated: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_PROFILE_CREATED] = isCreated
        }
    }

    suspend fun clearUserData() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

