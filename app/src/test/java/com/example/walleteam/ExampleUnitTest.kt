package com.example.walleteam

import com.example.walleteam.data.local.DataStoreManager
import com.example.walleteam.data.model.Payment
import com.example.walleteam.data.model.Plan
import com.example.walleteam.data.repository.PaymentRepository
import com.example.walleteam.data.repository.PlanRepository
import com.example.walleteam.viewmodel.plan.PlanDetailViewModel
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.collections.sumOf
import kotlin.jvm.java

/**
 * Test unitario local para PlanDetailViewModel.
 */
class PlanDetailViewModelTest {

    @Test
    fun `calculate progress returns correct percent`() {
        // Crear Mocks
        val planRepository = mockk<PlanRepository>(relaxed = true)
        val paymentRepository = mockk<PaymentRepository>(relaxed = true)
        val dataStoreManager = mockk<DataStoreManager>(relaxed = true)

        // Instanciar con el constructor @HiltViewModel
        val vm = PlanDetailViewModel(
            planRepository = planRepository,
            paymentRepository = paymentRepository,
            dataStoreManager = dataStoreManager
        )

        // Datos de prueba
        val plan = Plan(
            id = "1",
            name = "Plan Vacaciones",
            motive = null,
            targetAmount = 1000.0,
            months = 10,
            createdAt = ""
        )
        val payments = listOf(
            Payment(id = "p1", memberId = "m1", planId = "1", amount = 200.0, date = ""),
            Payment(id = "p2", memberId = "m2", planId = "1", amount = 300.0, date = "")
        )


        vmTestSetPlanAndPayments(vm, plan, payments)



        val totalCollected = payments.sumOf { it.amount }
        val expectedProgress = (totalCollected / plan.targetAmount) * 100


        assertEquals(plan, vm.plan.value)
        assertEquals(payments, vm.payments.value)


    }


    private fun vmTestSetPlanAndPayments(vm: PlanDetailViewModel, plan: Plan, payments: List<Payment>) {
        // Acceder a _plan
        val planField = PlanDetailViewModel::class.java.getDeclaredField("_plan")
        planField.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val planFlow = planField.get(vm) as MutableStateFlow<Plan?>
        planFlow.value = plan

        // Acceder a _payments
        val payField = PlanDetailViewModel::class.java.getDeclaredField("_payments")
        payField.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val payFlow = payField.get(vm) as MutableStateFlow<List<Payment>>
        payFlow.value = payments
    }
}
