package com.talexel.paiapp.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Spin(
    @PrimaryKey(autoGenerate = true)
    var spinId: String? = null,
    var spinValue: Int? = null,
    var createdOn: String? = null
)