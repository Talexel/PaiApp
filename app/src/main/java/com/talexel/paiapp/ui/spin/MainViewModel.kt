package com.talexel.paiapp.ui.spin

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.talexel.paiapp.data.database.repositories.AuthRepository

class MainViewModel(
    activity: Activity,
    authRepository: AuthRepository
) : ViewModel(){

    companion object {
        enum class AuthenticationState {
            AUTHENTICATED,
            UN_AUTHENTICATED
        }
    }

    var currrentAuthState = MutableLiveData<AuthenticationState>()

    init {
        authRepository.currentUser.observeForever {
            currrentAuthState.postValue(when(it == null) {
                true -> AuthenticationState.UN_AUTHENTICATED
                false -> AuthenticationState.AUTHENTICATED
            })
        }
    }

}