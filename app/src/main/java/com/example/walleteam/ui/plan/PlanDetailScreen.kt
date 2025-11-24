package com.example.walleteam.ui.plan
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walleteam.ui.theme.PrimaryBlue
import com.example.walleteam.ui.theme.PrimaryPurple
import com.example.walleteam.ui.theme.TextSecondary
import com.example.walleteam.viewmodel.plan.PlanDetailViewModel
import com.example.walleteam.viewmodel.member.MemberViewModel
import com.example.walleteam.ui.payment.RegisterPaymentContent
import java.text.NumberFormat
import java.util.Locale
import com.example.walleteam.ui.plan.PlanMembersScreen



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanDetailScreen(
    planId: String,
    planDetailViewModel: PlanDetailViewModel,
    memberViewModel: MemberViewModel,
    onNavigateToPaymentList: (String) -> Unit,
    onNavigateToMembers: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val plan by planDetailViewModel.plan.collectAsState()
    val payments by planDetailViewModel.payments.collectAsState()
    val loading by planDetailViewModel.loading.collectAsState()
    val error by planDetailViewModel.error.collectAsState()

    val members by memberViewModel.members.collectAsState()

    var showPaymentSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(planId) {
        planDetailViewModel.loadPlan(planId)
        planDetailViewModel.loadPayments(planId)
        memberViewModel.loadMembers(planId)
    }

    val totalSaved = payments.sumOf { it.amount }
    val targetAmount = plan?.targetAmount ?: 1.0
    val progressFraction = if (targetAmount > 0) (totalSaved / targetAmount).toFloat().coerceIn(0f, 1f) else 0f
    val progressPercentage = (progressFraction * 100).toInt()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Ahorro", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            if (loading && plan == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else if (plan != null) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF5F5F5))
                        .verticalScroll(rememberScrollState())
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(plan!!.name, fontSize = 22.sp, fontWeight = FontWeight.Bold)

                            Spacer(modifier = Modifier.height(8.dp))

                            val clipboardManager = LocalClipboardManager.current
                            val context = LocalContext.current

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        clipboardManager.setText(AnnotatedString(planId))
                                        Toast.makeText(context, "ID del plan copiado", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.ContentCopy,
                                    contentDescription = "Copiar ID",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "ID: $planId",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(200.dp)) {
                                CircularProgressIndicator(
                                    progress = { 1f },
                                    modifier = Modifier.fillMaxSize(),
                                    color = Color(0xFFE0E0E0),
                                    strokeWidth = 16.dp
                                )
                                CircularProgressIndicator(
                                    progress = { progressFraction },
                                    modifier = Modifier.fillMaxSize(),
                                    color = MaterialTheme.colorScheme.primary,
                                    strokeWidth = 16.dp
                                )
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("$progressPercentage%", fontSize = 48.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Completado", fontSize = 16.sp, color = TextSecondary)
                                }
                            }
                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                "Aportado: ${formatMoney(totalSaved)} de ${formatMoney(plan!!.targetAmount)}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50)
                            )
                        }
                    }

                    val gradientColors = listOf(PrimaryPurple, PrimaryBlue)
                    val brush = remember { Brush.horizontalGradient(gradientColors) }

                    Button(
                        onClick = { showPaymentSheet = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp)
                    ) {

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(brush = brush, shape = RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Savings, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Ingresar Dinero", color = Color.White, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { onNavigateToPaymentList(planId) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = brush),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = PrimaryPurple
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.History, contentDescription = null, tint = PrimaryPurple)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Ver Historial de Pagos", fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        "Equipo",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color(0xFF1E293B)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    PlanMembersScreen(
                        members = members,
                        loading = loading
                    )

                    Spacer(modifier = Modifier.height(50.dp))
                }
            }
        }

        if (showPaymentSheet) {
            ModalBottomSheet(
                onDismissRequest = { showPaymentSheet = false },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                RegisterPaymentContent(
                    loading = loading,
                    error = error,
                    onRegisterClick = { amount ->
                        Log.d("PlanDetailScreen", "Monto: $amount")
                        planDetailViewModel.addMoneyToGoal(planId, amount)
                        showPaymentSheet = false
                    }
                )
            }
        }
    }
}

fun formatMoney(amount: Double): String {
    return try {
        NumberFormat.getCurrencyInstance(Locale("es", "CO")).format(amount)
    } catch (e: Exception) {
        "$ $amount"
    }
}