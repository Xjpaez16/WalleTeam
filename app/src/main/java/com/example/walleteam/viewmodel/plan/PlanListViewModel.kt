package com.example.walleteam.viewmodel.plan

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walleteam.data.model.Plan
import com.example.walleteam.data.repository.PlanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanListViewModel @Inject constructor(
    private val planRepository: PlanRepository
) : ViewModel() {

    private val _plans = MutableStateFlow<List<Plan>>(emptyList())
    val plans: StateFlow<List<Plan>> = _plans

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadMyPlans() {


        viewModelScope.launch {
            _loading.value = true
            Log.d("PlanListVM", "Iniciando petición al servidor...")

            try {

                val resp = planRepository.getMyPlans()

                if (resp.isSuccessful) {
                    val listaPlanes = resp.body() ?: emptyList()
                    _plans.value = listaPlanes
                    Log.d("PlanListVM", "¡Éxito! Se recibieron ${listaPlanes.size} planes")

                    if (listaPlanes.isEmpty()) {
                        Log.w("PlanListVM", "La lista llegó vacía del servidor ( [] )")
                    }
                } else {
                    val errorMsg = "Error ${resp.code()}: ${resp.errorBody()?.string()}"
                    _error.value = errorMsg
                    Log.e("PlanListVM", "Fallo en respuesta: $errorMsg")
                }
            } catch (e: Exception) {
                _error.value = e.message
                Log.e("PlanListVM", "Excepción fatal: ${e.message}", e)
            } finally {
                _loading.value = false
            }
        }
    }
}
