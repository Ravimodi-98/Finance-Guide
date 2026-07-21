package com.example.financeguide.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.financeguide.StrawHatIcon
import com.example.financeguide.theme.SpideyPrimary
import com.example.financeguide.theme.SpideyPrimaryLight
import com.example.financeguide.theme.SpideySecondary

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.financeguide.R

/**
 * Theme-aware launcher logo component.
 * Displays SpidermanLogoIcon for Spider-Man, FinanceProLogoIcon for Finance Pro, and StrawHatIcon for One Piece.
 */
@Composable
fun ThemeAppLogo(
    modifier: Modifier = Modifier,
    themeMode: String = "basic"
) {
    when (themeMode) {
        "spiderman" -> SpidermanLogoIcon(modifier = modifier)
        "basic"     -> FinanceProLogoIcon(modifier = modifier)
        else        -> StrawHatIcon(modifier = modifier)
    }
}

/**
 * Renders a high-tech modern Finance Pro Shield & Growth Trend Logo.
 */
@Composable
fun FinanceProLogoIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val center = Offset(w * 0.5f, h * 0.5f)
        val radius = size.minDimension / 2

        // Outer Dark Slate Circle with Mint Emerald Glow Rim
        drawCircle(
            color = Color(0xFF00E599),
            radius = radius
        )
        drawCircle(
            color = Color(0xFF080D1A),
            radius = radius * 0.92f
        )
        // Inner Glass Slate Disk
        drawCircle(
            color = Color(0xFF172238),
            radius = radius * 0.82f
        )

        // Geometric Shield Path
        val shieldPath = Path().apply {
            moveTo(w * 0.5f, h * 0.22f)
            lineTo(w * 0.78f, h * 0.32f)
            lineTo(w * 0.78f, h * 0.58f)
            cubicTo(
                w * 0.78f, h * 0.76f,
                w * 0.5f, h * 0.86f,
                w * 0.5f, h * 0.86f
            )
            cubicTo(
                w * 0.5f, h * 0.86f,
                w * 0.22f, h * 0.76f,
                w * 0.22f, h * 0.58f
            )
            lineTo(w * 0.22f, h * 0.32f)
            close()
        }
        drawPath(
            path = shieldPath,
            color = Color(0xFF003822)
        )
        drawPath(
            path = shieldPath,
            color = Color(0xFF00E599),
            style = Stroke(width = size.minDimension * 0.035f)
        )

        // Ascending Growth Chart Trendline inside Shield
        val chartPath = Path().apply {
            moveTo(w * 0.32f, h * 0.65f)
            lineTo(w * 0.44f, h * 0.55f)
            lineTo(w * 0.54f, h * 0.60f)
            lineTo(w * 0.68f, h * 0.38f)
        }
        drawPath(
            path = chartPath,
            color = Color(0xFF00D2FF),
            style = Stroke(
                width = size.minDimension * 0.055f,
                cap = StrokeCap.Round
            )
        )

        // Growth Trend Arrowhead at Top-Right
        val arrowPath = Path().apply {
            moveTo(w * 0.68f, h * 0.38f)
            lineTo(w * 0.58f, h * 0.38f)
            moveTo(w * 0.68f, h * 0.38f)
            lineTo(w * 0.68f, h * 0.48f)
        }
        drawPath(
            path = arrowPath,
            color = Color(0xFF00D2FF),
            style = Stroke(
                width = size.minDimension * 0.055f,
                cap = StrokeCap.Round
            )
        )

        // Central Radiant Gold Coin Node
        drawCircle(
            color = Color(0xFFFFC72C),
            radius = radius * 0.12f,
            center = Offset(w * 0.68f, h * 0.38f)
        )
    }
}

/**
 * Renders the exact Spider-Man Mask Logo image provided by the user on a sleek black background.
 */
@Composable
fun SpidermanLogoIcon(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.spiderman_logo),
            contentDescription = "Spider-Man Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}

/**
 * Spider-Man Character Avatar for AI Agent Chat.
 */
@Composable
fun SpiderManAvatar(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.spiderman_logo),
            contentDescription = "Spider-Man Avatar",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}



/**
 * Spider Web Watermark Background for Spider-Man Theme.
 */
@Composable
fun SpiderWebWatermark(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val center = Offset(w * 0.5f, h * 0.45f)
        val maxRadius = size.minDimension * 0.42f

        // Draw Spider Web Radial Spokes
        val spokes = 12
        for (i in 0 until spokes) {
            val angle = (360f / spokes) * i
            val rad = Math.toRadians(angle.toDouble())
            val endX = center.x + maxRadius * Math.cos(rad).toFloat()
            val endY = center.y + maxRadius * Math.sin(rad).toFloat()
            drawLine(
                color = SpideyPrimaryLight.copy(alpha = 0.08f),
                start = center,
                end = Offset(endX, endY),
                strokeWidth = 2f
            )
        }

        // Draw Web Concentric Circles
        val rings = 4
        for (r in 1..rings) {
            val currentR = maxRadius * (r / rings.toFloat())
            drawCircle(
                color = SpideyPrimaryLight.copy(alpha = 0.06f),
                radius = currentR,
                center = center,
                style = Stroke(
                    width = 2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
                )
            )
        }

        // Central Spider Emblem Watermark Silhouette
        drawCircle(
            color = SpideyPrimary.copy(alpha = 0.05f),
            radius = maxRadius * 0.18f,
            center = center
        )
    }
}
