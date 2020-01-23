package com.talexel.paiapp.ui.login

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.talexel.paiapp.data.database.repositories.AuthRepository
import com.talexel.paiapp.ui.interfaces.PhoneNumberSignUpInterface
import com.talexel.paiapp.ui.spin.MainActivity
import com.talexel.paiapp.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    companion object {
        val OTP_TIME_OUT_TIME = 60L
        val OTP_TIME_OUT_UNITS = TimeUnit.SECONDS

        val TAG = LoginViewModel::class.java.canonicalName
    }

    var phoneNumber: String? = null
    var countryCode: String? = null
    var otpCode: String? = null

    lateinit var activityRef: WeakReference<Activity>

    var loginUpdateListener: PhoneNumberSignUpInterface? = null
    var phoneAuthCredential: PhoneAuthCredential? = null

    var verificationId: String? = null

    private val phoneVerificationCallback = object: PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
            loginUpdateListener?.onPhoneEntered()
            Log.d(TAG, "Listening for Code Sent.")
            verificationId = p0
        }

        override fun onCodeAutoRetrievalTimeOut(p0: String) {
            super.onCodeAutoRetrievalTimeOut(p0)
            Log.d(TAG, "OTP receiver TimedOut.")
        }

        override fun onVerificationCompleted(p0: PhoneAuthCredential) {
            Log.d(TAG, "Verification Completed!")
            phoneAuthCredential = p0
            otpCode = p0.smsCode
            onOTPEntered()
        }

        override fun onVerificationFailed(p0: FirebaseException) {
            Log.d(TAG, "OTP Verification Failed with Exception: $p0")
        }
    }

    private fun getPhoneFormatted(): String {
        val d = "+$countryCode $phoneNumber"
        Log.d(TAG, "Phone Number is: $d")
        return d
    }

    fun updatePhoneNumber(){
        if(
            activityRef.get() == null ||
            !Utils.verifyNonEmpty(activityRef.get()!!.applicationContext, countryCode, "Country Code") ||
            !Utils.verifyNonEmpty(activityRef.get()!!.applicationContext, phoneNumber, "Phone Number")
        ) return

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            getPhoneFormatted(),
            OTP_TIME_OUT_TIME,
            OTP_TIME_OUT_UNITS,
            activityRef.get() as Activity,
            phoneVerificationCallback
        )
    }

    fun submittedOTP(){

        activityRef.get()?.let {

            if(
                Utils.verifyNonEmpty(it.applicationContext, verificationId, "Verification Code") &&
                Utils.verifyNonEmpty(it.applicationContext, otpCode, "OTPCode")
            ) {
                phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId!!, otpCode!!)
                onOTPEntered()
            }

        }


    }

    fun onOTPEntered() {

        phoneAuthCredential?.let {
            if(verifyOTP()) {
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        authRepository.signIn(it)
                        loginUpdateListener?.onOTPVerified(MainActivity::class.java)
                    } catch (e: Exception) {
                        Log.d(TAG, "Error Sigin with Exception: $e")
                    }
                }
            }
        }

    }

    fun verifyOTP(): Boolean{
        return activityRef.get()?.let {
            (Utils.verifyNonEmpty(it.applicationContext, otpCode, "OTP"))
        } ?: false

    }

}