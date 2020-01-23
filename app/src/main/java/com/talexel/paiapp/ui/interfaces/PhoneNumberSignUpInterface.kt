package com.talexel.paiapp.ui.interfaces

interface PhoneNumberSignUpInterface {

    fun onPhoneEntered()
    fun<T> onOTPVerified(cls: Class<T>)

}