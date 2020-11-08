package com.shevaalex.android.rickmortydatabase.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.afollestad.materialdialogs.MaterialDialog
import com.shevaalex.android.rickmortydatabase.R

/**
 * Use everywhere except from Activity (Custom View, Fragment, Dialogs, DialogFragments).
 */
fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

/**
 * shows a dialog with given text @param errorMessage
 */
fun Activity.displayErrorDialog(errorMessage: String?){
    MaterialDialog(this)
            .show{
                title(R.string.dialog_error_title)
                message(text = errorMessage)
                positiveButton(text = "OK")
            }
}