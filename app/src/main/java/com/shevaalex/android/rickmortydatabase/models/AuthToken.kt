package com.shevaalex.android.rickmortydatabase.models

import androidx.annotation.Keep

@Keep
data class AuthToken(
        val token: String,
        val timestamp: Long
)
