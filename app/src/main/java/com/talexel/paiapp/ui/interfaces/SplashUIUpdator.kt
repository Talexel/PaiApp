package com.talexel.paiapp.ui.interfaces

import com.talexel.paiapp.ui.splash.SplashViewModel

interface SplashUIUpdator {
    fun updateState(s: SplashViewModel.Companion.SplashGoState)
}