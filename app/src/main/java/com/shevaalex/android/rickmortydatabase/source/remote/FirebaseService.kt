package com.shevaalex.android.rickmortydatabase.source.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseService
@Inject
constructor(
        private val firebaseAuth: FirebaseAuth
) {

    suspend fun firebaseAnonymAuth(): String? {
        val authTask = firebaseAuth.signInAnonymously()
        return try {
            val result = authTask.await()
            result.user?.let {
                getIdToken(it)
            }
        } catch (e: Exception) {
            Timber.e(e, "firebaseAuth.signInAnonymously().await() exception!")
            null
        }
    }

    private suspend fun getIdToken(firebaseUser: FirebaseUser): String? {
        val getTokenTask = firebaseUser.getIdToken(true)
        return try {
            val result = getTokenTask.await()
            Timber.i("firebase getToken:success, token: %s", result.token)
            result.token
        } catch (e: Exception) {
            Timber.e(e, "firebase getIdToken:failure")
            null
        }
    }

}