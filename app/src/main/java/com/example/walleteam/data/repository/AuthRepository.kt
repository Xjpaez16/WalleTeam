package com.example.walleteam.data.repository

import com.example.walleteam.data.model.AuthRequest
import com.example.walleteam.data.model.AuthResponse
import com.example.walleteam.data.remote.ApiService
import retrofit2.Response
import javax.inject.Inject // 👈 Importante

// @Inject le dice a Hilt cómo crear esto
class AuthRepository @Inject constructor(
    private val api: ApiService
) {

    suspend fun register(name: String, email: String, password: String): Response<AuthResponse> {
        return api.registerUser(AuthRequest(name, email, password))
    }

    suspend fun login(name: String, email: String, password: String): Response<AuthResponse> {
        return api.loginUser(AuthRequest(name, email, password))
    }
}
