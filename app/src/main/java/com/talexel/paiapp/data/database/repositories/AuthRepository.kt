package com.talexel.paiapp.data.database.repositories

import android.util.Log
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
import com.talexel.paiapp.ui.login.LoginActivity.Companion.TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthRepository (
    private val authApi: FirebaseAuthApi,
    private val spinApi: FirestoreApi
) {

    var currentUser = MutableLiveData<User>()
    init {
        CoroutineScope(Dispatchers.Default).launch {
            Log.d("Auth Repository", "Called Init Current User State.")
            currentUser.postValue(syncSignedState())
        }
    }

    suspend fun syncSignedState(): User{
        val user = authApi.getCurrentUser()
        val c = getUserState(User(uid = user?.uid))
        Log.d(TAG, "$c")
        return when(c == null) {
            true -> {
                when(user != null) {
                    true -> updateUserState(User(
                            uid = user!!.uid,
                            userPhoneNumber = user!!.phoneNumber
                        ))
                    false -> User()
                }
            }
            false -> {
                val toU = c.toObject(User::class.java)
                currentUser.postValue(toU)
                toU!!
            }
        }
    }

    suspend fun signIn(c: AuthCredential): User {
        authApi.signInWithPhoneNumber(c as PhoneAuthCredential)
        return syncSignedState()
    }

    fun signOut() = authApi.signOut()

    suspend fun getUserState(u: User) = spinApi.getUserState(u)

    suspend fun updateUserState(u: User): User{

        val user = spinApi.updateUserState(u)
        if(user != null) return user.toObject(User::class.java)!!
        else throw UserNotAddedException()

    }
}