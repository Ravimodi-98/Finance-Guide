package com.example.financeguide.ui.screen.ai

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import com.example.financeguide.theme.LocalThemeStrings
import com.example.financeguide.ui.components.SpiderManAvatar
import com.example.financeguide.ui.components.SpiderWebWatermark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceAIScreen(
    viewModel: FinanceAIViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var inputText by remember { mutableStateOf("") }
    val strings = LocalThemeStrings.current
    val isSpidey = strings.chatbotName == "Spider-Man"
    val isBasic = strings.chatbotName == "FinBot"

    val listState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val isKeyboardOpen = WindowInsets.ime.getBottom(LocalDensity.current) > 0

    // Unique Theme Colors (No WhatsApp colors)
    val screenBg = when {
        isSpidey -> Color(0xFF0D0B12) // Web Suit Obsidian
        isBasic  -> Color(0xFF080D1A) // Finance Pro Obsidian Slate
        else     -> Color(0xFF0A111E) // One Piece Grand Line Navy
    }
    val headerBg = when {
        isSpidey -> Color(0xFF18101C) // Web Suit Tech Header
        isBasic  -> Color(0xFF0E1626) // Finance Pro Glass Header
        else     -> Color(0xFF121E33) // One Piece Marine Navy Header
    }
    val topBorderGradient = when {
        isSpidey -> listOf(Color(0xFFC1121F), Color(0xFF1D4ED8)) // Web Crimson to Spider Blue
        isBasic  -> listOf(Color(0xFF00E599), Color(0xFF00D2FF)) // Mint Emerald to Cyber Cyan
        else     -> listOf(Color(0xFFFFC72C), Color(0xFF1E88E5)) // Straw Hat Gold to Marine Blue
    }

    // Auto-scroll to the bottom when new messages arrive
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
            Column {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            NavigatorAvatar(modifier = Modifier.size(38.dp))
                            Column {
                                Text(
                                    text = strings.chatbotTitle,
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = if (uiState.isLoading) {
                                        when {
                                            isSpidey -> "Spider-Sense Thinking..."
                                            isBasic  -> "FinBot Analyzing..."
                                            else     -> "Nami Calculating..."
                                        }
                                    } else {
                                        when {
                                            isSpidey -> "AI Patrol Guide 🕸️"
                                            isBasic  -> "AI Wealth Advisor 📊"
                                            else     -> "AI Navigation Guide ⛵"
                                        }
                                    },
                                    color = if (uiState.isLoading) (
                                        when {
                                            isSpidey -> Color(0xFF60A5FA)
                                            isBasic  -> Color(0xFF34D399)
                                            else     -> Color(0xFFF6C445)
                                        }
                                    ) else Color.White.copy(alpha = 0.65f),
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
                        IconButton(onClick = { viewModel.clearChat() }) {
                            Icon(
                                imageVector = Icons.Rounded.DeleteOutline,
                                contentDescription = "Clear Chat",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = headerBg,
                        titleContentColor = Color.White
                    )
                )
                // Bottom Accent Border Line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(brush = Brush.horizontalGradient(topBorderGradient))
                )
            }
        },
        containerColor = screenBg
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(screenBg)
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
                    // Empty state showing theme welcome guidance
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        NavigatorAvatar(modifier = Modifier.size(if (isKeyboardOpen) 60.dp else 110.dp))
                        Spacer(modifier = Modifier.height(if (isKeyboardOpen) 8.dp else 20.dp))
                        Text(
                            text = strings.chatbotWelcomeTitle,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = if (isKeyboardOpen) 20.sp else 26.sp
                            ),
                            color = if (isSpidey) Color(0xFF60A5FA) else if (isBasic) Color(0xFF34D399) else Secondary,
                            textAlign = TextAlign.Center
                        )
                        if (!isKeyboardOpen) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = strings.chatbotWelcomeBody,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White.copy(alpha = 0.75f),
                                textAlign = TextAlign.Center,
                                lineHeight = 22.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = strings.chatbotWelcomeQuote,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isSpidey) Color(0xFFF6C445) else SecondaryLight,
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
                                isSpidey = isSpidey,
                                isBasic = isBasic,
                                onCopy = {
                                    clipboardManager.setText(AnnotatedString(message.text))
                                    Toast.makeText(context, strings.copiedToast, Toast.LENGTH_SHORT).show()
                                },
                                onShare = {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, message.text)
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Share Message"))
                                },
                                onRegenerate = {
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
                                TypingIndicatorRow(isSpidey = isSpidey, isBasic = isBasic)
                            }
                        }
                    }
                }
            }



            // Modern AI Assistant Prompt Card
            Surface(
                color = when {
                    isSpidey -> Color(0xFF18101C)
                    isBasic  -> Color(0xFF0E1626)
                    else     -> Color(0xFF121E33)
                },
                tonalElevation = 8.dp
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = when {
                        isSpidey -> Color(0xFF23182B)
                        isBasic  -> Color(0xFF172238)
                        else     -> Color(0xFF192A45)
                    },
                    border = BorderStroke(
                        1.dp,
                        when {
                            isSpidey -> Color(0xFFC1121F).copy(alpha = 0.45f)
                            isBasic  -> Color(0xFF00E599).copy(alpha = 0.35f)
                            else     -> Color(0xFFFFC72C).copy(alpha = 0.35f)
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                    ) {
                        TextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = {
                                Text(
                                    text = when {
                                        isSpidey -> "Ask Spider-Man anything..."
                                        isBasic  -> "Ask FinBot a financial question..."
                                        else     -> "Ask Nami a question..."
                                    },
                                    color = Color.White.copy(alpha = 0.45f),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
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
                            ),
                            modifier = Modifier.weight(1f),
                            maxLines = 4
                        )

                        val sendGradient = when {
                            isSpidey -> listOf(Color(0xFFC1121F), Color(0xFF1D4ED8))
                            isBasic  -> listOf(Color(0xFF00E599), Color(0xFF00D2FF))
                            else     -> listOf(Color(0xFFFFC72C), Color(0xFF1E88E5))
                        }

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = if (inputText.isNotBlank() && !uiState.isLoading) {
                                        Brush.linearGradient(sendGradient)
                                    } else {
                                        Brush.linearGradient(listOf(Color.White.copy(alpha = 0.15f), Color.White.copy(alpha = 0.15f)))
                                    }
                                )
                                .clickable(enabled = inputText.isNotBlank() && !uiState.isLoading) {
                                    if (inputText.isNotBlank() && !uiState.isLoading) {
                                        viewModel.sendMessage(inputText)
                                        inputText = ""
                                        keyboardController?.hide()
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.Send,
                                contentDescription = "Send",
                                tint = if (inputText.isNotBlank() && !uiState.isLoading) Color.White else Color.White.copy(alpha = 0.4f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: ChatMessage,
    isSpidey: Boolean,
    isBasic: Boolean = false,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onRegenerate: () -> Unit,
    isLast: Boolean
) {
    val bubbleShape = if (message.isFromUser) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp)
    } else {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp)
    }

    val alignment = if (message.isFromUser) Alignment.CenterEnd else Alignment.CenterStart

    val containerColor = if (message.isFromUser) {
        when {
            isSpidey -> Color(0xFF9E0B16) // Web Crimson for User
            isBasic  -> Color(0xFF008055) // Deep Mint Emerald for User
            else     -> Color(0xFF1E88E5) // Marine Ocean Blue for User
        }
    } else {
        when {
            isSpidey -> Color(0xFF1C1422) // Tech Suit Dark Slate for Bot
            isBasic  -> Color(0xFF172238) // Glass Slate for Bot
            else     -> Color(0xFF152238) // Pirate Navy for Bot
        }
    }

    val borderColor = if (!message.isFromUser) {
        when {
            isSpidey -> Color(0xFFC1121F).copy(alpha = 0.45f)
            isBasic  -> Color(0xFF00E599).copy(alpha = 0.35f)
            else     -> Color(0xFFFFC72C).copy(alpha = 0.40f)
        }
    } else {
        Color.Transparent
    }

    val timeText = remember(message.timestamp) {
        java.text.SimpleDateFormat("h:mm a", java.util.Locale.US).format(java.util.Date(message.timestamp))
    }

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
                border = if (borderColor != Color.Transparent) BorderStroke(1.dp, borderColor) else null
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                    Text(
                        text = parseMarkdown(message.text, isSpidey, isBasic),
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            lineHeight = 20.sp,
                            letterSpacing = 0.3.sp
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            text = timeText,
                            color = Color.White.copy(alpha = 0.55f),
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp)
                        )
                    }

                    if (!message.isFromUser) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .height(0.5.dp)
                                .background(Color.White.copy(alpha = 0.08f))
                        )
                        Row(
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(
                                onClick = onCopy,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = Color.White.copy(alpha = 0.6f),
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(
                                onClick = onShare,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Share,
                                    contentDescription = "Share",
                                    tint = Color.White.copy(alpha = 0.6f),
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                            if (isLast) {
                                Spacer(modifier = Modifier.width(6.dp))
                                IconButton(
                                    onClick = onRegenerate,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Refresh,
                                        contentDescription = "Regenerate",
                                        tint = Color.White.copy(alpha = 0.6f),
                                        modifier = Modifier.size(13.dp)
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
    val strings = LocalThemeStrings.current
    val isSpidey = strings.chatbotName == "Spider-Man"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Text(
            text = if (isSpidey) "Suggested Hero Prompts 🕸️" else "Suggested Crew Questions 📜",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = if (isSpidey) Color(0xFFF6C445) else SecondaryLight,
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
    val strings = LocalThemeStrings.current
    val isSpidey = strings.chatbotName == "Spider-Man"
    val isBasic = strings.chatbotName == "FinBot"

    val emoji = when {
        isSpidey -> when {
            label.contains("stocks") || label.contains("IPOs") || label.contains("market") -> "📈"
            label.contains("banking") || label.contains("interest") -> "🏦"
            label.contains("news") -> "📰"
            label.contains("spent") || label.contains("mission") -> "🕸️"
            label.contains("earned") || label.contains("S.H.I.E.L.D.") -> "🕷️"
            label.contains("budget") || label.contains("tech") -> "🧪"
            label.contains("reduce") || label.contains("save") || label.contains("split") -> "🛡️"
            else -> "🕸️"
        }
        isBasic -> when {
            label.contains("stocks") || label.contains("IPOs") || label.contains("portfolio") || label.contains("investing") -> "📈"
            label.contains("banking") || label.contains("interest") || label.contains("deposits") -> "🏦"
            label.contains("news") -> "📰"
            label.contains("spent") || label.contains("groceries") -> "💳"
            label.contains("earned") || label.contains("salary") -> "💵"
            label.contains("budget") -> "📊"
            label.contains("emergency") || label.contains("save") -> "🛡️"
            else -> "💎"
        }
        else -> when {
            label.contains("stocks") || label.contains("IPOs") || label.contains("portfolio") -> "📈"
            label.contains("banking") || label.contains("interest") || label.contains("deposits") -> "🏦"
            label.contains("news") -> "📰"
            label.contains("spent") || label.contains("Beli on") -> "🍖"
            label.contains("earned") || label.contains("treasure") -> "🪙"
            label.contains("budget") || label.contains("limit") -> "📊"
            label.contains("reduce") || label.contains("save") -> "📉"
            label.contains("emergency") -> "🛡️"
            else -> "⛵"
        }
    }

    val borderColor = when {
        isSpidey -> Color(0xFFC1121F).copy(alpha = 0.45f)
        isBasic  -> Color(0xFF00E599).copy(alpha = 0.45f)
        else     -> Color(0xFFF6C445).copy(alpha = 0.35f)
    }
    val chipBg = when {
        isSpidey -> Color(0xFF23182B)
        isBasic  -> Color(0xFF172238)
        else     -> Color(0xFF192A45)
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = chipBg,
        border = BorderStroke(1.dp, borderColor),
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
fun TypingIndicatorRow(isSpidey: Boolean = false, isBasic: Boolean = false) {
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
            shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp, bottomStart = 4.dp, bottomEnd = 14.dp),
            color = when {
                isSpidey -> Color(0xFF1C1422)
                isBasic  -> Color(0xFF172238)
                else     -> Color(0xFF152238)
            },
            border = BorderStroke(1.dp, when {
                isSpidey -> Color(0xFFC1121F).copy(alpha = 0.45f)
                isBasic  -> Color(0xFF00E599).copy(alpha = 0.35f)
                else     -> Color(0xFFFFC72C).copy(alpha = 0.40f)
            })
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

                Dot(opacity = dot1, color = when {
                    isSpidey -> Color(0xFFC1121F)
                    isBasic  -> Color(0xFF6366F1)
                    else     -> Secondary
                })
                Dot(opacity = dot2, color = when {
                    isSpidey -> Color(0xFF1D4ED8)
                    isBasic  -> Color(0xFF10B981)
                    else     -> Secondary
                })
                Dot(opacity = dot3, color = when {
                    isSpidey -> Color(0xFFF6C445)
                    isBasic  -> Color(0xFF818CF8)
                    else     -> Secondary
                })
            }
        }
    }
}

@Composable
fun Dot(opacity: Float, color: Color = Secondary) {
    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = maxOf(0.2f, opacity)))
    )
}

@Composable
fun NavigatorAvatar(modifier: Modifier = Modifier) {
    val strings = LocalThemeStrings.current
    when (strings.chatbotName) {
        "Spider-Man" -> SpiderManAvatar(modifier = modifier)
        "FinBot" -> FinBotAvatar(modifier = modifier)
        else -> Canvas(modifier = modifier) {
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
            val w = size.width
            val h = size.height
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
}

@Composable
fun FinBotAvatar(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val radius = size.minDimension / 2
        val w = size.width
        val h = size.height
        // Outer Mint Emerald ring
        drawCircle(color = Color(0xFF00E599), radius = radius)
        // Inner Dark Obsidian background
        drawCircle(color = Color(0xFF080D1A), radius = radius * 0.84f)
        // Cyber Cyan inner ring
        drawCircle(
            color = Color(0xFF00D2FF),
            radius = radius * 0.60f,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
        )
        // Mint Emerald cross lines
        drawLine(color = Color(0xFF00E599).copy(alpha = 0.6f), start = Offset(w*0.5f, h*0.2f), end = Offset(w*0.5f, h*0.8f), strokeWidth = 2.5f)
        drawLine(color = Color(0xFF00E599).copy(alpha = 0.6f), start = Offset(w*0.2f, h*0.5f), end = Offset(w*0.8f, h*0.5f), strokeWidth = 2.5f)
        // Center Radiant Gold dot
        drawCircle(color = Color(0xFFFFC72C), radius = radius * 0.14f)
    }
}

@Composable
fun BackgroundWatermark(modifier: Modifier = Modifier) {
    val strings = LocalThemeStrings.current
    when (strings.chatbotName) {
        "Spider-Man" -> SpiderWebWatermark(modifier = modifier)
        "FinBot" -> FinBotWatermark(modifier = modifier)
        else -> Canvas(modifier = modifier) {
            val w = size.width
            val h = size.height
            val radius = size.minDimension * 0.35f
            val center = Offset(w * 0.5f, h * 0.45f)

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
}

@Composable
fun FinBotWatermark(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val center = Offset(w * 0.5f, h * 0.45f)
        val radius = size.minDimension * 0.35f
        val mintAlpha = Color(0xFF00E599).copy(alpha = 0.05f)
        val cyanAlpha = Color(0xFF00D2FF).copy(alpha = 0.04f)
        // Outer mint ring
        drawCircle(
            color = mintAlpha,
            radius = radius,
            center = center,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 2f,
                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(14f, 8f), 0f)
            )
        )
        drawCircle(
            color = cyanAlpha,
            radius = radius * 0.6f,
            center = center,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f)
        )
        // Grid lines (financial graph feel)
        drawLine(color = mintAlpha, start = Offset(center.x, center.y - radius * 1.1f), end = Offset(center.x, center.y + radius * 1.1f), strokeWidth = 2f)
        drawLine(color = mintAlpha, start = Offset(center.x - radius * 1.1f, center.y), end = Offset(center.x + radius * 1.1f, center.y), strokeWidth = 2f)
        drawLine(color = cyanAlpha, start = Offset(center.x - radius * 0.7f, center.y - radius * 0.7f), end = Offset(center.x + radius * 0.7f, center.y + radius * 0.7f), strokeWidth = 1.5f)
        drawLine(color = cyanAlpha, start = Offset(center.x + radius * 0.7f, center.y - radius * 0.7f), end = Offset(center.x - radius * 0.7f, center.y + radius * 0.7f), strokeWidth = 1.5f)
        // Center dot
        drawCircle(color = Color(0xFF00E599).copy(alpha = 0.08f), radius = radius * 0.12f, center = center)
    }
}

private fun parseMarkdown(text: String, isSpidey: Boolean = false, isBasic: Boolean = false): AnnotatedString {
    val highlightColor = when {
        isSpidey -> Color(0xFFF6C445)
        isBasic  -> Color(0xFF00E599) // Mint Emerald for FinBot
        else     -> SecondaryLight
    }
    val builder = buildAnnotatedString {
        val parts = text.split("**")
        for (i in parts.indices) {
            if (i % 2 == 1) {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = highlightColor)) {
                    append(parts[i])
                }
            } else {
                append(parts[i])
            }
        }
    }
    return builder
}
