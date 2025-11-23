package com.example.walleteam.data.model

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    val token: String?,
    val userId: String?,
    @SerializedName("_id") val _id: String?,
    val user: User?
)
