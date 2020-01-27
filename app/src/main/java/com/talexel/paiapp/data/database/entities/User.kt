package com.talexel.paiapp.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude

@Entity
data class User(
    @PrimaryKey(autoGenerate = true)
    var uid: String? = null,
    var userEmail: String? = null,
    var referralCode: String? = null,
    var createdOn: String? = null,
    var updatedOn: String? = null
) {

    companion object {
        val UID = "uid"
        val USER_EMAIL = "user_email"
        val REFERRAL_CODE = "referral_code"
        val CREATED_ON = "created_on"
        val UPDATED_ON = "updated_on"

        val PRIMARY_KEY = UID
    }

    @Exclude
    fun toMap() = hashMapOf(
        UID to uid,
        USER_EMAIL to userEmail,
        REFERRAL_CODE to referralCode,
        CREATED_ON to createdOn,
        UPDATED_ON to updatedOn
    )

}