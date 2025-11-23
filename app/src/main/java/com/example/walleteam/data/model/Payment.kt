package com.example.walleteam.data.model

import com.google.gson.annotations.SerializedName

data class Payment(
    @SerializedName("_id") val id: String? = null,

    val memberId: String,
    val planId: String,
    val amount: Double,
    val date: String
)
