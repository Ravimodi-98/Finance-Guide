package com.example.financeguide.ui.screen.ai

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.financeguide.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceAIScreen(
    viewModel: FinanceAIViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var inputText by remember { mutableStateOf("") }
    
    val listState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val isKeyboardOpen = WindowInsets.ime.getBottom(LocalDensity.current) > 0

    // Auto-scroll to the bottom when new messages arrive or keyboard is opened (WhatsApp style)
    LaunchedEffect(uiState.messages.size, uiState.isLoading, isKeyboardOpen) {
        if (uiState.messages.isNotEmpty()) {
            if (isKeyboardOpen) {
                kotlinx.coroutines.delay(150)
            }
            listState.animateScrollToItem(uiState.messages.size)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        NavigatorAvatar(modifier = Modifier.size(36.dp))
                        Column {
                            Text(
                                text = "Finance Navigator",
                                color = Secondary,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = if (uiState.isLoading) "Mapping stars..." else "Online",
                                color = if (uiState.isLoading) SecondaryLight else Color.Green.copy(alpha = 0.8f),
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    if (uiState.messages.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearChat() }) {
                            Icon(
                                imageVector = Icons.Rounded.DeleteSweep,
                                contentDescription = "Clear Chat",
                                tint = OnSurfaceDarkVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceDark,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = SurfaceDark
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            SurfaceDark,
                            SurfaceDarkVariant,
                            SurfaceDark
                        )
                    )
                )
        ) {
            // Error banner
            AnimatedVisibility(
                visible = uiState.error != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                uiState.error?.let { err ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Warning,
                                contentDescription = "Warning",
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = err,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { viewModel.dismissError() }) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = "Close",
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
            }



            // Message List
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                BackgroundWatermark(modifier = Modifier.fillMaxSize())
                if (uiState.messages.isEmpty()) {
                    // Empty state showing Straw Hat guidelines
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        NavigatorAvatar(modifier = Modifier.size(if (isKeyboardOpen) 60.dp else 120.dp))
                        Spacer(modifier = Modifier.height(if (isKeyboardOpen) 8.dp else 24.dp))
                        Text(
                            text = "Ahoy, Sailor!",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = if (isKeyboardOpen) 20.sp else 28.sp
                            ),
                            color = Secondary,
                            textAlign = TextAlign.Center
                        )
                        if (!isKeyboardOpen) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "I am Nami, your Finance Navigator. Let's make sure our ship's ledger is in order! Ask me questions about saving money, budget limits, emergency funds, or expense logs.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = OnSurfaceDarkVariant,
                                textAlign = TextAlign.Center,
                                lineHeight = 22.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "⚠️ \"Every Pirate King starts by protecting their treasure!\"",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = SecondaryLight,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(uiState.messages) { message ->
                            ChatBubble(
                                message = message,
                                onCopy = {
                                    clipboardManager.setText(AnnotatedString(message.text))
                                    Toast.makeText(context, "Logbook copied to clipboard!", Toast.LENGTH_SHORT).show()
                                },
                                onShare = {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, message.text)
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Share Navigator's Log"))
                                },
                                onRegenerate = {
                                    // Find last user message
                                    val lastUserMessage = uiState.messages.lastOrNull { it.isFromUser }?.text
                                    if (lastUserMessage != null) {
                                        viewModel.sendMessage(lastUserMessage)
                                    }
                                },
                                isLast = message == uiState.messages.last()
                            )
                        }

                        if (uiState.isLoading) {
                            item {
                                TypingIndicatorRow()
                            }
                        }
                    }
                }
            }

            // Suggestions List
            if (uiState.messages.isEmpty() || uiState.messages.size < 4) {
                HorizontalSuggestedQuestions(
                    suggestions = uiState.suggestedQuestions,
                    onSelect = { question ->
                        inputText = question
                        viewModel.sendMessage(question)
                        keyboardController?.hide()
                    }
                )
            }

            // Input Row
            Surface(
                color = SurfaceDarkVariant,
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .navigationBarsPadding()
                        .imePadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text(
                                "Ask the Navigator...",
                                color = OnSurfaceDarkVariant.copy(alpha = 0.7f)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Secondary,
                            unfocusedBorderColor = OutlineDark,
                            focusedContainerColor = SurfaceDark,
                            unfocusedContainerColor = SurfaceDark
                        ),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 4,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Send
                        ),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (inputText.isNotBlank() && !uiState.isLoading) {
                                    viewModel.sendMessage(inputText)
                                    inputText = ""
                                    keyboardController?.hide()
                                }
                            }
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    val sendBtnScale = if (inputText.isNotBlank()) 1.1f else 1.0f
                    val infiniteTransition = rememberInfiniteTransition(label = "SendAnim")
                    val pulseScale by if (inputText.isNotBlank()) {
                        infiniteTransition.animateFloat(
                            initialValue = 1.0f,
                            targetValue = 1.1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1000, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "Pulse"
                        )
                    } else {
                        remember { mutableStateOf(1.0f) }
                    }

                    FloatingActionButton(
                        onClick = {
                            if (inputText.isNotBlank() && !uiState.isLoading) {
                                viewModel.sendMessage(inputText)
                                inputText = ""
                                keyboardController?.hide()
                            }
                        },
                        containerColor = if (inputText.isNotBlank()) Primary else SurfaceDarkElevated,
                        contentColor = Color.White,
                        shape = CircleShape,
                        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .size(48.dp)
                            .clickable(enabled = inputText.isNotBlank() && !uiState.isLoading) {}
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.Send,
                            contentDescription = "Send",
                            tint = if (inputText.isNotBlank()) Color.White else OnSurfaceDarkVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: ChatMessage,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onRegenerate: () -> Unit,
    isLast: Boolean
) {
    val bubbleShape = if (message.isFromUser) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 2.dp)
    } else {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 2.dp, bottomEnd = 16.dp)
    }

    val alignment = if (message.isFromUser) Alignment.CenterEnd else Alignment.CenterStart
    val containerColor = if (message.isFromUser) PrimaryContainer else SurfaceDarkElevated
    val borderColor = if (message.isFromUser) Primary.copy(alpha = 0.5f) else Tertiary.copy(alpha = 0.4f)

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.85f),
            horizontalAlignment = if (message.isFromUser) Alignment.End else Alignment.Start
        ) {
            Surface(
                shape = bubbleShape,
                color = containerColor,
                border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    if (message.isFromUser) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(bottom = 6.dp)
                        ) {
                            Text(
                                text = "LOGBOOK ENTRY",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                ),
                                color = PrimaryLight.copy(alpha = 0.8f),
                                letterSpacing = 1.sp
                            )
                            Icon(
                                imageVector = Icons.Rounded.Anchor,
                                contentDescription = null,
                                tint = PrimaryLight,
                                modifier = Modifier.size(11.dp)
                            )
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(bottom = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Explore,
                                contentDescription = null,
                                tint = Secondary,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = "NAVIGATOR LOG",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                ),
                                color = SecondaryLight.copy(alpha = 0.8f),
                                letterSpacing = 1.sp
                            )
                        }
                    }
                    Text(
                        text = parseMarkdown(message.text),
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            lineHeight = 20.sp,
                            letterSpacing = 0.3.sp
                        )
                    )

                    // Actions bar inside bubble for AI messages, or below
                    if (!message.isFromUser) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(
                                onClick = onCopy,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = OnSurfaceDarkVariant.copy(alpha = 0.7f),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            IconButton(
                                onClick = onShare,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Share,
                                    contentDescription = "Share",
                                    tint = OnSurfaceDarkVariant.copy(alpha = 0.7f),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            if (isLast) {
                                Spacer(modifier = Modifier.width(4.dp))
                                IconButton(
                                    onClick = onRegenerate,
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Refresh,
                                        contentDescription = "Regenerate",
                                        tint = OnSurfaceDarkVariant.copy(alpha = 0.7f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HorizontalSuggestedQuestions(
    suggestions: List<String>,
    onSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Text(
            text = "Suggested Questions",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = SecondaryLight,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(suggestions) { question ->
                SuggestionChip(
                    label = question,
                    onClick = { onSelect(question) }
                )
            }
        }
    }
}

@Composable
fun SuggestionChip(
    label: String,
    onClick: () -> Unit
) {
    val emoji = when {
        label.contains("spent") || label.contains("Beli on") -> "🍖"
        label.contains("earned") || label.contains("treasure") -> "🪙"
        label.contains("budget") || label.contains("limit") -> "📊"
        label.contains("reduce") || label.contains("save") -> "📉"
        label.contains("emergency") -> "🛡️"
        label.contains("invest") -> "📈"
        else -> "🧭"
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = SurfaceDarkElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineDark),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = emoji, fontSize = 14.sp)
            Text(
                text = label,
                color = OnSurfaceDark,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}

@Composable
fun TypingIndicatorRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavigatorAvatar(modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 2.dp, bottomEnd = 16.dp),
            color = SurfaceDarkElevated,
            border = androidx.compose.foundation.BorderStroke(1.dp, Tertiary.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val infiniteTransition = rememberInfiniteTransition(label = "DotsAnim")
                
                val dot1 by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = keyframes {
                            durationMillis = 1000
                            0.0f at 0
                            0.5f at 200
                            1.0f at 400
                            0.0f at 800
                        },
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "Dot1"
                )
                val dot2 by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = keyframes {
                            durationMillis = 1000
                            0.0f at 200
                            0.5f at 400
                            1.0f at 600
                            0.0f at 1000
                        },
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "Dot2"
                )
                val dot3 by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = keyframes {
                            durationMillis = 1000
                            0.0f at 400
                            0.5f at 600
                            1.0f at 800
                            0.0f at 1200
                        },
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "Dot3"
                )

                Dot(opacity = dot1)
                Dot(opacity = dot2)
                Dot(opacity = dot3)
            }
        }
    }
}

@Composable
fun Dot(opacity: Float) {
    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(Secondary.copy(alpha = maxOf(0.2f, opacity)))
    )
}

@Composable
fun NavigatorAvatar(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val radius = size.minDimension / 2
        
        // Outer golden rim
        drawCircle(
            color = Color(0xFFFFB703),
            radius = radius
        )
        // Inner ocean blue background
        drawCircle(
            color = Color(0xFF023047),
            radius = radius * 0.85f
        )
        // Compass coordinate ticks
        drawLine(
            color = Color(0xFFFFD166),
            start = Offset(w * 0.5f, h * 0.22f),
            end = Offset(w * 0.5f, h * 0.78f),
            strokeWidth = 3f
        )
        drawLine(
            color = Color(0xFFFFD166),
            start = Offset(w * 0.22f, h * 0.5f),
            end = Offset(w * 0.78f, h * 0.5f),
            strokeWidth = 3f
        )
        
        // Red pointer (North)
        val north = Path().apply {
            moveTo(w * 0.5f, h * 0.18f)
            lineTo(w * 0.44f, h * 0.5f)
            lineTo(w * 0.56f, h * 0.5f)
            close()
        }
        drawPath(north, color = Color(0xFFD00000))
        
        // White pointer (South)
        val south = Path().apply {
            moveTo(w * 0.5f, h * 0.82f)
            lineTo(w * 0.44f, h * 0.5f)
            lineTo(w * 0.56f, h * 0.5f)
            close()
        }
        drawPath(south, color = Color(0xFFF1F5F9))
        
        // Golden center pin
        drawCircle(
            color = Color(0xFFFFB703),
            radius = radius * 0.12f
        )
    }
}

@Composable
fun BackgroundWatermark(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val radius = size.minDimension * 0.35f
        val center = Offset(w * 0.5f, h * 0.45f) // Centered slightly higher for balance

        // Draw compass rose outer dashed circle
        drawCircle(
            color = Color(0xFFFFB703).copy(alpha = 0.05f),
            radius = radius,
            center = center,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 2f,
                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )
        )

        // Draw inner circle
        drawCircle(
            color = Color(0xFFFFB703).copy(alpha = 0.03f),
            radius = radius * 0.8f,
            center = center,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1f)
        )

        // Draw directional axes
        val directions = listOf(0f, 45f, 90f, 135f, 180f, 225f, 270f, 315f)
        for (angle in directions) {
            val rad = Math.toRadians(angle.toDouble())
            val endX = center.x + radius * Math.cos(rad).toFloat()
            val endY = center.y + radius * Math.sin(rad).toFloat()
            drawLine(
                color = Color(0xFFFFB703).copy(alpha = 0.04f),
                start = center,
                end = Offset(endX, endY),
                strokeWidth = 2f
            )
        }

        // Draw compass cardinal pointers (N, S, E, W lines)
        val cardinalPoints = listOf(
            Offset(center.x, center.y - radius * 1.05f) to Offset(center.x, center.y + radius * 1.05f),
            Offset(center.x - radius * 1.05f, center.y) to Offset(center.x + radius * 1.05f, center.y)
        )
        for (line in cardinalPoints) {
            drawLine(
                color = Color(0xFFFFD166).copy(alpha = 0.05f),
                start = line.first,
                end = line.second,
                strokeWidth = 3f
            )
        }
    }
}

// Basic parser that formats double asterisks (**word**) into bold styling in compose
private fun parseMarkdown(text: String): AnnotatedString {
    val builder = buildAnnotatedString {
        val parts = text.split("**")
        for (i in parts.indices) {
            if (i % 2 == 1) {
                // Odd indices contain bold elements
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = SecondaryLight)) {
                    append(parts[i])
                }
            } else {
                append(parts[i])
            }
        }
    }
    return builder
}
