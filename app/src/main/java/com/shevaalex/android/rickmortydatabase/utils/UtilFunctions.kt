package com.shevaalex.android.rickmortydatabase.utils

fun currentTimeHours(): Long {
    return System.currentTimeMillis()/3600000
}

fun currentTimeMinutes(): Long {
    return System.currentTimeMillis()/60000
}