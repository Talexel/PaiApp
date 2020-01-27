package com.talexel.paiapp.data.database.repositories

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.PhoneAuthCredential
import com.talexel.paiapp.data.database.entities.User
import com.talexel.paiapp.data.network.FirebaseAuthApi
import com.talexel.paiapp.data.network.FirestoreApi
import com.talexel.paiapp.exceptions.UserNotAddedException
import com.talexel.paiapp.exceptions.UserNotFoundException

class AuthRepository (
    private val authApi: FirebaseAuthApi,
    private val spinApi: FirestoreApi
) {

    var currentUser = MutableLiveData(authApi.getCurrentUser())
    suspend fun signIn(c: AuthCredential) = authApi.signInWithPhoneNumber(c as PhoneAuthCredential)
    fun signOut() = authApi.signOut()

    suspend fun getUserState(u: User) = spinApi.getUserState(u)

    suspend fun updateUserState(u: User): User{

        val user = spinApi.updateUserState(u)
        if(user != null) return user.toObject(User::class.java)!!
        else throw UserNotAddedException()

    }
}