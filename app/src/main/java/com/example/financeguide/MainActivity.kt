package com.example.financeguide

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financeguide.theme.FinanceGuideTheme
import com.example.financeguide.theme.GradientStart
import com.example.financeguide.theme.GradientMiddle
import com.example.financeguide.theme.GradientEnd
import com.example.financeguide.ui.navigation.FinanceNavGraph
import com.example.financeguide.ui.FinanceViewModel
import com.example.financeguide.ui.components.LocalCurrency
import com.example.financeguide.ui.screen.onboarding.OnboardingScreen
import com.example.financeguide.ui.screen.auth.LoginScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appContainer = (application as FinanceGuideApp).container
            val settingsManager = appContainer.settingsManager

            val onboardingCompleted by settingsManager.onboardingCompleted.collectAsState()
            val isLoggedIn by settingsManager.isLoggedIn.collectAsState()
            val themeMode by settingsManager.theme.collectAsState()
            val currencyCode by settingsManager.currency.collectAsState()

            val darkTheme = when (themeMode) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme()
            }

            var showSplash by remember { mutableStateOf(true) }

            // Pulse & Rotate animations for the Straw Hat
            val infiniteTransition = rememberInfiniteTransition(label = "SplashTransition")
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.85f,
                targetValue = 1.15f,
                animationSpec = InfiniteRepeatableSpec(
                    animation = tween(1000, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "ScaleAnimation"
            )
            val rotation by infiniteTransition.animateFloat(
                initialValue = -10f,
                targetValue = 10f,
                animationSpec = InfiniteRepeatableSpec(
                    animation = tween(1200, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "RotationAnimation"
            )

            // Floating coin offsets
            val floatAnim1 by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = -30f,
                animationSpec = InfiniteRepeatableSpec(
                    animation = tween(1500, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "Float1"
            )
            val floatAnim2 by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = -40f,
                animationSpec = InfiniteRepeatableSpec(
                    animation = tween(1800, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "Float2"
            )

            LaunchedEffect(Unit) {
                // Keep splash for 2200ms then hide
                kotlinx.coroutines.delay(2200)
                showSplash = false
            }

            FinanceGuideTheme(themeMode = themeMode) {
                CompositionLocalProvider(LocalCurrency provides currencyCode) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (showSplash) {
                            // Splash screen with premium animated background
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                Color(0xFF0C0E14),
                                                Color(0xFF161B26),
                                                Color(0xFF0C0E14)
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                // Floating coins background decorations
                                Box(modifier = Modifier.fillMaxSize()) {
                                    // Gold Coin 1
                                    GoldCoin(
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .padding(start = 60.dp, top = 150.dp)
                                            .offset(y = floatAnim1.dp)
                                            .scale(0.8f)
                                    )
                                    // Gold Coin 2
                                    GoldCoin(
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .padding(end = 80.dp, bottom = 200.dp)
                                            .offset(y = floatAnim2.dp)
                                            .scale(1.2f)
                                    )
                                    // Gold Coin 3
                                    GoldCoin(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(end = 60.dp, top = 220.dp)
                                            .offset(y = floatAnim1.dp * 0.7f)
                                            .scale(0.9f)
                                    )
                                }

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    // Pulsing Custom Straw Hat
                                    StrawHatIcon(
                                        modifier = Modifier
                                            .size(160.dp)
                                            .scale(scale)
                                            .rotate(rotation)
                                    )
                                    Spacer(modifier = Modifier.height(32.dp))
                                    Text(
                                        text = "FINANCE GUIDE",
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFB703), // Straw Hat Gold
                                        letterSpacing = 4.sp
                                    )
                                    Text(
                                        text = "The Pirate King's Ledger",
                                        fontSize = 14.sp,
                                        color = Color.White.copy(alpha = 0.6f),
                                        letterSpacing = 2.sp,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        } else if (!isLoggedIn) {
                            LoginScreen(
                                onLoginSuccess = { selectedName, selectedEmail, selectedAvatar, selectedToken ->
                                    settingsManager.saveUserLogin(selectedName, selectedEmail, selectedAvatar, selectedToken)
                                }
                            )
                        } else if (!onboardingCompleted) {
                            OnboardingScreen(
                                onComplete = { selectedCurrency, selectedTheme ->
                                    settingsManager.setCurrency(selectedCurrency)
                                    settingsManager.setTheme(selectedTheme)
                                    settingsManager.setOnboardingCompleted(true)
                                }
                            )
                        } else {
                            FinanceNavGraph(
                                viewModel = viewModel(factory = appContainer.financeViewModelFactory),
                                appContainer = appContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GoldCoin(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(36.dp)) {
        val radius = size.minDimension / 2
        // Outer shiny rim
        drawCircle(
            color = Color(0xFFFFB703),
            radius = radius
        )
        // Inner circle
        drawCircle(
            color = Color(0xFFFFD166),
            radius = radius * 0.75f
        )
        // Beli sign "B" or "฿"
        drawCircle(
            color = Color(0xFFC78B00),
            radius = radius * 0.4f
        )
    }
}

@Composable
fun StrawHatIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        
        // Draw Brim Shadow (semi-transparent dark red/black under brim)
        drawOval(
            color = Color.Black.copy(alpha = 0.3f),
            topLeft = Offset(w * 0.05f, h * 0.52f),
            size = Size(w * 0.9f, h * 0.35f)
        )

        // Draw Brim (Straw Hat Yellow/Gold Oval)
        drawOval(
            color = Color(0xFFFFB703),
            topLeft = Offset(0f, h * 0.48f),
            size = Size(w, h * 0.38f)
        )
        
        // Draw Crown (Straw Hat Yellow/Gold Dome)
        val path = Path().apply {
            moveTo(w * 0.22f, h * 0.55f)
            cubicTo(
                w * 0.22f, h * 0.08f,
                w * 0.78f, h * 0.08f,
                w * 0.78f, h * 0.55f
            )
            close()
        }
        drawPath(path, color = Color(0xFFFFB703))
        
        // Draw Red Ribbon at the base of the crown
        val ribbonPath = Path().apply {
            moveTo(w * 0.225f, h * 0.48f)
            lineTo(w * 0.775f, h * 0.48f)
            quadraticTo(w * 0.5f, h * 0.55f, w * 0.225f, h * 0.48f)
            close()
        }
        drawPath(ribbonPath, color = Color(0xFFD00000))
        
        // Ribbon extension below the hat
        val ribbonLeft = Path().apply {
            moveTo(w * 0.25f, h * 0.55f)
            lineTo(w * 0.75f, h * 0.55f)
            lineTo(w * 0.74f, h * 0.59f)
            lineTo(w * 0.26f, h * 0.59f)
            close()
        }
        drawPath(ribbonLeft, color = Color(0xFFD00000))
    }
}
