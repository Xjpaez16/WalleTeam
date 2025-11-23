package com.example.walleteam.data.model
import com.google.gson.annotations.SerializedName

data class User(

    @SerializedName("_id") private val mongoId: String?,
    @SerializedName("id") private val simpleId: String?,

    val email: String?,
    val name: String?,
    val plans: List<String>? = emptyList(),
    val createdAt: String?
) {

    val id: String
        get() = mongoId ?: simpleId ?: ""
}
