package com.talexel.paiapp.ui.splash

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.talexel.paiapp.data.database.entities.ReferralInfo
import com.talexel.paiapp.data.database.repositories.AuthRepository
import com.talexel.paiapp.ui.interfaces.SplashUIUpdator
import com.talexel.paiapp.ui.login.LoginActivity
import com.talexel.paiapp.ui.spin.MainActivity

class SplashViewModel(
    val authRepository: AuthRepository
) : ViewModel() {

    companion object {
        val TAG = SplashActivity::class.java.canonicalName
        enum class SplashGoState{
            NIL,
            SIGNUP,
            LOGIN,
            MAIN
        }
    }

    val currentState = MutableLiveData<SplashGoState>()
    init {
        currentState.postValue(SplashGoState.NIL)
    }

    fun runUIUpdator() {

        authRepository.currentUser.value?.let {
            user ->
                if (user.uid == null) currentState.postValue(SplashGoState.LOGIN)
                else if (user.referralInfo?.referralStatus == ReferralInfo.Companion.ReferralStatus.NIL) currentState.postValue(
                    SplashGoState.SIGNUP
                )
                else currentState.postValue(SplashGoState.MAIN)
        } ?: currentState.postValue(SplashGoState.LOGIN)
    }
}