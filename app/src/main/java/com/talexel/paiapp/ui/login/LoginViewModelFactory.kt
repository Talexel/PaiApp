package com.talexel.paiapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.talexel.paiapp.data.database.repositories.AuthRepository

class LoginViewModelFactory(
    private val creator: () -> ViewModel
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return creator() as T
    }
}