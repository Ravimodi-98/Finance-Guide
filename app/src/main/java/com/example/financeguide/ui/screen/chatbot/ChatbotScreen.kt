package com.example.financeguide.ui.screen.chatbot

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.NorthEast
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material.icons.rounded.VpnKey
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.financeguide.domain.model.ChatMessage
import com.example.financeguide.domain.model.MessageStatus
import com.example.financeguide.theme.LocalThemeStrings
import com.example.financeguide.ui.components.ThemeAppLogo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatbotScreen(
    viewModel: ChatbotViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val strings = LocalThemeStrings.current
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val listState = rememberLazyListState()
    val density = LocalDensity.current

    var inputText by remember { mutableStateOf("") }
    var showClearDialog by remember { mutableStateOf(false) }
    var showApiKeyDialog by remember { mutableStateOf(false) }
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }

    val apiKey by viewModel.apiKey.collectAsStateWithLifecycle()
    val selectedModel by viewModel.selectedModel.collectAsStateWithLifecycle()

    val isSpidey = themeMode == "spiderman"
    val isBasic = themeMode == "basic"

    // Theme Color Tokens
    val primaryAccent = when {
        isSpidey -> Color(0xFFE63946)
        isBasic  -> Color(0xFF00E599)
        else     -> Color(0xFFFFB703)
    }

    val secondaryAccent = when {
        isSpidey -> Color(0xFF3B82F6)
        isBasic  -> Color(0xFF00D2FF)
        else     -> Color(0xFFD00000)
    }

    val glowGradient = when {
        isSpidey -> listOf(Color(0xFFC1121F).copy(alpha = 0.25f), Color(0xFF1D4ED8).copy(alpha = 0.15f), Color.Transparent)
        isBasic  -> listOf(Color(0xFF00E599).copy(alpha = 0.20f), Color(0xFF00D2FF).copy(alpha = 0.15f), Color.Transparent)
        else     -> listOf(Color(0xFFFFB703).copy(alpha = 0.25f), Color(0xFFD00000).copy(alpha = 0.15f), Color.Transparent)
    }

    val userBubbleGradient = when {
        isSpidey -> listOf(Color(0xFFC1121F), Color(0xFF1D4ED8))
        isBasic  -> listOf(Color(0xFF00B4D8), Color(0xFF0077B6))
        else     -> listOf(Color(0xFFE63946), Color(0xFFD97706))
    }

    val headerGradient = when {
        isSpidey -> listOf(Color(0xFF780000), Color(0xFF0F1B3D))
        isBasic  -> listOf(Color(0xFF003822), Color(0xFF003547))
        else     -> listOf(Color(0xFF800000), Color(0xFF2D2510))
    }

    // Interactive topic prompt cards
    val topicCategories = remember(strings) {
        listOf(
            TopicCategory(
                title = "Smart Budgeting",
                icon = Icons.Rounded.Savings,
                prompts = listOf(
                    "How should I split my monthly salary using the 50/30/20 rule?",
                    "What are the best strategies to cut down impulsive spending?",
                    strings.chatbotSuggest1.ifBlank { "How do I save 20% of my income each month?" }
                )
            ),
            TopicCategory(
                title = "Market & Growth",
                icon = Icons.AutoMirrored.Rounded.TrendingUp,
                prompts = listOf(
                    "Explain the difference between Stocks, ETFs, and Mutual Funds",
                    "How does compound interest create exponential wealth over 10 years?",
                    "What should a beginner know before investing in IPOs?"
                )
            ),
            TopicCategory(
                title = "Security & Debt",
                icon = Icons.Rounded.Security,
                prompts = listOf(
                    "How many months of expenses should be in an Emergency Fund?",
                    "What is the avalanche vs snowball method for clearing loans?",
                    "How does inflation affect my bank savings?"
                )
            ),
            TopicCategory(
                title = "Quick Log & Wealth",
                icon = Icons.Rounded.AccountBalanceWallet,
                prompts = listOf(
                    strings.chatbotSuggest2.ifBlank { "I just received a 10% bonus, how should I invest it?" },
                    "Give me 5 actionable rules for financial freedom",
                    "How do tax-saving investment schemes work?"
                )
            )
        )
    }

    // Auto-scroll when new messages arrive or when loading state changes
    LaunchedEffect(messages.size, isLoading) {
        if (messages.isNotEmpty() || isLoading) {
            delay(100)
            listState.animateScrollToItem(index = if (isLoading) messages.size else (messages.size - 1).coerceAtLeast(0))
        }
    }

    // Auto-scroll when keyboard opens
    val imeBottom = WindowInsets.ime.getBottom(density)
    LaunchedEffect(imeBottom) {
        if (imeBottom > 0 && messages.isNotEmpty()) {
            delay(80)
            listState.animateScrollToItem((messages.size - 1).coerceAtLeast(0))
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            ModernChatTopBar(
                title = strings.chatbotTitle,
                statusText = strings.chatbotStatusOnline,
                themeMode = themeMode,
                headerGradient = headerGradient,
                primaryAccent = primaryAccent,
                hasMessages = messages.isNotEmpty(),
                hasApiKey = apiKey.isNotBlank(),
                onBack = onBack,
                onClearClick = { showClearDialog = true },
                onKeyClick = { showApiKeyDialog = true }
            )
        },
        bottomBar = {
            ModernChatInputBar(
                inputText = inputText,
                onInputTextChange = { inputText = it },
                isLoading = isLoading,
                primaryAccent = primaryAccent,
                secondaryAccent = secondaryAccent,
                quickSuggestions = if (messages.isEmpty()) {
                    topicCategories.getOrNull(selectedCategoryIndex)?.prompts ?: emptyList()
                } else emptyList(),
                onSend = { textToSend ->
                    inputText = ""
                    viewModel.sendMessage(textToSend)
                },
                onQuickPromptClick = { prompt ->
                    viewModel.sendMessage(prompt)
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Ambient glowing background orbs
            ChatAmbientGlow(glowGradient = glowGradient)

            Column(modifier = Modifier.fillMaxSize()) {
                if (apiKey.isBlank()) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.VpnKey,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Gemini API Key Required",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                    Text(
                                        text = "Paste your API key to activate the Finance AI.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { showApiKeyDialog = true },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Paste Key", fontSize = 12.sp)
                            }
                        }
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    if (messages.isEmpty() && !isLoading) {
                        // Creative Welcome Hero Screen
                        CreativeWelcomeView(
                            themeMode = themeMode,
                            strings = strings,
                            primaryAccent = primaryAccent,
                            secondaryAccent = secondaryAccent,
                            headerGradient = headerGradient,
                            categories = topicCategories,
                            selectedCategoryIndex = selectedCategoryIndex,
                            onSelectCategory = { selectedCategoryIndex = it },
                            onPromptSelected = { prompt ->
                                viewModel.sendMessage(prompt)
                            }
                        )
                    } else {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(messages, key = { it.id }) { message ->
                                ModernChatBubble(
                                    message = message,
                                    userGradient = userBubbleGradient,
                                    themeMode = themeMode,
                                    primaryAccent = primaryAccent,
                                    botName = strings.chatbotName,
                                    onCopy = { textToCopy ->
                                        clipboardManager.setText(AnnotatedString(textToCopy))
                                        Toast.makeText(context, strings.copiedToast, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }

                            if (isLoading) {
                                item {
                                    ModernTypingIndicator(
                                        themeMode = themeMode,
                                        primaryAccent = primaryAccent,
                                        secondaryAccent = secondaryAccent,
                                        botName = strings.chatbotName
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Clear Conversation Confirmation Dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Rounded.DeleteSweep,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "Clear Conversation?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    text = "This will remove all current messages in this chat session.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearConversation()
                        showClearDialog = false
                        Toast.makeText(context, "Chat cleared", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Clear All", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // API Key & Model Configuration Dialog
    if (showApiKeyDialog) {
        var tempKey by remember { mutableStateOf(apiKey) }
        var tempModel by remember { mutableStateOf(selectedModel) }

        val commonModels = listOf(
            "minimaxai/minimax-m3.1-flash",
            "gemini-3.8-flash",
            "gemini-3.5-flash",
            "deepseek-ai/deepseek-v4.1-flash",
            "meta/llama-3.1-8b-instruct"
        )

        AlertDialog(
            onDismissRequest = { showApiKeyDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Rounded.VpnKey,
                    contentDescription = null,
                    tint = primaryAccent,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "AI Model & API Key",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Paste your Gemini API key below. The AI will only work when an API key is configured.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = tempKey,
                        onValueChange = { tempKey = it },
                        label = { Text("Gemini API Key") },
                        placeholder = { Text("Paste API key here...") },
                        singleLine = false,
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            if (tempKey.isNotEmpty()) {
                                IconButton(onClick = { tempKey = "" }) {
                                    Icon(Icons.Rounded.Close, contentDescription = "Clear")
                                }
                            }
                        }
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                val clip = clipboardManager.getText()?.text
                                if (!clip.isNullOrBlank()) {
                                    tempKey = clip.trim()
                                    Toast.makeText(context, "Pasted from clipboard", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Clipboard is empty", Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
                            Icon(Icons.Rounded.ContentPaste, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Paste from Clipboard")
                        }
                    }

                    Text(
                        text = "Model Preset",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(commonModels) { modelName ->
                            val isSelected = tempModel == modelName
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) primaryAccent.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, primaryAccent) else null,
                                modifier = Modifier.clickable {
                                    tempModel = modelName
                                }
                            ) {
                                Text(
                                    text = modelName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isSelected) primaryAccent else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = tempModel,
                        onValueChange = { tempModel = it },
                        label = { Text("Selected Model") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Tip: Generate an API key from Google AI Studio (aistudio.google.com)",
                        style = MaterialTheme.typography.labelSmall,
                        color = primaryAccent.copy(alpha = 0.9f)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateApiKey(tempKey)
                        viewModel.updateModel(tempModel)
                        showApiKeyDialog = false
                        Toast.makeText(context, "API Key & Model saved!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryAccent)
                ) {
                    Text("Save & Apply")
                }
            },
            dismissButton = {
                TextButton(onClick = { showApiKeyDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

data class TopicCategory(
    val title: String,
    val icon: ImageVector,
    val prompts: List<String>
)

/**
 * Ambient background glow canvas for a futuristic, deep aesthetic.
 */
@Composable
fun ChatAmbientGlow(
    glowGradient: List<Color>,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ambientGlow")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Top right ambient radial glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = glowGradient,
                center = Offset(width * 0.85f, height * 0.15f),
                radius = width * 0.7f
            ),
            radius = width * 0.7f,
            center = Offset(width * 0.85f, height * 0.15f),
            alpha = pulseAlpha
        )

        // Bottom left ambient subtle glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = glowGradient,
                center = Offset(width * 0.15f, height * 0.80f),
                radius = width * 0.6f
            ),
            radius = width * 0.6f,
            center = Offset(width * 0.15f, height * 0.80f),
            alpha = pulseAlpha * 0.7f
        )
    }
}

/**
 * Modern Glassmorphic Top App Bar with live pulsating status badge.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernChatTopBar(
    title: String,
    statusText: String,
    themeMode: String,
    headerGradient: List<Color>,
    primaryAccent: Color,
    hasMessages: Boolean,
    hasApiKey: Boolean,
    onBack: () -> Unit,
    onClearClick: () -> Unit,
    onKeyClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "avatarPulse")
    val haloScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "haloScale"
    )
    val haloAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "haloAlpha"
    )

    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Pulsing Avatar Ring
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(44.dp)
                ) {
                    // Halo glow
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .scale(haloScale)
                            .clip(CircleShape)
                            .background(primaryAccent.copy(alpha = haloAlpha))
                    )

                    // Avatar Disk
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(headerGradient))
                            .border(1.5.dp, primaryAccent.copy(alpha = 0.8f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        ThemeAppLogo(
                            themeMode = themeMode,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // AI Chip
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = primaryAccent.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(0.8.dp, primaryAccent.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "AI",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 9.sp
                                ),
                                color = primaryAccent,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }

                    // Online indicator with green dot
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00E676))
                        )
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .clip(CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        actions = {
            IconButton(
                onClick = onKeyClick,
                modifier = Modifier
                    .clip(CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Rounded.VpnKey,
                    contentDescription = "AI API Key",
                    tint = if (hasApiKey) primaryAccent else MaterialTheme.colorScheme.error
                )
            }
            if (hasMessages) {
                IconButton(
                    onClick = onClearClick,
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .clip(CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.DeleteSweep,
                        contentDescription = "Clear Chat",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.85f)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        ),
        modifier = Modifier.shadow(elevation = 3.dp)
    )
}

/**
 * Creative Welcome Hero view when there are no messages yet.
 */
@Composable
fun CreativeWelcomeView(
    themeMode: String,
    strings: com.example.financeguide.theme.ThemeStrings,
    primaryAccent: Color,
    secondaryAccent: Color,
    headerGradient: List<Color>,
    categories: List<TopicCategory>,
    selectedCategoryIndex: Int,
    onSelectCategory: (Int) -> Unit,
    onPromptSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "heroOrbRotation")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotationAngle"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Orb Badge
        item {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .size(96.dp)
            ) {
                // Outer rotating gradient dash ring
                Canvas(
                    modifier = Modifier
                        .size(96.dp)
                        .rotate(rotationAngle)
                ) {
                    drawCircle(
                        brush = Brush.sweepGradient(listOf(primaryAccent, secondaryAccent, Color.Transparent, primaryAccent)),
                        radius = size.minDimension / 2,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = 2.5.dp.toPx(),
                            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(20f, 15f), 0f)
                        )
                    )
                }

                // Inner avatar sphere
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(headerGradient))
                        .border(2.dp, primaryAccent.copy(alpha = 0.7f), CircleShape)
                        .shadow(8.dp, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    ThemeAppLogo(
                        themeMode = themeMode,
                        modifier = Modifier.size(46.dp)
                    )
                }

                // Sparkle floating badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(primaryAccent)
                        .border(1.5.dp, MaterialTheme.colorScheme.background, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }

        // Welcome Headline
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = strings.chatbotWelcomeTitle,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = strings.chatbotWelcomeBody,
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }

        // Creative Quote Card
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(listOf(primaryAccent.copy(alpha = 0.4f), secondaryAccent.copy(alpha = 0.2f)))
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(primaryAccent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Lightbulb,
                            contentDescription = null,
                            tint = primaryAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Text(
                        text = strings.chatbotWelcomeQuote,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontStyle = FontStyle.Italic,
                            lineHeight = 18.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Section Title: Topic Hub
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp, start = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Psychology,
                    contentDescription = null,
                    tint = primaryAccent,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Explore Financial Topics",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.3.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Category Pills Row
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(categories.size) { index ->
                    val category = categories[index]
                    val isSelected = selectedCategoryIndex == index

                    val chipBg = if (isSelected) {
                        primaryAccent.copy(alpha = 0.2f)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    }

                    val chipBorderColor = if (isSelected) {
                        primaryAccent
                    } else {
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = chipBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, chipBorderColor),
                        modifier = Modifier.clickable { onSelectCategory(index) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = category.icon,
                                contentDescription = null,
                                tint = if (isSelected) primaryAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = category.title,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Suggested Prompt Cards under active category
        item {
            val currentPrompts = categories.getOrNull(selectedCategoryIndex)?.prompts ?: emptyList()

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                currentPrompts.forEach { prompt ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPromptSelected(prompt) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = prompt,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(primaryAccent.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.NorthEast,
                                    contentDescription = "Ask",
                                    tint = primaryAccent,
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

/**
 * Modern Chat Bubble with rich Markdown text rendering, custom styled cards,
 * copy feedback, and distinct visual geometry.
 */
@Composable
fun ModernChatBubble(
    message: ChatMessage,
    userGradient: List<Color>,
    themeMode: String,
    primaryAccent: Color,
    botName: String,
    onCopy: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val timeString = remember(message.timestamp) { timeFormat.format(Date(message.timestamp)) }
    var isCopied by remember { mutableStateOf(false) }

    LaunchedEffect(isCopied) {
        if (isCopied) {
            delay(2000)
            isCopied = false
        }
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!message.isUser) {
            // Miniature Bot Avatar
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(primaryAccent.copy(alpha = 0.15f))
                    .border(1.dp, primaryAccent.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = "AI",
                    tint = primaryAccent,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start,
            modifier = Modifier.weight(1f, fill = false)
        ) {
            if (message.isUser) {
                // User Bubble - Dynamic Vibrant Gradient
                Surface(
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 4.dp),
                    color = Color.Transparent,
                    shadowElevation = 3.dp,
                    modifier = Modifier.background(
                        brush = Brush.linearGradient(userGradient),
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 4.dp)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = message.text,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                lineHeight = 22.sp,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            color = Color.White
                        )
                    }
                }
            } else {
                // AI Bubble - Modern Frosted Glass Card with Markdown renderer
                val isError = message.status == MessageStatus.ERROR
                val bubbleBorder = if (isError) {
                    androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
                } else {
                    androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
                }

                Surface(
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 4.dp, bottomEnd = 20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                    shadowElevation = 2.dp,
                    border = bubbleBorder
                ) {
                    Column(
                        modifier = Modifier.padding(start = 14.dp, end = 14.dp, top = 12.dp, bottom = 8.dp)
                    ) {
                        // AI Bubble Header Label
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(bottom = 6.dp)
                        ) {
                            Text(
                                text = botName,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = primaryAccent
                            )
                            Box(
                                modifier = Modifier
                                    .size(3.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                            )
                            Text(
                                text = "Assistant",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }

                        // Rich Formatted Message Text
                        RichFormattedMarkdownText(
                            rawText = message.text,
                            primaryAccent = primaryAccent,
                            isError = isError
                        )

                        // Bottom Actions: Copy button & timestamp
                        if (!isError) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                                    modifier = Modifier.clickable {
                                        onCopy(message.text)
                                        isCopied = true
                                    }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isCopied) Icons.Rounded.Check else Icons.Rounded.ContentCopy,
                                            contentDescription = "Copy message",
                                            tint = if (isCopied) Color(0xFF00E676) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            text = if (isCopied) "Copied" else "Copy",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            color = if (isCopied) Color(0xFF00E676) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Timestamp + Delivery Checkmark
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(top = 3.dp, start = 4.dp, end = 4.dp)
            ) {
                Text(
                    text = timeString,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f)
                )
                if (message.isUser) {
                    Icon(
                        imageVector = Icons.Rounded.DoneAll,
                        contentDescription = "Delivered",
                        tint = primaryAccent.copy(alpha = 0.8f),
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

/**
 * Parses markdown blocks (headers, bullets, bold emphasis, code blocks)
 * and renders them beautifully with structured layout.
 */
@Composable
fun RichFormattedMarkdownText(
    rawText: String,
    primaryAccent: Color,
    isError: Boolean,
    modifier: Modifier = Modifier
) {
    if (isError) {
        Text(
            text = rawText,
            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp, fontSize = 14.5.sp),
            color = MaterialTheme.colorScheme.error,
            modifier = modifier
        )
        return
    }

    val lines = remember(rawText) { rawText.lines() }

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        var inCodeBlock = false
        val codeBuffer = StringBuilder()

        for (line in lines) {
            val trimmed = line.trim()

            if (trimmed.startsWith("```")) {
                if (inCodeBlock) {
                    // Render accumulated code block
                    CodeBlockCard(code = codeBuffer.toString().trimEnd())
                    codeBuffer.clear()
                    inCodeBlock = false
                } else {
                    inCodeBlock = true
                }
                continue
            }

            if (inCodeBlock) {
                codeBuffer.appendLine(line)
                continue
            }

            when {
                // Section Header (### or ## or #)
                trimmed.startsWith("### ") -> {
                    Text(
                        text = parseInlineMarkdown(trimmed.removePrefix("### ")),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        ),
                        color = primaryAccent,
                        modifier = Modifier.padding(top = 6.dp, bottom = 2.dp)
                    )
                }
                trimmed.startsWith("## ") -> {
                    Text(
                        text = parseInlineMarkdown(trimmed.removePrefix("## ")),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp
                        ),
                        color = primaryAccent,
                        modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
                    )
                }
                trimmed.startsWith("# ") -> {
                    Text(
                        text = parseInlineMarkdown(trimmed.removePrefix("# ")),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp
                        ),
                        color = primaryAccent,
                        modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
                    )
                }
                // Bullet points (* or -)
                trimmed.startsWith("* ") || trimmed.startsWith("- ") -> {
                    val bulletText = if (trimmed.startsWith("* ")) trimmed.removePrefix("* ") else trimmed.removePrefix("- ")
                    Row(
                        modifier = Modifier.padding(start = 4.dp, top = 2.dp, bottom = 2.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 7.dp)
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(primaryAccent)
                        )
                        Text(
                            text = parseInlineMarkdown(bulletText),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                lineHeight = 21.sp,
                                fontSize = 14.5.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                // Numbered list items (e.g. "1. ", "2. ")
                trimmed.matches(Regex("^\\d+\\.\\s.*")) -> {
                    val number = trimmed.substringBefore(".")
                    val content = trimmed.substringAfter(". ").trim()
                    Row(
                        modifier = Modifier.padding(start = 2.dp, top = 2.dp, bottom = 2.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = primaryAccent.copy(alpha = 0.15f),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Text(
                                text = number,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = primaryAccent,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                        Text(
                            text = parseInlineMarkdown(content),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                lineHeight = 21.sp,
                                fontSize = 14.5.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                // Empty lines for spacing
                trimmed.isEmpty() -> {
                    Spacer(modifier = Modifier.height(3.dp))
                }
                // Normal paragraph text
                else -> {
                    Text(
                        text = parseInlineMarkdown(line),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            lineHeight = 22.sp,
                            fontSize = 14.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // If code block was not closed
        if (inCodeBlock && codeBuffer.isNotEmpty()) {
            CodeBlockCard(code = codeBuffer.toString().trimEnd())
        }
    }
}

/**
 * Formats inline bold (**text**) and inline backticks (`code`) into AnnotatedString.
 */
fun parseInlineMarkdown(text: String): AnnotatedString {
    return buildAnnotatedString {
        var index = 0
        while (index < text.length) {
            val boldStart = text.indexOf("**", index)
            val codeStart = text.indexOf("`", index)

            val nextSpecial = when {
                boldStart != -1 && codeStart != -1 -> minOf(boldStart, codeStart)
                boldStart != -1 -> boldStart
                codeStart != -1 -> codeStart
                else -> -1
            }

            if (nextSpecial == -1) {
                append(text.substring(index))
                break
            }

            // Append regular text leading up to special marker
            if (nextSpecial > index) {
                append(text.substring(index, nextSpecial))
            }

            if (nextSpecial == boldStart) {
                val boldEnd = text.indexOf("**", boldStart + 2)
                if (boldEnd != -1) {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append(text.substring(boldStart + 2, boldEnd))
                    }
                    index = boldEnd + 2
                } else {
                    append("**")
                    index = boldStart + 2
                }
            } else {
                val codeEnd = text.indexOf("`", codeStart + 1)
                if (codeEnd != -1) {
                    withStyle(
                        style = SpanStyle(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium,
                            background = Color(0x33888888)
                        )
                    ) {
                        append(" ${text.substring(codeStart + 1, codeEnd)} ")
                    }
                    index = codeEnd + 1
                } else {
                    append("`")
                    index = codeStart + 1
                }
            }
        }
    }
}

/**
 * Renders a clean syntax/calculation code block inside a message.
 */
@Composable
fun CodeBlockCard(code: String) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF0F172A),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = code,
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                lineHeight = 17.sp
            ),
            color = Color(0xFFE2E8F0),
            modifier = Modifier.padding(10.dp)
        )
    }
}

/**
 * Futuristic Dynamic Soundwave & AI Thinking Visualizer.
 */
@Composable
fun ModernTypingIndicator(
    themeMode: String,
    primaryAccent: Color,
    secondaryAccent: Color,
    botName: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveTransition")

    val bar1Height by infiniteTransition.animateFloat(
        initialValue = 6f,
        targetValue = 24f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar1"
    )
    val bar2Height by infiniteTransition.animateFloat(
        initialValue = 18f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, delayMillis = 100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar2"
    )
    val bar3Height by infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = 26f,
        animationSpec = infiniteRepeatable(
            animation = tween(420, delayMillis = 200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar3"
    )
    val bar4Height by infiniteTransition.animateFloat(
        initialValue = 22f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(480, delayMillis = 300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar4"
    )

    val thinkingQuotes = remember {
        listOf(
            "Analyzing financial signals...",
            "Formulating smart advice...",
            "Scanning financial wisdom..."
        )
    }

    var quoteIndex by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(2200)
            quoteIndex = (quoteIndex + 1) % thinkingQuotes.size
        }
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(primaryAccent.copy(alpha = 0.15f))
                .border(1.dp, primaryAccent.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.AutoAwesome,
                contentDescription = "Thinking",
                tint = primaryAccent,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Surface(
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
            shadowElevation = 2.dp,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                Brush.horizontalGradient(listOf(primaryAccent.copy(alpha = 0.5f), secondaryAccent.copy(alpha = 0.2f)))
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Waveform Bars
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.height(26.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(3.5.dp)
                            .height(bar1Height.dp)
                            .clip(CircleShape)
                            .background(primaryAccent)
                    )
                    Box(
                        modifier = Modifier
                            .width(3.5.dp)
                            .height(bar2Height.dp)
                            .clip(CircleShape)
                            .background(secondaryAccent)
                    )
                    Box(
                        modifier = Modifier
                            .width(3.5.dp)
                            .height(bar3Height.dp)
                            .clip(CircleShape)
                            .background(primaryAccent)
                    )
                    Box(
                        modifier = Modifier
                            .width(3.5.dp)
                            .height(bar4Height.dp)
                            .clip(CircleShape)
                            .background(secondaryAccent)
                    )
                }

                // Rotating dynamic thinking text
                AnimatedContent(
                    targetState = quoteIndex,
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(300)) + slideInVertically { it / 2 })
                            .togetherWith(fadeOut(animationSpec = tween(300)))
                    },
                    label = "thinkingText"
                ) { target ->
                    Text(
                        text = thinkingQuotes[target],
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Modern floating glassmorphic input dock with quick suggestions carousel.
 */
@Composable
fun ModernChatInputBar(
    inputText: String,
    onInputTextChange: (String) -> Unit,
    isLoading: Boolean,
    primaryAccent: Color,
    secondaryAccent: Color,
    quickSuggestions: List<String>,
    onSend: (String) -> Unit,
    onQuickPromptClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val canSend = inputText.isNotBlank() && !isLoading

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
            .windowInsetsPadding(
                WindowInsets.navigationBars.only(WindowInsetsSides.Bottom).union(WindowInsets.ime)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        // Quick suggestion pills if any
        if (quickSuggestions.isNotEmpty() && inputText.isEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                items(quickSuggestions) { suggestion ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            primaryAccent.copy(alpha = 0.25f)
                        ),
                        modifier = Modifier.clickable { onQuickPromptClick(suggestion) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AutoAwesome,
                                contentDescription = null,
                                tint = primaryAccent,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = suggestion,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // Input Field & Action Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = onInputTextChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = "Ask about budgets, stocks, savings...",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                },
                trailingIcon = {
                    if (inputText.isNotEmpty()) {
                        IconButton(
                            onClick = { onInputTextChange("") },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(26.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryAccent,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    cursorColor = primaryAccent
                ),
                maxLines = 4
            )

            // Animated Send Button with Gradient
            val sendScale by animateFloatAsState(
                targetValue = if (canSend) 1.05f else 1.0f,
                label = "sendScale"
            )

            Surface(
                shape = CircleShape,
                color = if (canSend) primaryAccent else MaterialTheme.colorScheme.surfaceVariant,
                shadowElevation = if (canSend) 6.dp else 0.dp,
                modifier = Modifier
                    .size(48.dp)
                    .scale(sendScale)
                    .clip(CircleShape)
                    .clickable(
                        enabled = canSend,
                        onClick = { onSend(inputText) }
                    )
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.Send,
                        contentDescription = "Send",
                        tint = if (canSend) {
                            Color.Black
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
