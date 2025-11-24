package com.example.walleteam.viewmodel.pay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walleteam.data.model.Payment
import com.example.walleteam.data.repository.PaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository
) : ViewModel() {

    private val _payments = MutableStateFlow<List<Payment>>(emptyList())
    val payments: StateFlow<List<Payment>> = _payments

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadPayments(planId: String) {

        viewModelScope.launch {
            _loading.value = true
            try {
                // Llamada directa al repo
                val resp = paymentRepository.getPaymentsByPlan(planId)
                if (resp.isSuccessful) _payments.value = resp.body() ?: emptyList()
                else _error.value = "Error ${resp.code()}"
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun registerPayment(payment: Payment, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val resp = paymentRepository.registerPayment(payment)
                if (resp.isSuccessful) onSuccess?.invoke()
                else _error.value = "Register payment failed ${resp.code()}"
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
}
