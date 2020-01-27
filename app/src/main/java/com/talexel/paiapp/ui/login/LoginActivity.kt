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
import com.talexel.paiapp.ui.spin.MainActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var loginBinding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val api = FirebaseAuthApi()
        val fStoreApi = FirestoreApi()
        val repository = AuthRepository(api, fStoreApi)
        val factory = LoginViewModelFactory {LoginViewModel(this, repository)}

        viewModel = ViewModelProviders.of(this, factory)[LoginViewModel::class.java]
        addSignUpListener()

        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        loginBinding.loginViewModel = viewModel
        initCountryCode()

    }

    private fun updateSignUpView(v: LoginViewModel.Companion.SignupState){
        if(v != LoginViewModel.Companion.SignupState.PHONE_SCREEN) loginBinding.llPhoneNumber.visibility = View.GONE else loginBinding.llPhoneNumber.visibility = View.VISIBLE
        if(v != LoginViewModel.Companion.SignupState.OTP_SCREEN) loginBinding.llOtp.visibility = View.GONE else loginBinding.llOtp.visibility = View.VISIBLE
        if(v == LoginViewModel.Companion.SignupState.AUTHENTICATED) {
            Log.d(TAG, "Called UpdateSignUpView for Authenticated State")
            changeOnAuthenticated(MainActivity::class.java)
        }
    }

    private fun updateState(v: LoginViewModel.Companion.SignupState) {
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


    fun<T> changeOnAuthenticated(cls: Class<T>) {
        Toast.makeText(this, "Updated the View on OTP Screen", Toast.LENGTH_LONG).show()
        Intent(this, cls).let {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            Log.d(TAG, "__CALLING__ Activity Login...")
            startActivity(it)
        }
    }


    companion object {

        val TAG = LoginActivity::class.java.canonicalName

    }

}