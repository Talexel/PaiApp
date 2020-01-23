package com.talexel.paiapp.data.network

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.talexel.paiapp.data.database.entities.User
import kotlinx.coroutines.tasks.await

class FirebaseAuthApi {

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firebaseStore by lazy {
        FirebaseFirestore.getInstance()
    }

    suspend fun signInWithPhoneNumber(authCredential: PhoneAuthCredential)
            = firebaseAuth.signInWithCredential(authCredential).await()
    suspend fun updateUserState(u: User): DocumentSnapshot? {
        val d = firebaseStore
            .collection(FirebasePathDescriptor.USER_PATH)
            .document(u.toMap()[User.PRIMARY_KEY]!!)

            d.set(u).await()
        return d.get().await()
    }

    fun signOut() = firebaseAuth.signOut()
    fun getCurrentUser() = firebaseAuth.currentUser

}