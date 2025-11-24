package com.example.walleteam.viewmodel.member

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walleteam.data.model.Member
import com.example.walleteam.data.repository.MemberRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemberViewModel @Inject constructor(
    private val memberRepository: MemberRepository
) : ViewModel() {

    private val _members = MutableStateFlow<List<Member>>(emptyList())
    val members: StateFlow<List<Member>> = _members

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun loadMembers(planId: String) {

        viewModelScope.launch {
            Log.i("MEMBER_VM", "🔵 Cargando miembros para planId=$planId")
            _loading.value = true
            try {
                val resp = memberRepository.getMembersByPlan(planId)
                if (resp.isSuccessful) {
                    _members.value = resp.body() ?: emptyList()
                } else {
                    _error.value = "Error ${resp.code()}"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun addMember(member: Member, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _loading.value = true

            val memberMap = mapOf(
                "name" to member.name,
                "planId" to member.planId,
                "contributionPerMonth" to member.contributionPerMonth,
                "joinedAt" to member.joinedAt
            )

            try {
                val resp = memberRepository.createMember(memberMap)
                if (resp.isSuccessful) {
                    onSuccess()
                    loadMembers(member.planId)
                } else {
                    val errorBody = resp.errorBody()?.string()
                    _error.value = "Error ${resp.code()}: $errorBody"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
}
