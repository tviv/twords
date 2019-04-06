package com.tuvv.tword.utils.extentions

import android.content.Context
import android.widget.Toast
import com.tuvv.tword.R

fun Context.showMessage(text: String) {
    Toast.makeText(this.applicationContext, text, Toast.LENGTH_SHORT).show()
}

fun Context.showMessage(textId: Int) {
        showMessage(getString(textId))
}

fun Context.showErrorMessage(text: String) {
    showMessage("${getString(R.string.error).capitalize()}: ${text}")
}

