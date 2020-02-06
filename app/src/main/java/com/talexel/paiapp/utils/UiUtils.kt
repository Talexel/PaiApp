package com.talexel.paiapp.utils

import android.content.Context
import android.view.View
import android.widget.Toast

fun Context.toastLong(m: String) {
    Toast.makeText(this, m, Toast.LENGTH_LONG).show()
}

fun View.removeVisual(){
    this.visibility = View.GONE
}

fun View.addVisual() {
    this.visibility = View.VISIBLE
}