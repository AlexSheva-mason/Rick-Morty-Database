package com.shevaalex.android.rickmortydatabase.auth

import android.content.SharedPreferences
import com.google.gson.Gson
import com.shevaalex.android.rickmortydatabase.models.AuthToken
import com.shevaalex.android.rickmortydatabase.utils.Constants.Companion.KEY_AUTH_TOKEN
import com.shevaalex.android.rickmortydatabase.utils.currentTimeMinutes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthManagerImpl
@Inject
constructor(
        private val firebaseService: FirebaseService,
        private val sharedPref: SharedPreferences
) : AuthManager {

    override val defaultToken = AuthToken("-1", 0L)

    private val _token = MutableStateFlow(getAuthTokenFromSharedPrefs())
    override val token: StateFlow<AuthToken?> = _token

    override suspend fun getNewToken() {
        Timber.v("getNewToken()")
        val token = firebaseService.firebaseAnonymAuth()
        val timestamp = currentTimeMinutes()
        token?.let {
            saveTokenToSharedPrefsAndSet(AuthToken(it, timestamp))
        }
    }

    private fun getAuthTokenFromSharedPrefs(): AuthToken {
        val tokenJson = sharedPref.getString(KEY_AUTH_TOKEN, null)
        return tokenJson?.let {
            val token = Gson().fromJson(tokenJson, AuthToken::class.java)
            Timber.v("getAuthTokenFromSharedPrefs() token: %s", token.token.takeLast(7))
            token
        } ?: run {
            Timber.e("getAuthTokenFromSharedPrefs() token is null, returning default")
            defaultToken
        }
    }

    private fun saveTokenToSharedPrefsAndSet(token: AuthToken) {
        Timber.v(
                "saving token to shared prefs and setting MutableStateFlow token: %s",
                token.token.takeLast(7)
        )
        val tokenJson = Gson().toJson(token)
        sharedPref.edit().putString(KEY_AUTH_TOKEN, tokenJson).apply()
        _token.value = token
    }

}