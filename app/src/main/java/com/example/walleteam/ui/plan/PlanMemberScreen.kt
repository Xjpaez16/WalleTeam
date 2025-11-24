package com.example.walleteam.ui.plan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walleteam.data.model.Member
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

val PrimaryPurple = Color(0xFF6366F1)
val PrimaryBlue = Color(0xFF3B82F6)
val AccentOrange = Color(0xFFFF8C42)
val AccentPink = Color(0xFFEC4899)
val BackgroundLight = Color(0xFFF8FAFC)
val TextPrimary = Color(0xFF1E293B)
val TextSecondary = Color(0xFF64748B)

val avatarColors = listOf(
    PrimaryPurple, PrimaryBlue, AccentOrange, AccentPink,
    Color(0xFF10B981), Color(0xFFF59E0B), Color(0xFF8B5CF6), Color(0xFF06B6D4)
)


@Composable
fun PlanMembersScreen(
    members: List<Member>,
    loading: Boolean
) {
    val totalContribution = members.sumOf { it.contributionPerMonth }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundLight)
    ) {
        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else if (members.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.GroupOff,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(60.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "No hay miembros aún",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Total Miembros", fontSize = 14.sp, color = TextSecondary)
                            Text("${members.size}", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        }
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(PrimaryBlue.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Group, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(28.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color(0xFFE2E8F0))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Aporte Mensual Total", fontSize = 12.sp, color = TextSecondary)
                    Text(
                        // Usamos un helper local o el string formateado directo
                        formatCurrencyLocal(totalContribution),
                        fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                members.forEachIndexed { index, member ->
                    MemberCard(member = member, index = index)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun MemberCard(member: Member, index: Int) {
    val avatarColor = avatarColors[index % avatarColors.size]
    val initial = member.name.firstOrNull()?.uppercaseChar() ?: '?'

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(avatarColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial.toString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = avatarColor
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = member.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatDate(member.joinedAt),
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(formatCurrencyLocal(member.contributionPerMonth), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                Text("/mes", fontSize = 10.sp, color = TextSecondary)
            }
        }
    }
}

fun formatCurrencyLocal(amount: Double): String {
    return try {
        java.text.NumberFormat.getCurrencyInstance(Locale("es", "CO")).format(amount)
    } catch (e: Exception) {
        "$ $amount"
    }
}

fun formatDate(dateString: String): String {
    return try {
        val dateTime = LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME)
        val formatter = DateTimeFormatter.ofPattern("dd MMM", Locale("es", "ES"))
        dateTime.format(formatter)
    } catch (e: Exception) {
        "Recientemente"
    }
}
