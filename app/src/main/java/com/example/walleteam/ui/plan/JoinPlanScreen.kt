package com.example.walleteam.ui.plan


import TextPrimary
import TextSecondary
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
import com.example.walleteam.data.model.Member
import com.example.walleteam.ui.theme.*
import com.example.walleteam.viewmodel.member.MemberViewModel
import com.example.walleteam.viewmodel.plan.PlanDetailViewModel
import java.text.NumberFormat
import java.time.LocalDateTime
import java.util.*




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinPlanScreen(
    memberViewModel: MemberViewModel,
    planDetailViewModel: PlanDetailViewModel,
    onJoinSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var planId by remember { mutableStateOf("") }
    var memberName by remember { mutableStateOf("") }
    var contribution by remember { mutableStateOf("") }
    var showPlanDetails by remember { mutableStateOf(false) }

    val plan by planDetailViewModel.plan.collectAsState()
    val loading by memberViewModel.loading.collectAsState()
    val error by memberViewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Unirse a Plan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryBlue,
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Brush.horizontalGradient(listOf(PrimaryBlue, PrimaryPurple))),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.GroupAdd,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Únete a un plan existente",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = "Ingresa el ID del plan y tu información",
                fontSize = 14.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = planId,
                onValueChange = {
                    planId = it
                    showPlanDetails = false
                },
                label = { Text("ID del Plan") },
                placeholder = { Text("Ej: 60d5ec49f1b2c72e8c8b4567") },
                leadingIcon = { Icon(Icons.Default.Tag, null) },
                trailingIcon = {
                    if (planId.isNotBlank()) {
                        IconButton(onClick = {
                            planDetailViewModel.loadPlan(planId)
                            showPlanDetails = true
                        }) {
                            Icon(Icons.Default.Search, contentDescription = "Buscar", tint = PrimaryBlue)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue
                ),
                singleLine = true
            )

            if (showPlanDetails && plan != null) {
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = PrimaryPurple.copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Plan encontrado",
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF10B981)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            plan!!.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        if (!plan!!.motive.isNullOrBlank()) {
                            Text(
                                plan!!.motive!!,
                                fontSize = 14.sp,
                                color = TextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Meta", fontSize = 12.sp, color = TextSecondary)
                                Text(
                                    formatCurrency(plan!!.targetAmount),
                                    fontWeight = FontWeight.SemiBold,
                                    color = PrimaryPurple
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Duración", fontSize = 12.sp, color = TextSecondary)
                                Text(
                                    "${plan!!.months} meses",
                                    fontWeight = FontWeight.SemiBold,
                                    color = AccentOrange
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = memberName,
                onValueChange = { memberName = it },
                label = { Text("Tu Nombre") },
                placeholder = { Text("Ej: Jorge") },
                leadingIcon = { Icon(Icons.Default.Person, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = contribution,
                onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) contribution = it },
                label = { Text("Contribución Mensual") },
                placeholder = { Text("Ej: 500000") },
                leadingIcon = { Icon(Icons.Default.AttachMoney, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            if (error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = error!!, color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val member = Member(
                        id = "",
                        name = memberName,
                        planId = planId,
                        contributionPerMonth = contribution.toDoubleOrNull() ?: 0.0,
                        joinedAt = LocalDateTime.now().toString()
                    )
                    memberViewModel.addMember(member) {
                        onJoinSuccess()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                enabled = !loading && planId.isNotBlank() && memberName.isNotBlank() && contribution.isNotBlank()
            ) {
                if (loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.GroupAdd, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Unirse al Plan", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF1F5F9)
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "El ID del plan te lo debe proporcionar el creador del plan",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
    return formatter.format(amount)
}