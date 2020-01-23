package com.talexel.paiapp.ui.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import com.talexel.paiapp.data.database.repositories.AuthRepository
import com.talexel.paiapp.ui.interfaces.SplashUIUpdator
import com.talexel.paiapp.ui.login.LoginActivity
import com.talexel.paiapp.ui.spin.MainActivity

class SplashViewModel(
    val authRepository: AuthRepository
) : ViewModel() {

    companion object {
        val TAG = SplashActivity::class.java.canonicalName
    }

    var uiUpdator: SplashUIUpdator? = null

    fun runUIUpdator() {
        if(authRepository.currentUser.value != null) {
            Log.d(TAG, "User found: ${authRepository.currentUser.value}")
            uiUpdator?.updateUI(MainActivity::class.java)
        } else {
            uiUpdator?.updateUI(LoginActivity::class.java)
        }

    }

}