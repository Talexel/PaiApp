package com.talexel.paiapp.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude

@Entity
data class User(
    @PrimaryKey(autoGenerate = true)
    var uid: String? = null,
    var userEmail: String? = null,
    var userPhoneNumber: String? = null,
    var referralInfo: ReferralInfo? = ReferralInfo(),
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

}