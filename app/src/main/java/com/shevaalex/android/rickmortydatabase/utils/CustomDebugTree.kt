package com.shevaalex.android.rickmortydatabase.utils

import android.annotation.SuppressLint
import android.util.Log
import timber.log.Timber
import kotlin.math.min

class CustomDebugTree: Timber.DebugTree() {

    companion object {
        private const val MAX_LOG_LENGTH = 4000
    }

   /**method is overriden to provide tag prefix
    * */
    @SuppressLint("LogNotTimber")
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val prefixedTag = tag + "_LOG_TAG"
        if (message.length < MAX_LOG_LENGTH) {
            if (priority == Log.ASSERT) {
                Log.wtf(prefixedTag, message)
            } else {
                Log.println(priority, prefixedTag, message)
            }
            return
        }

        // Split by line, then ensure each line can fit into Log's maximum length.
        var i = 0
        val length = message.length
        while (i < length) {
            var newline = message.indexOf('\n', i)
            newline = if (newline != -1) newline else length
            do {
                val end = min(newline, i + MAX_LOG_LENGTH)
                val part = message.substring(i, end)
                if (priority == Log.ASSERT) {
                    Log.wtf(prefixedTag, part)
                } else {
                    Log.println(priority, prefixedTag, part)
                }
                i = end
            } while (i < newline)
            i++
        }
    }
}