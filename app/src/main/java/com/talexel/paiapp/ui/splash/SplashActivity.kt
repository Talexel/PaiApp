package com.talexel.paiapp.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.core.os.postDelayed
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import com.talexel.paiapp.R
import com.talexel.paiapp.data.database.repositories.AuthRepository
import com.talexel.paiapp.data.network.FirebaseAuthApi
import com.talexel.paiapp.data.network.FirestoreApi
import com.talexel.paiapp.databinding.ActivitySplashBinding
import com.talexel.paiapp.ui.interfaces.SplashUIUpdator
import com.talexel.paiapp.ui.login.LoginActivity
import com.talexel.paiapp.ui.login.LoginViewModel
import com.talexel.paiapp.ui.login.LoginViewModelFactory
import com.talexel.paiapp.ui.signup.SignupActivity
import com.talexel.paiapp.ui.spin.MainActivity

class SplashActivity : AppCompatActivity() {

    companion object {
        private val TAG = SplashActivity::class.java.canonicalName
    }

    private val WAIT_TIMER_BEFORE_MOVING_TO_LOGIN = 1000L

    lateinit var viewModel: SplashViewModel
    var loginCallHandler: Handler? = null

    val splashActivityCallbacks = hashMapOf<SplashViewModel.Companion.SplashGoState, () -> Unit>()

    val uiUpdateRunnable = Runnable {
        if(::viewModel.isInitialized) {
            Log.d(TAG, "Calling: Run UI Updator")
            viewModel.runUIUpdator()
            viewModel.currentState.observe(this) {
                splashActivityCallbacks[it]?.invoke()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivitySplashBinding = DataBindingUtil.setContentView(this,
            R.layout.activity_splash
        )

        val api = FirebaseAuthApi()
        val fStoreApi = FirestoreApi()

        val repository = AuthRepository(api, fStoreApi)
        val factory = LoginViewModelFactory { SplashViewModel(repository) }
        viewModel = ViewModelProviders.of(this, factory)[SplashViewModel::class.java]

        binding.authViewModel = viewModel

        initSplashActivityCallbacks()

//        Give Some time for Auth Repository to load current User
        loginCallHandler = Handler()
        loginCallHandler!!.postDelayed(uiUpdateRunnable, WAIT_TIMER_BEFORE_MOVING_TO_LOGIN)
    }

    fun initSplashActivityCallbacks() {
        splashActivityCallbacks[SplashViewModel.Companion.SplashGoState.MAIN] = { loadIntent(MainActivity::class.java) }
        splashActivityCallbacks[SplashViewModel.Companion.SplashGoState.SIGNUP] = { loadIntent(SignupActivity::class.java) }
        splashActivityCallbacks[SplashViewModel.Companion.SplashGoState.LOGIN] = { loadIntent(LoginActivity::class.java) }
    }

    fun<T> loadIntent(cls: Class<T>){
        Intent(this, cls).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(this)
        }
    }

    override fun onPause() {
        super.onPause()
        loginCallHandler!!.removeCallbacks(uiUpdateRunnable)
        loginCallHandler = null
    }
}
