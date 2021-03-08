package com.shevaalex.android.rickmortydatabase.utils

fun currentTimeDays(): Int {
    return (System.currentTimeMillis() / 86400000).toInt()
}

fun currentTimeHours(): Long {
    return System.currentTimeMillis()/3600000
}

fun currentTimeMinutes(): Long {
    return System.currentTimeMillis()/60000
}