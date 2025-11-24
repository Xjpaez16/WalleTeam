package com.example.walleteam

import java.text.NumberFormat
import java.util.Locale

fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
    formatter.maximumFractionDigits = 0
    return formatter.format(amount)
}