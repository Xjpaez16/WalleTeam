package com.example.walleteam.ui.plan



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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walleteam.data.model.Plan
import com.example.walleteam.viewmodel.plan.CreatePlanViewModel
import java.time.LocalDateTime
import com.example.walleteam.ui.theme.*

val GradientPurple = Brush.horizontalGradient(
    colors = listOf(Color(0xFF8B5CF6), Color(0xFF6366F1))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlanScreen(
    createPlanViewModel: CreatePlanViewModel,
    onPlanCreated: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var motive by remember { mutableStateOf("") }
    var targetAmount by remember { mutableStateOf("") }
    var months by remember { mutableStateOf("") }

    val loading by createPlanViewModel.loading.collectAsState()
    val error by createPlanViewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Plan", fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Icono decorativo
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(GradientPurple)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Savings,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Nombre del Plan
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre del Plan") },
                placeholder = { Text("Ej: Viaje a Europa") },
                leadingIcon = { Icon(Icons.Default.Label, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryPurple
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Motivo
            OutlinedTextField(
                value = motive,
                onValueChange = { motive = it },
                label = { Text("Motivo (Opcional)") },
                placeholder = { Text("¿Para qué estás ahorrando?") },
                leadingIcon = { Icon(Icons.Default.Description, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryPurple
                ),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Meta de Ahorro
            OutlinedTextField(
                value = targetAmount,
                onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) targetAmount = it },
                label = { Text("Meta de Ahorro") },
                placeholder = { Text("5000000") },
                leadingIcon = { Icon(Icons.Default.AttachMoney, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryPurple
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Meses
            OutlinedTextField(
                value = months,
                onValueChange = { if (it.all { char -> char.isDigit() }) months = it },
                label = { Text("Duración (meses)") },
                placeholder = { Text("12") },
                leadingIcon = { Icon(Icons.Default.CalendarMonth, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryPurple
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            if (error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = error!!, color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón Crear
            Button(
                onClick = {
                    val plan = Plan(
                        id = "",
                        name = name,
                        motive = motive.ifBlank { null },
                        targetAmount = targetAmount.toDoubleOrNull() ?: 0.0,
                        months = months.toIntOrNull() ?: 0,
                        createdAt = LocalDateTime.now().toString()
                    )
                    createPlanViewModel.createPlan(plan) {
                        onPlanCreated()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                enabled = !loading && name.isNotBlank() && targetAmount.isNotBlank() && months.isNotBlank()
            ) {
                if (loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Crear Plan", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}