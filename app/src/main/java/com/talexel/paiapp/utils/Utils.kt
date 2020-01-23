package com.talexel.paiapp.utils

import android.content.Context
import android.widget.Toast

object Utils {

    fun verifyNonEmpty(appContext: Context, s: String?, name: String): Boolean{
        if(s.isNullOrEmpty()) {
            Toast.makeText(appContext, "$name is Empty", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

}