package com.example.financeguide.ui.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeguide.data.repository.AuthRepository
import com.example.financeguide.data.repository.AuthResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyEmailScreen(
    email: String,
    authRepository: AuthRepository,
    onVerificationSuccess: () -> Unit,
    onBack: () -> Unit
) {
    var code by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isResending by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var cooldownSeconds by remember { mutableStateOf(60) }

    val coroutineScope = rememberCoroutineScope()

    // 60-second countdown timer for resend code
    LaunchedEffect(cooldownSeconds) {
        if (cooldownSeconds > 0) {
            delay(1000L)
            cooldownSeconds -= 1
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Verify Email", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.MarkEmailRead,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Enter Verification Code",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "We sent a 6-digit verification code to:\n$email",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = code,
                        onValueChange = { if (it.length <= 6) code = it.filter { char -> char.isDigit() } },
                        label = { Text("6-Digit Code") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(
                            textAlign = TextAlign.Center,
                            fontSize = 24.sp,
                            letterSpacing = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    errorMessage?.let {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                    }

                    successMessage?.let {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (code.length == 6) {
                                coroutineScope.launch {
                                    isLoading = true
                                    errorMessage = null
                                    successMessage = null
                                    val result = authRepository.verifyEmail(email, code)
                                    isLoading = false
                                    when (result) {
                                        is AuthResult.Success -> {
                                            successMessage = result.message
                                            delay(800L)
                                            onVerificationSuccess()
                                        }
                                        is AuthResult.Error -> {
                                            errorMessage = result.message
                                        }
                                        else -> {}
                                    }
                                }
                            }
                        },
                        enabled = !isLoading && code.length == 6,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Verify Code", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(
                        onClick = {
                            if (cooldownSeconds == 0 && !isResending) {
                                coroutineScope.launch {
                                    isResending = true
                                    errorMessage = null
                                    successMessage = null
                                    val result = authRepository.resendVerification(email)
                                    isResending = false
                                    when (result) {
                                        is AuthResult.Success -> {
                                            successMessage = "New code sent to your email!"
                                            cooldownSeconds = 60
                                        }
                                        is AuthResult.Error -> {
                                            errorMessage = result.message
                                        }
                                        else -> {}
                                    }
                                }
                            }
                        },
                        enabled = cooldownSeconds == 0 && !isResending
                    ) {
                        if (isResending) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp))
                        } else if (cooldownSeconds > 0) {
                            Text("Resend Code in ${cooldownSeconds}s", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            Text("Resend Code", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}
