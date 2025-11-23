package com.example.walleteam.data.repository

import com.example.walleteam.data.model.Member
import com.example.walleteam.data.remote.ApiService
import retrofit2.Response
import javax.inject.Inject // 👈 Importante


class MemberRepository @Inject constructor(
    private val api: ApiService
) {

    suspend fun getMembersByPlan(planId: String): Response<List<Member>> {
        return api.getMembersByPlan(planId)
    }

    suspend fun createMember(memberData: Map<String, Any>): Response<Member> {
        return api.createMember(memberData)
    }

    suspend fun deleteMember(memberId: String): Response<Unit> {
        return api.deleteMember(memberId)
    }
}
