package com.talexel.paiapp.utils

import android.content.Context
import android.widget.Toast

fun Context.toastLong(m: String) {
    Toast.makeText(this, m, Toast.LENGTH_LONG).show()
}