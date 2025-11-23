package com.example.walleteam.data.model

import com.google.gson.annotations.SerializedName
import java.util.UUID


data class Plan(
    @SerializedName("_id") val id: String? = UUID.randomUUID().toString(),
    val name: String,
    val motive: String?,
    val targetAmount: Double,
    val months: Int,
    val createdAt: String
)