package com.talexel.paiapp.data.database.repositories

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
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

    var currentUser = MutableLiveData<FirebaseUser?>()
    init {
        currentUser.postValue(authApi.getCurrentUser())
    }

    suspend fun signIn(c: AuthCredential): AuthResult {
        val v = authApi.signInWithPhoneNumber(c as PhoneAuthCredential)
        currentUser.postValue(authApi.getCurrentUser())
        return v
    }
    fun signOut() = authApi.signOut()

    suspend fun getUserState(u: User) = spinApi.getUserState(u)

    suspend fun updateUserState(u: User): User{

        val user = spinApi.updateUserState(u)
        if(user != null) return user.toObject(User::class.java)!!
        else throw UserNotAddedException()

    }
}