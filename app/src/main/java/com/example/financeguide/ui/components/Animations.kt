package com.example.financeguide.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.delay

/**
 * Reusable modifier to animate list items or cards entering the screen.
 * Uses spring physics for maximum fluidity and organic motion.
 * Delays are capped at index 3 to prevent scrolling lag in lists.
 */
fun Modifier.animateEnter(
    index: Int = 0,
    baseDelay: Int = 40,
    duration: Int = 400
): Modifier = composed {
    var animStarted by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        // Cap the cascade delay so scrolling down is always instant and smooth
        val delayTime = minOf(index, 3) * baseDelay
        delay(delayTime.toLong())
        animStarted = true
    }
    
    val alpha by animateFloatAsState(
        targetValue = if (animStarted) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "AlphaAnimation"
    )
    
    val slideY by animateFloatAsState(
        targetValue = if (animStarted) 0f else 24f, // 24dp translation is subtle and premium
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy, // Gentle bounce
            stiffness = Spring.StiffnessLow // Relaxed movement
        ),
        label = "SlideAnimation"
    )
    
    this.graphicsLayer(
        alpha = alpha,
        translationY = slideY
    )
}
