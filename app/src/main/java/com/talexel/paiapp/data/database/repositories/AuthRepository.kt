package com.talexel.paiapp.data.database.repositories

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.PhoneAuthCredential
import com.talexel.paiapp.data.database.entities.User
import com.talexel.paiapp.data.network.FirebaseAuthApi
import com.talexel.paiapp.exceptions.UserNotAddedException

class AuthRepository (
    private val api: FirebaseAuthApi
) {

    var currentUser = MutableLiveData(api.getCurrentUser())
    suspend fun signIn(c: AuthCredential) = api.signInWithPhoneNumber(c as PhoneAuthCredential)
    fun signOut() = api.signOut()
    suspend fun updateUserState(u: User): User{

        val user = api.updateUserState(u)
        if(user != null) return user.toObject(User::class.java)!!
        else throw UserNotAddedException()

    }
}