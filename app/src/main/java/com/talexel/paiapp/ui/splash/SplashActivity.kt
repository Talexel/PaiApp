package com.talexel.paiapp.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.talexel.paiapp.R
import com.talexel.paiapp.data.database.repositories.AuthRepository
import com.talexel.paiapp.data.network.FirebaseAuthApi
import com.talexel.paiapp.databinding.ActivitySplashBinding
import com.talexel.paiapp.ui.interfaces.SplashUIUpdator
import com.talexel.paiapp.ui.login.LoginActivity
import com.talexel.paiapp.ui.login.LoginViewModel
import com.talexel.paiapp.ui.login.LoginViewModelFactory
import com.talexel.paiapp.ui.spin.MainActivity

class SplashActivity : AppCompatActivity(), SplashUIUpdator {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivitySplashBinding = DataBindingUtil.setContentView(this,
            R.layout.activity_splash
        )

        val api = FirebaseAuthApi()
        val repository = AuthRepository(api)
        val factory = LoginViewModelFactory { SplashViewModel(repository) }
        val viewModel = ViewModelProviders.of(this, factory)[SplashViewModel::class.java]

        binding.authViewModel = viewModel
        viewModel.uiUpdator = this
        viewModel.runUIUpdator()

    }

    override fun<T> updateUI(cls: Class<T>) {
        Intent(this, cls).also {
            Toast.makeText(this, "Called Update UI to class: ${cls.canonicalName}", Toast.LENGTH_LONG).show()
            it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(it)
        }
    }
}
