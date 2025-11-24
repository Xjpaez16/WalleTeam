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
class CreatePlanViewModel @Inject constructor(
    private val planRepository: PlanRepository
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun createPlan(plan: Plan, onSuccess: () -> Unit = {}) {
        // Ya no necesitamos obtener el token manualmente ni usar factory.
        // El interceptor de Hilt se encarga de poner el token en la cabecera.

        // Creamos un mapa solo con los campos que Mongo necesita
        val planToSend = mutableMapOf<String, Any>(
            "name" to plan.name,
            "targetAmount" to plan.targetAmount,
            "months" to plan.months
        )

        // Solo agregamos motive si no es null
        plan.motive?.let { planToSend["motive"] = it }

        Log.i("CREATE_PLAN", "🔵 [CREATE PLAN] Mapa a enviar = $planToSend")

        viewModelScope.launch {
            _loading.value = true
            try {
                // Usamos el repositorio inyectado directamente
                val resp = planRepository.createPlan(planToSend)

                Log.i("CREATE_PLAN", "🟣 [CREATE PLAN RESPONSE] Code=${resp.code()} Success=${resp.isSuccessful} Body=${resp.body()} Error=${resp.errorBody()}")

                if (resp.isSuccessful) {
                    Log.i("CREATE_PLAN", "✅ [CREATE PLAN] Plan creado correctamente")
                    onSuccess()
                } else {
                    _error.value = "Error ${resp.code()}"
                    Log.e("CREATE_PLAN", "❌ [CREATE PLAN] Error al crear plan: code=${resp.code()}")
                }
            } catch (e: Exception) {
                _error.value = e.message
                Log.e("CREATE_PLAN", "❌ [CREATE PLAN] Exception: ${e.message}", e)
            } finally {
                _loading.value = false
                Log.i("CREATE_PLAN", "⚪ [CREATE PLAN] Finalizado (loading=false)")
            }
        }
    }
}
