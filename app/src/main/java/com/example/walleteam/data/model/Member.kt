package com.example.walleteam.data.model

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class Member(
    @SerializedName("_id") val id: String? = UUID.randomUUID().toString(),
    val name: String,
    val planId: String,
    val contributionPerMonth: Double,
    val joinedAt: String
)