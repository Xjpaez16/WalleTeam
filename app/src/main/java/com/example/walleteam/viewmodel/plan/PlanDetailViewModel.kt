package com.example.walleteam.viewmodel.plan

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walleteam.data.local.DataStoreManager
import com.example.walleteam.data.model.Payment
import com.example.walleteam.data.model.Plan
import com.example.walleteam.data.repository.PaymentRepository
import com.example.walleteam.data.repository.PlanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanDetailViewModel @Inject constructor(
    private val planRepository: PlanRepository,
    private val paymentRepository: PaymentRepository,
    private val dataStoreManager: DataStoreManager // Usamos DataStore directo para el ID
) : ViewModel() {

    private val TAG = "PlanDetailVM"

    private val _plan = MutableStateFlow<Plan?>(null)
    val plan: StateFlow<Plan?> = _plan

    private val _payments = MutableStateFlow<List<Payment>>(emptyList())
    val payments: StateFlow<List<Payment>> = _payments

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadPlan(planId: String) {
        Log.d(TAG, ">>> loadPlan iniciado para ID: $planId")


        viewModelScope.launch {
            _loading.value = true
            try {
                val pResp = planRepository.getPlanDetails(planId)
                if (pResp.isSuccessful) {
                    _plan.value = pResp.body()
                } else {
                    val errorBody = pResp.errorBody()?.string() ?: ""
                    if (errorBody.contains("populate")) {
                        Log.w(TAG, " Usando PLAN MOCK por error de populate")
                    } else {
                        _error.value = "Plan error ${pResp.code()}"
                    }
                }

                val payResp = paymentRepository.getPaymentsByPlan(planId)
                if (payResp.isSuccessful) {
                    _payments.value = payResp.body() ?: emptyList()
                } else {
                    if (payResp.code() == 404) _payments.value = emptyList()
                    else _error.value = "Payments error ${payResp.code()}"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun addMoneyToGoal(planId: String, amount: Double) {
        Log.d(TAG, ">>> 4. addMoneyToGoal INICIADO. Monto: $amount, PlanID: $planId")

        viewModelScope.launch {
            _loading.value = true

            // Obtenemos el userId desde el DataStore
            val userId = dataStoreManager.userIdFlow.firstOrNull()

            if (userId.isNullOrEmpty()) {
                Log.e(TAG, "❌ Error: UserID es vacío/nulo. Reinicia login.")
                _error.value = "Error de sesión: Usuario no identificado."
                _loading.value = false
                return@launch
            }


            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US)
            sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
            val isoDate = sdf.format(java.util.Date())

            val payment = Payment(
                id = null,
                memberId = userId,
                planId = planId,
                amount = amount,
                date = isoDate
            )

            Log.d(TAG, "📦 5. Objeto Payment listo para enviar: $payment")

            try {
                Log.d(TAG, "📡 6. Enviando petición al servidor...")
                // Usamos el repo inyectado directamente
                val resp = paymentRepository.registerPayment(payment)

                Log.d(TAG, "📥 7. Respuesta recibida. Código: ${resp.code()}")

                if (resp.isSuccessful) {
                    Log.d(TAG, "✅ ¡PAGO REGISTRADO CON ÉXITO!")

                    loadPayments(planId)

                    val pResp = planRepository.getPlanDetails(planId)
                    if (pResp.isSuccessful) _plan.value = pResp.body()
                } else {
                    val errorBody = resp.errorBody()?.string()
                    Log.e(TAG, "❌ Error Backend: ${resp.code()} - $errorBody")
                    _error.value = "Error al registrar: $errorBody"
                }
            } catch (e: Exception) {
                Log.e(TAG, "🔥 Excepción en petición: ${e.message}")
                e.printStackTrace()
                _error.value = "Error de conexión: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadPayments(planId: String) {
        viewModelScope.launch {
            try {
                val response = paymentRepository.getPaymentsByPlan(planId)
                if (response.isSuccessful) {
                    _payments.value = response.body() ?: emptyList()
                } else if (response.code() == 404) {
                    _payments.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading payments: ${e.message}")
            }
        }
    }
}
