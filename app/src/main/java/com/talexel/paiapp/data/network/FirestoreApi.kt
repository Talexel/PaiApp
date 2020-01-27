package com.talexel.paiapp.data.network

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.talexel.paiapp.data.database.entities.User
import kotlinx.coroutines.tasks.await

class FirestoreApi {

    private val firebaseStore by lazy {
        FirebaseFirestore.getInstance()
    }

    suspend fun updateUserState(u: User): DocumentSnapshot? {
        val d = firebaseStore
            .collection(FirebasePathDescriptor.USER_PATH)
            .document(u.toMap()[User.PRIMARY_KEY]!!)

        d.set(u).await()
        return d.get().await()
    }

    suspend fun getUserState(u: User): DocumentSnapshot? {
        val d = firebaseStore
            .collection(FirebasePathDescriptor.USER_PATH)
            .document(u.toMap()[User.PRIMARY_KEY]!!)
        return d.get().await()
    }

}