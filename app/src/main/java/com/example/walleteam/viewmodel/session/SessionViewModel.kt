package com.example.walleteam.viewmodel.sessionimport

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walleteam.data.local.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val dataStore: DataStoreManager
) : ViewModel() {

    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName

    private val _userEmail = MutableStateFlow("")
    val userEmail: StateFlow<String> = _userEmail

    init {
        dataStore.tokenFlow.onEach { _token.value = it }.launchIn(viewModelScope)
        dataStore.userIdFlow.onEach { _userId.value = it }.launchIn(viewModelScope)
        dataStore.userNameFlow.onEach { _userName.value = it }.launchIn(viewModelScope)
        dataStore.userEmailFlow.onEach { _userEmail.value = it }.launchIn(viewModelScope)
    }

    fun saveSession(token: String, userId: String) {
        _token.value = token
        _userId.value = userId
        viewModelScope.launch {
            dataStore.saveToken(token)
            dataStore.saveUserId(userId)
        }
    }

    fun saveUserData(name: String, email: String) {
        _userName.value = name
        _userEmail.value = email
        viewModelScope.launch {
            dataStore.saveUserData(name, email)
        }
    }

    fun getUserId(): String? = _userId.value

    fun logout() {
        viewModelScope.launch {
            dataStore.clearToken()
            dataStore.clearUserData()
            _token.value = null
            _userId.value = null
            _userName.value = ""
            _userEmail.value = ""
        }
    }
}
