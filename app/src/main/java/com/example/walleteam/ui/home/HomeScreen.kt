package com.example.walleteam.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.example.walleteam.data.model.Plan
import com.example.walleteam.viewmodel.plan.PlanListViewModel
import java.text.NumberFormat
import java.util.*
import kotlin.math.absoluteValue
import com.example.walleteam.ui.theme.*

@Composable
fun HomeScreen(
    planListViewModel: PlanListViewModel,
    onNavigateToPlanDetail: (String) -> Unit,
    onNavigateToCreatePlan: () -> Unit,
    onNavigateToJoinPlan: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val plans by planListViewModel.plans.collectAsState()
    val loading by planListViewModel.loading.collectAsState()

    LaunchedEffect(Unit) {
        planListViewModel.loadMyPlans()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(colors = listOf(PrimaryPurple, PrimaryBlue)))
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Mis Planes", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Ahorra en equipo", fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile", tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (loading) {
                    CircularProgressIndicator(color = PrimaryPurple)
                } else if (plans.isEmpty()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Savings, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(80.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No tienes planes aún", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Text("Crea tu primer plan de ahorro", fontSize = 14.sp, color = TextSecondary)
                    }
                } else {
                    val pagerState = rememberPagerState(pageCount = { plans.size })

                    HorizontalPager(
                        state = pagerState,
                        contentPadding = PaddingValues(horizontal = 65.dp),
                        pageSpacing = 20.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(420.dp)
                    ) { page ->
                        val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                        val absOffset = pageOffset.absoluteValue.coerceIn(0f, 1f)

                        PlanCard(
                            plan = plans[page],
                            onClick = { plans[page].id?.let(onNavigateToPlanDetail) },
                            modifier = Modifier.graphicsLayer {
                                val scale = lerp(0.85f, 1f, 1f - absOffset)
                                scaleX = scale
                                scaleY = scale
                                alpha = lerp(0.6f, 1f, 1f - absOffset)
                            }
                        )
                    }


                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(pagerState.pageCount) { iteration ->
                            val color = if (pagerState.currentPage == iteration) PrimaryPurple else Color.LightGray
                            val width = if (pagerState.currentPage == iteration) 24.dp else 8.dp
                            Box(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(color)
                                    .height(8.dp)
                                    .width(width)
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        onNavigateToCreatePlan()
                        planListViewModel.loadMyPlans()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Crear Plan")
                }

                OutlinedButton(
                    onClick = onNavigateToJoinPlan,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(2.dp, PrimaryPurple)
                ) {
                    Icon(Icons.Default.GroupAdd, contentDescription = null, tint = PrimaryPurple)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Unirse", color = PrimaryPurple)
                }
            }
        }
    }
}

@Composable
fun PlanCard(
    plan: Plan,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxHeight()
            .shadow(10.dp, RoundedCornerShape(32.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color.White, Color(0xFFF3F0FF))
                    )
                )
                .padding(28.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(PrimaryPurple)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = plan.name,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 32.sp
                    )
                    if (!plan.motive.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = plan.motive,
                            fontSize = 15.sp,
                            color = TextSecondary,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8F9FA), RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Meta", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                            Text(
                                formatCurrency(plan.targetAmount),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryPurple
                            )
                        }
                        Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color.LightGray))
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Tiempo", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                            Text(
                                "${plan.months} meses",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentOrange
                            )
                        }
                    }
                    Button(
                        onClick = onClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                    ) {
                        Text("Ver Detalles", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

fun formatCurrency(amount: Double): String {
    return try {
        val formatter = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
        formatter.maximumFractionDigits = 0
        formatter.format(amount)
    } catch (e: Exception) {
        "$ $amount"
    }
}