package com.shevaalex.android.rickmortydatabase.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * Use everywhere except from Activity (Custom View, Fragment, Dialogs, DialogFragments).
 */
fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}
