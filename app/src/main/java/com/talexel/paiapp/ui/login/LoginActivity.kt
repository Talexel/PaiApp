package com.talexel.paiapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.talexel.paiapp.R
import com.talexel.paiapp.data.database.repositories.AuthRepository
import com.talexel.paiapp.data.network.FirebaseAuthApi
import com.talexel.paiapp.data.network.FirestoreApi
import com.talexel.paiapp.databinding.ActivityLoginBinding
import com.talexel.paiapp.ui.signup.SignupActivity
import com.talexel.paiapp.ui.spin.MainActivity
import com.talexel.paiapp.utils.addVisual
import com.talexel.paiapp.utils.removeVisual
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var loginBinding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    private var stateRunner = hashMapOf<LoginViewModel.Companion.SignupState, () -> Unit>()

    private val SIGNUP_CLASS = SignupActivity::class.java
    private val MAIN_CLASS = MainActivity::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val api = FirebaseAuthApi()
        val fStoreApi = FirestoreApi()
        val repository = AuthRepository(api, fStoreApi)
        val factory = LoginViewModelFactory {LoginViewModel(this, repository)}

        initStateUpdater()

        viewModel = ViewModelProviders.of(this, factory)[LoginViewModel::class.java]
        addSignUpListener()

        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        loginBinding.loginViewModel = viewModel
        initCountryCode()

    }

    private fun updateSignUpView(v: LoginViewModel.Companion.SignupState){
        stateRunner[v]?.invoke()
    }

    private fun updateState(v: LoginViewModel.Companion.SignupState) {
        Log.d(TAG, "Updated Status to: $v")
        updateSignUpView(v)
    }

    fun addSignUpListener(){
        viewModel.signupStateLive.observe(
            this,
            Observer { updateState(it) }
        )
    }

    fun initCountryCode(){
        viewModel.countryCode = loginBinding.countryCode.defaultCountryCode
        loginBinding.countryCode.setOnCountryChangeListener {
            viewModel.countryCode = loginBinding.countryCode.selectedCountryCode
        }
    }

    fun initStateUpdater(){
        stateRunner[LoginViewModel.Companion.SignupState.PHONE_SCREEN] = this::phoneVisual
        stateRunner[LoginViewModel.Companion.SignupState.OTP_SCREEN] = this::otpVisual
        stateRunner[LoginViewModel.Companion.SignupState.REFERRAL_CODE] =  this::referralCodeVisual
        stateRunner[LoginViewModel.Companion.SignupState.AUTHENTICATED] =  this::changeOnAuthenticated
        stateRunner[LoginViewModel.Companion.SignupState.SIGNUP] = this::changeOnSignUp
    }

    fun otpVisual(){

        ll_phone_number.removeVisual()
        ll_otp.addVisual()
    }

    fun phoneVisual(){
        ll_otp.removeVisual()
        ll_phone_number.addVisual()
    }

    fun changeOnSignUp(){
        Intent(this, SIGNUP_CLASS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            Log.d(TAG, "__CALLING__ Activity Signup")
            startActivity(this)
        }
    }

    fun referralCodeVisual(){
        ll_phone_number.removeVisual()
        ll_otp.removeVisual()
    }

    fun changeOnAuthenticated() {
        Toast.makeText(this, "Updated the View on OTP Screen", Toast.LENGTH_LONG).show()
        Intent(this, MainActivity::class.java).let {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            Log.d(TAG, "__CALLING__ Activity Login...")
            startActivity(it)
        }
    }


    companion object {

        val TAG = LoginActivity::class.java.canonicalName

    }

}