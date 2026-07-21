package com.example.financeguide.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeguide.theme.LocalThemeStrings

data class ChartData(
    val value: Float,
    val color: Color,
    val brush: Brush? = null
)

@Composable
fun DonutChart(
    data: List<ChartData>,
    totalAmount: Double,
    modifier: Modifier = Modifier,
    size: Dp = 230.dp,
    thickness: Dp = 26.dp,
    labelText: String = "TOTAL EXPENSE",
    labelColor: Color = Color(0xFFF6C445)
) {
    var animationPlayed by remember { mutableFloatStateOf(0f) }
    val animateProgress by animateFloatAsState(
        targetValue = animationPlayed,
        animationSpec = tween(
            durationMillis = 1400,
            easing = FastOutSlowInEasing
        ),
        label = "DonutChartAnimation"
    )

    LaunchedEffect(key1 = true) {
        animationPlayed = 1f
    }

    val strings = LocalThemeStrings.current
    val total = data.sumOf { it.value.toDouble() }.toFloat()

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokePx = thickness.toPx()
            val arcDimension = size.toPx() - strokePx
            val topLeftOffset = Offset(strokePx / 2f, strokePx / 2f)

            // Outer Ambient Glow Ring Track
            drawArc(
                color = labelColor.copy(alpha = 0.08f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokePx + 14.dp.toPx(), cap = StrokeCap.Round),
                size = Size(arcDimension, arcDimension),
                topLeft = topLeftOffset
            )

            // Background Track Ring
            drawArc(
                color = Color.White.copy(alpha = 0.08f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round),
                size = Size(arcDimension, arcDimension),
                topLeft = topLeftOffset
            )

            if (total > 0f) {
                var startAngle = -90f
                val gapAngle = if (data.size > 1) 4f else 0f
                for (chartData in data) {
                    val sweepAngle = ((chartData.value / total) * 360f - gapAngle) * animateProgress
                    if (sweepAngle > 0f) {
                        // Soft Ambient Glow underneath the primary segment
                        drawArc(
                            color = chartData.color.copy(alpha = 0.35f),
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            style = Stroke(width = strokePx + 8.dp.toPx(), cap = StrokeCap.Round),
                            size = Size(arcDimension, arcDimension),
                            topLeft = topLeftOffset
                        )

                        // Primary High-Quality Arc Segment
                        if (chartData.brush != null) {
                            drawArc(
                                brush = chartData.brush,
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                style = Stroke(width = strokePx, cap = StrokeCap.Round),
                                size = Size(arcDimension, arcDimension),
                                topLeft = topLeftOffset
                            )
                        } else {
                            drawArc(
                                color = chartData.color,
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                style = Stroke(width = strokePx, cap = StrokeCap.Round),
                                size = Size(arcDimension, arcDimension),
                                topLeft = topLeftOffset
                            )
                        }

                        startAngle += sweepAngle + gapAngle
                    }
                }
            }
        }

        val currency = LocalCurrency.current
        val innerCircleDimension = size - (thickness * 2.2f)

        // Center Ambient Radial Aura Box
        Box(
            modifier = Modifier
                .size(innerCircleDimension)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            labelColor.copy(alpha = 0.18f),
                            Color.Transparent
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = labelText.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.4.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatAmountString(totalAmount, currency),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 26.sp
                    ),
                    color = labelColor,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
