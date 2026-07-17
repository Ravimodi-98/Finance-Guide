package com.example.financeguide.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class ChartData(
    val value: Float,
    val color: Color
)

@Composable
fun DonutChart(
    data: List<ChartData>,
    totalAmount: Double,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    thickness: Dp = 32.dp
) {
    var animationPlayed by remember { mutableFloatStateOf(0f) }
    val animateProgress by animateFloatAsState(
        targetValue = animationPlayed,
        animationSpec = tween(
            durationMillis = 1500,
            easing = FastOutSlowInEasing
        ),
        label = "DonutChartAnimation"
    )

    LaunchedEffect(key1 = true) {
        animationPlayed = 1f
    }

    val total = data.sumOf { it.value.toDouble() }.toFloat()

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(thickness / 2)
        ) {
            var startAngle = -90f
            for (chartData in data) {
                val sweepAngle = (chartData.value / total) * 360f * animateProgress
                drawArc(
                    color = chartData.color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = thickness.toPx(), cap = StrokeCap.Butt),
                    size = Size(size.toPx() - thickness.toPx(), size.toPx() - thickness.toPx()),
                    topLeft = Offset(thickness.toPx() / 2, thickness.toPx() / 2)
                )
                startAngle += sweepAngle
            }
        }
        
        val currency = LocalCurrency.current
        Text(
            text = formatAmountString(totalAmount, currency),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
