package com.example.walleteam.data.remote

import com.example.walleteam.data.model.AuthRequest
import com.example.walleteam.data.model.AuthResponse
import com.example.walleteam.data.model.Member
import com.example.walleteam.data.model.Payment
import com.example.walleteam.data.model.Plan
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Endpoints de Autenticación
    @POST("api/user/register")
    suspend fun registerUser(@Body request: AuthRequest): Response<AuthResponse>

    @POST("api/user/login")
    suspend fun loginUser(@Body request: AuthRequest): Response<AuthResponse>


    // Mostrar lista de planes (del usuario logueado)
    @GET("api/plans/my-plans")
    suspend fun getMyPlans(): Response<List<Plan>>

    //Registrar un plan (Asumimos que el backend le asigna el userId por el Token)
    @POST("api/plans")
    suspend fun createPlan(@Body plan: Map<String, @JvmSuppressWildcards Any>): Response<Plan>

    // Ver detalle de un plan (Verificar el Plan)
    @GET("api/plans/{planId}")
    suspend fun getPlanDetails(@Path("planId") planId: String): Response<Plan>

    // Registrar un pago (POST)
    @POST("api/payments")
    suspend fun registerPayment(@Body payment: Payment): Response<Payment>

    // Consultar pagos de un plan
    @GET("api/payments/plan/{planId}")
    suspend fun getPaymentsByPlan(@Path("planId") planId: String): Response<List<Payment>>
    // Especificaciones de un plan
    @GET("api/members/plan/{planId}")
    suspend fun getMembersByPlan(@Path("planId") planId: String): Response<List<Member>>
//miembros de un plan (GET)

    @POST("api/members")
    suspend fun createMember(@Body member: Map<String, @JvmSuppressWildcards Any>): Response<Member>

    @DELETE("api/members/{memberId}")
    suspend fun deleteMember(@Path("memberId") memberId: String): Response<Unit>
}