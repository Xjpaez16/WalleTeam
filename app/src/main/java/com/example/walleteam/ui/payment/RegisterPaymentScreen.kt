package com.example.walleteam.ui.payment

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walleteam.data.model.Payment
import com.example.walleteam.viewmodel.member.MemberViewModel
import com.example.walleteam.viewmodel.pay.PaymentViewModel
import java.time.LocalDateTime

val PrimaryPurple = Color(0xFF6366F1)
val AccentOrange = Color(0xFFFF8C42)
val BackgroundLight = Color(0xFFF8FAFC)
val TextPrimary = Color(0xFF1E293B)
val TextSecondary = Color(0xFF64748B)
val GradientPurple = Brush.horizontalGradient(
    colors = listOf(Color(0xFF8B5CF6), Color(0xFF6366F1))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterPaymentScreen(
    planId: String,
    paymentViewModel: PaymentViewModel,
    memberViewModel: MemberViewModel,
    onPaymentRegistered: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val loading by paymentViewModel.loading.collectAsState()
    val error by paymentViewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Pago", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryPurple,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        RegisterPaymentContent(
            modifier = Modifier.padding(padding), // Corrección de 'padding'
            loading = loading,
            error = error,
            onRegisterClick = { amount ->

                val payment = Payment(
                    id = null,
                    memberId = "",
                    planId = planId,
                    amount = amount,
                    date = LocalDateTime.now().toString()
                )
                paymentViewModel.registerPayment(payment) {
                    onPaymentRegistered()
                }
            }
        )
    }
}

@Composable
fun RegisterPaymentContent(
    modifier: Modifier = Modifier,
    loading: Boolean,
    error: String?,
    onRegisterClick: (Double) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    val TAG = "RegisterPaymentUI"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(BackgroundLight)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(GradientPurple)
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Payment,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Nuevo Aporte Personal",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Text(
            "Ingresa el monto que deseas ahorrar hoy",
            fontSize = 14.sp,
            color = TextSecondary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Input de Monto
        Text("Monto del Aporte", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) amount = it },
            label = { Text("Monto ($)") },
            leadingIcon = { Icon(Icons.Default.AttachMoney, null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryPurple),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        if (error != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = error, color = Color.Red, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val cleanAmount = amount.replace(",", ".").trim()
                Log.d(TAG, "1. Click en Confirmar. Input original: '$amount', Limpio: '$cleanAmount'")

                val amountDouble = cleanAmount.toDoubleOrNull()

                if (amountDouble != null && amountDouble > 0) {
                    Log.d(TAG, "2. Monto válido ($amountDouble). Enviando al callback...")
                    onRegisterClick(amountDouble)
                } else {
                    Log.e(TAG, "❌ Error: Monto inválido o cero.")
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
            enabled = !loading && amount.isNotBlank()
        ) {
            if (loading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Icon(Icons.Default.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Confirmar Aporte", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
