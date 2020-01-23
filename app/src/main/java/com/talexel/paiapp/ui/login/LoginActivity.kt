package com.talexel.paiapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.talexel.paiapp.R
import com.talexel.paiapp.data.database.repositories.AuthRepository
import com.talexel.paiapp.data.network.FirebaseAuthApi
import com.talexel.paiapp.data.network.FirestoreApi
import com.talexel.paiapp.databinding.ActivityLoginBinding
import com.talexel.paiapp.ui.interfaces.PhoneNumberSignUpInterface
import com.talexel.paiapp.ui.spin.MainActivity
import java.lang.ref.WeakReference

class LoginActivity : AppCompatActivity(), PhoneNumberSignUpInterface {

    private lateinit var loginBinding: ActivityLoginBinding
    private lateinit var currentState: SignupState
    private lateinit var viewModel: LoginViewModel

    private fun updateSignUpView(){
        if(currentState != SignupState.PHONE_SCREEN) loginBinding.llPhoneNumber.visibility = View.GONE else loginBinding.llPhoneNumber.visibility = View.VISIBLE
        if(currentState != SignupState.OTP_SCREEN) loginBinding.llOtp.visibility = View.GONE else loginBinding.llOtp.visibility = View.VISIBLE
    }

    private fun updateState(v: SignupState) {
        if(v == currentState) return
        currentState = v
        updateSignUpView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val api = FirebaseAuthApi()
        val repository = AuthRepository(api)
        val factory = LoginViewModelFactory {LoginViewModel(repository)}

        val viewModel = ViewModelProviders.of(this, factory)[LoginViewModel::class.java]
        viewModel.activityRef = WeakReference(this)
        viewModel.loginUpdateListener = this

        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        loginBinding.loginViewModel = viewModel

        viewModel.countryCode = loginBinding.countryCode.defaultCountryCode
        loginBinding.countryCode.setOnCountryChangeListener {
            viewModel.countryCode = loginBinding.countryCode.selectedCountryCode
        }

        currentState = SignupState.NIL

        initUI()
    }

    fun initUI(){
        updateState(SignupState.PHONE_SCREEN)
    }

    override fun onPhoneEntered() {
        updateState(SignupState.OTP_SCREEN)
    }

    override fun<T> onOTPVerified(cls: Class<T>) {
        Toast.makeText(this, "Updated the View on OTP Screen", Toast.LENGTH_LONG).show()
        Intent(this, MainActivity::class.java).let {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            Log.d(TAG, "__CALLING__ Activity Login...")
            startActivity(it)
        }
    }


    companion object {

        private enum class SignupState(val v: Int) {
            NIL(-1),
            PHONE_SCREEN(0),
            OTP_SCREEN(1);

            companion object {
                fun from(v: Int) = values().first { it.v == v }
            }
        }
        val TAG = LoginActivity::class.java.canonicalName

    }

}