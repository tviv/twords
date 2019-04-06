package com.tuvv.tword.utils.extentions

import android.view.View

fun View.setVisible(value: Boolean) {
    visibility = if (value) View.VISIBLE else View.GONE
}

fun View.getVisible(): Boolean = visibility == View.VISIBLE
