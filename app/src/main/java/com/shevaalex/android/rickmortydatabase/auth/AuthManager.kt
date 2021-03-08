package com.shevaalex.android.rickmortydatabase.auth

import com.shevaalex.android.rickmortydatabase.models.AuthToken
import kotlinx.coroutines.flow.StateFlow

interface AuthManager {

    val defaultToken: AuthToken

    val token: StateFlow<AuthToken?>

    suspend fun getNewToken()

}