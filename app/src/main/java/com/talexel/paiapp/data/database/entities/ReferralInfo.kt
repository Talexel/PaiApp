package com.talexel.paiapp.data.database.entities

data class ReferralInfo(
    var referredBy: String? = null,
    var referralCode: String? = null,
    var referralStatus: ReferralStatus = ReferralStatus.NIL
) {
    companion object {
        enum class ReferralStatus {
            NIL,
            REJECTED,
            PROVIDED
        }
    }
}