package com.talexel.paiapp.ui.login

import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class PhoneAuthCallbacks() : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
    override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
        super.onCodeSent(p0, p1)



    }

    override fun onCodeAutoRetrievalTimeOut(p0: String) {
        super.onCodeAutoRetrievalTimeOut(p0)
    }

    override fun onVerificationCompleted(p0: PhoneAuthCredential) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onVerificationFailed(p0: FirebaseException) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}