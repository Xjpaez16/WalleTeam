package com.example.walleteam.data.repository

import com.example.walleteam.data.model.Plan
import com.example.walleteam.data.remote.ApiService
import retrofit2.Response
import javax.inject.Inject

class PlanRepository @Inject constructor(
    private val api: ApiService
) {

    suspend fun getMyPlans() = api.getMyPlans()

    suspend fun createPlan(planData: Map<String, Any>): Response<Plan> = api.createPlan(planData)

    suspend fun getPlanDetails(planId: String) = api.getPlanDetails(planId)
}