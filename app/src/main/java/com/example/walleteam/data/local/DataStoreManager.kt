package com.example.walleteam.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow

private val Context.dataStore by preferencesDataStore(name = "session_data")

class DataStoreManager(private val context: Context) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")

        private val USER_ID_KEY = stringPreferencesKey("user_id")
    }



    val tokenFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[TOKEN_KEY]
    }

    val userNameFlow: Flow<String> = context.dataStore.data.map {
        it[USER_NAME_KEY] ?: ""
    }

    val userEmailFlow: Flow<String> = context.dataStore.data.map {
        it[USER_EMAIL_KEY] ?: ""
    }


    val userIdFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[USER_ID_KEY]
    }



    suspend fun saveToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
        }
    }


    suspend fun saveUserId(userId: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID_KEY] = userId
        }
    }

    suspend fun saveUserData(name: String, email: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_NAME_KEY] = name
            prefs[USER_EMAIL_KEY] = email
        }
    }



    suspend fun clearToken() {
        context.dataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
        }
    }

    suspend fun clearUserData() {
        context.dataStore.edit { prefs ->
            prefs.remove(USER_NAME_KEY)
            prefs.remove(USER_EMAIL_KEY)
            prefs.remove(USER_ID_KEY) // 4. Borrar ID también
        }
    }
}
