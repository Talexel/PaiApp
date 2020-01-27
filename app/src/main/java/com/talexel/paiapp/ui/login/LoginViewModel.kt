package com.talexel.paiapp.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.talexel.paiapp.data.database.entities.User
import com.talexel.paiapp.data.database.repositories.AuthRepository
import com.talexel.paiapp.ui.interfaces.PhoneNumberSignUpInterface
import com.talexel.paiapp.ui.spin.MainActivity
import com.talexel.paiapp.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.TimeUnit

class LoginViewModel(
    private val activity: Activity,
    private val authRepository: AuthRepository
) : ViewModel() {

    companion object {
        val OTP_TIME_OUT_TIME = 60L
        val OTP_TIME_OUT_UNITS = TimeUnit.SECONDS

        enum class SignupState(val v: Int) {
            NIL(-1),
            PHONE_SCREEN(0),
            OTP_SCREEN(1),
            AUTHENTICATED(2),
            REFERRAL_CODE(999);

            companion object {
                fun from(v: Int) = values().first { it.v == v }
            }
        }

        val TAG = LoginViewModel::class.java.canonicalName
    }

    var signupStateLive = MutableLiveData<SignupState>()

    init {
        signupStateLive.postValue(SignupState.PHONE_SCREEN)
    }

    var phoneNumber: String? = null
    var countryCode: String? = null
    var otpCode: String? = null

    var phoneAuthCredential: PhoneAuthCredential? = null

    var verificationId: String? = null

    private val phoneVerificationCallback = object: PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
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
            !Utils.verifyNonEmpty(activity.applicationContext, countryCode, "Country Code") ||
            !Utils.verifyNonEmpty(activity.applicationContext, phoneNumber, "Phone Number")
        ) return
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            getPhoneFormatted(),
            OTP_TIME_OUT_TIME,
            OTP_TIME_OUT_UNITS,
            activity,
            phoneVerificationCallback
        )
        updateState(SignupState.OTP_SCREEN)
    }

    fun submittedOTP(){

        if(
            Utils.verifyNonEmpty(activity.applicationContext, verificationId, "Verification Code") &&
            Utils.verifyNonEmpty(activity.applicationContext, otpCode, "OTPCode")
        ) {
            phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId!!, otpCode!!)
            onOTPEntered()
        }

    }

    fun onOTPEntered() {

        phoneAuthCredential?.let {
            if(verifyOTP()) {
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        authRepository.signIn(it)
                        val u = authRepository.getUserState(
                            User(uid = authRepository.currentUser.value?.uid)
                        )
                        if(u == null) {
                            val tm = Calendar.getInstance().timeInMillis.toString()
                            authRepository.updateUserState(
                                User(
                                    authRepository.currentUser.value?.uid,
                                    createdOn = tm,
                                    updatedOn = tm
                                )
                            )
                            updateState(SignupState.REFERRAL_CODE)
                        }
                        else { updateState(SignupState.AUTHENTICATED) }
                    } catch (e: Exception) {
                        Log.d(TAG, "Error Sigin with Exception: $e")
                    }
                }
            }
        }

    }

    fun onReferralCodeEntered(){

    }

    fun verifyOTP(): Boolean{
        return Utils.verifyNonEmpty(activity.applicationContext, otpCode, "OTP")
    }

    fun updateState(s: SignupState){
        signupStateLive.postValue(s)
    }

}