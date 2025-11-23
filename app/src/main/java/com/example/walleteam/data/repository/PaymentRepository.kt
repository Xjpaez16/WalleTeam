package com.example.walleteam.data.repository

import com.example.walleteam.data.model.Payment
import com.example.walleteam.data.remote.ApiService
import javax.inject.Inject

class PaymentRepository @Inject constructor(
    private val api: ApiService
) {

    suspend fun registerPayment(payment: Payment) = api.registerPayment(payment)

    suspend fun getPaymentsByPlan(planId: String) = api.getPaymentsByPlan(planId)
}