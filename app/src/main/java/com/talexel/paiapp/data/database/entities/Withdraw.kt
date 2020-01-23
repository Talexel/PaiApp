package com.talexel.paiapp.data.database.entities

import androidx.room.Entity

@Entity
data class Withdraw(
    var withdrawId: String? = null,
    var amountWithdrawn: String? = null,
    var createdOn: String? = null
)