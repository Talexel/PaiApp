package com.talexel.paiapp.ui.spin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.talexel.paiapp.R
import com.talexel.paiapp.data.database.repositories.AuthRepository
import com.talexel.paiapp.data.network.FirebaseAuthApi
import com.talexel.paiapp.data.network.FirestoreApi
import com.talexel.paiapp.ui.login.LoginActivity
import com.talexel.paiapp.ui.login.LoginViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity: AppCompatActivity() {

    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        val navController = Navigation.findNavController(this, R.id.nav_fragment)
        NavigationUI.setupWithNavController(nav_view, navController)
        NavigationUI.setupActionBarWithNavController(this, navController, main_drawer_layout)

        val api = FirebaseAuthApi()
        val fStoreApi = FirestoreApi()

        val repo = AuthRepository(api, fStoreApi)

        val factory = LoginViewModelFactory { MainViewModel(this, repo) }
        viewModel = ViewModelProviders.of(this, factory)[MainViewModel::class.java]

        authStateObserver()
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(
            Navigation.findNavController(this, R.id.nav_fragment),
            main_drawer_layout
        )
    }

    fun authStateObserver(){
        viewModel.currrentAuthState.observe(
            this,
            Observer {
                if(it == MainViewModel.Companion.AuthenticationState.UN_AUTHENTICATED){
                    Intent(this, LoginActivity::class.java).also {
                        it.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(it)
                    }
                }
            }
        )
    }
}