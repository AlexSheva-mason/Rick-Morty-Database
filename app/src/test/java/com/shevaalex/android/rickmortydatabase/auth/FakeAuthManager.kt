package com.shevaalex.android.rickmortydatabase.auth

import com.shevaalex.android.rickmortydatabase.models.AuthToken
import com.shevaalex.android.rickmortydatabase.utils.currentTimeMinutes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.random.Random

class FakeAuthManager : AuthManager {

    var isTokenRefetched = false

    override val defaultToken = AuthToken("-1", 0L)
    val expiredToken = AuthToken("testToken", 0L)
    val upTodateToken = AuthToken("testToken", currentTimeMinutes())

    override val token = MutableStateFlow(defaultToken)

    override suspend fun getNewToken() {
        val randomInt = Random.nextInt(999, Int.MAX_VALUE)
        token.value = AuthToken("test_$randomInt", currentTimeMinutes())
        isTokenRefetched = true
    }

}