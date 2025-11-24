package com.example.walleteam.viewmodel.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walleteam.data.local.DataStoreManager
import com.example.walleteam.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val TAG = "AuthViewModel"

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun login(email: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                Log.d(TAG, "Intentando login con: $email")

                val response = authRepository.login("", email, pass)

                if (response.isSuccessful) {
                    val authBody = response.body()
                    val token = authBody?.token ?: ""

                    // Búsqueda del ID
                    val userId = authBody?.userId
                        ?: authBody?._id
                        ?: authBody?.user?.id
                        ?: ""

                    Log.d(TAG, "Login OK. Token: ${token.take(10)}... ID Encontrado: '$userId'")

                    if (token.isNotEmpty() && userId.isNotEmpty()) {
                        // Guardar sesión
                        dataStoreManager.saveToken(token)
                        dataStoreManager.saveUserId(userId)

                        // Guardar datos extra
                        val name = authBody?.user?.name ?: ""
                        val emailUser = authBody?.user?.email ?: email
                        dataStoreManager.saveUserData(name, emailUser)

                        onSuccess()
                    } else {
                        Log.e(TAG, "Error: Body recibido -> $authBody")
                        _error.value = "Error: Datos de sesión incompletos (Falta ID o Token)"
                    }
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                    Log.e(TAG, "Login fallido: ${response.code()} - $errorMsg")
                    _error.value = "Credenciales incorrectas o error de servidor"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Excepción login: ${e.message}")
                _error.value = "Error de conexión: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun register(
        name: String,
        email: String,
        pass: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = authRepository.register(name, email, pass)
                if (response.isSuccessful) {
                    Log.d(TAG, "Registro exitoso para: $email")

                    val authBody = response.body()
                    val token = authBody?.token ?: ""
                    val userId = authBody?.userId
                        ?: authBody?._id
                        ?: authBody?.user?.id
                        ?: ""

                    Log.d(TAG, "Registro OK. Token: ${token.take(10)}... ID Encontrado: '$userId'")

                    if (token.isNotEmpty() && userId.isNotEmpty()) {
                        // Guardar sesión
                        dataStoreManager.saveToken(token)
                        dataStoreManager.saveUserId(userId)

                        // Guardar datos extra
                        val userName = authBody?.user?.name ?: name
                        val userEmail = authBody?.user?.email ?: email
                        dataStoreManager.saveUserData(userName, userEmail)

                        onResult(true, null)
                    } else {
                        Log.e(TAG, "Error: Body de registro incompleto -> $authBody")
                        val errorMsg = "Error: Datos de sesión incompletos tras registro."
                        _error.value = errorMsg
                        onResult(false, errorMsg)
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "Registro fallido: ${response.code()} - $errorBody")
                    _error.value = "Error al registrarse: $errorBody"
                    onResult(false, errorBody)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Excepción en registro: ${e.message}")
                _error.value = e.message
                onResult(false, e.message)
            } finally {
                _loading.value = false
            }
        }
    }
}
