package com.example.financeguide.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import java.text.NumberFormat
import java.util.Locale

import androidx.compose.runtime.staticCompositionLocalOf

val LocalCurrency = staticCompositionLocalOf { "BELI" }

@Composable
fun AnimatedCounter(
    count: Double,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = style.color,
    currencyCode: String = "INR"
) {
    var animatedCount by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(count) {
        animatedCount = count.toFloat()
    }

    val animatedValue by animateFloatAsState(
        targetValue = animatedCount,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "AnimatedCounter"
    )

    val currentCurrency = LocalCurrency.current

    Text(
        text = formatAmountString(animatedValue.toDouble(), currentCurrency),
        modifier = modifier,
        style = style,
        color = color
    )
}

fun formatAmountString(amount: Double, currencyCode: String = "BELI"): String {
    val format = NumberFormat.getNumberInstance(Locale.US).apply {
        maximumFractionDigits = 0
    }
    return when (currencyCode) {
        "BELI" -> "฿${format.format(amount)}"
        "INR" -> "₹${format.format(amount)}"
        "USD" -> "$${format.format(amount)}"
        "GBP" -> "£${format.format(amount)}"
        else -> "${format.format(amount)} $currencyCode"
    }
}
