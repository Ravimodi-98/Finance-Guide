package com.example.financeguide.ui.screen.auth

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeguide.data.repository.AuthRepository
import com.example.financeguide.data.repository.AuthResult
import kotlinx.coroutines.launch

private val BrandIndigo = Color(0xFF6366F1)
private val BrandEmerald = Color(0xFF10B981)
private val SurfaceDark = Color(0xFF0F172A)
private val CardDark = Color(0xFF1E293B)
private val InputBorderDark = Color(0xFF334155)

data class AvatarOption(val name: String, val emoji: String, val themeColor: Color)

@Composable
fun LoginScreen(
    authRepository: AuthRepository,
    onLoginSuccess: (name: String, email: String, avatar: String, token: String) -> Unit,
    onNavigateToVerify: (email: String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isSignUpMode by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    var selectedAvatar by remember { mutableStateOf("Investor") }

    val avatarOptions = listOf(
        AvatarOption("Investor", "💼", Color(0xFF10B981)),
        AvatarOption("Trader", "📈", Color(0xFF06B6D4)),
        AvatarOption("Analyst", "📊", Color(0xFF6366F1)),
        AvatarOption("Planner", "🏛️", Color(0xFFF59E0B)),
        AvatarOption("Executive", "💎", Color(0xFF38BDF8))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        SurfaceDark,
                        Color(0xFF1E293B),
                        SurfaceDark
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .systemBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Professional Fintech Logo Badge
            ProfessionalLogo(
                modifier = Modifier
                    .size(90.dp)
                    .padding(bottom = 12.dp)
            )

            Text(
                text = if (isSignUpMode) "Create Your Account" else "Welcome Back",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                ),
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Text(
                text = if (isSignUpMode) 
                    "Register to receive a 6-digit email verification code & access smart AI finance tools." 
                    else "Sign in to access your financial portfolio, budgets, and AI assistant.",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                color = Color.White.copy(alpha = 0.65f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Name Field (Sign Up mode)
            if (isSignUpMode) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = null
                    },
                    label = { Text("Full Name") },
                    leadingIcon = { Icon(Icons.Rounded.Person, contentDescription = null, tint = BrandIndigo) },
                    isError = nameError != null,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandIndigo,
                        unfocusedBorderColor = InputBorderDark,
                        focusedLabelColor = BrandIndigo,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
                        focusedContainerColor = CardDark,
                        unfocusedContainerColor = CardDark
                    ),
                    shape = RoundedCornerShape(14.dp)
                )
                AnimatedVisibility(visible = nameError != null) {
                    Text(
                        text = nameError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.Start).padding(start = 4.dp, top = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Email Field
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = null
                },
                label = { Text("Email Address") },
                leadingIcon = { Icon(Icons.Rounded.Email, contentDescription = null, tint = BrandIndigo) },
                isError = emailError != null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandIndigo,
                    unfocusedBorderColor = InputBorderDark,
                    focusedLabelColor = BrandIndigo,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
                    focusedContainerColor = CardDark,
                    unfocusedContainerColor = CardDark
                ),
                shape = RoundedCornerShape(14.dp)
            )
            AnimatedVisibility(visible = emailError != null) {
                Text(
                    text = emailError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.Start).padding(start = 4.dp, top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = null
                },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Rounded.Lock, contentDescription = null, tint = BrandIndigo) },
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                            contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                            tint = Color.White.copy(alpha = 0.5f)
                        )
                    }
                },
                isError = passwordError != null,
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandIndigo,
                    unfocusedBorderColor = InputBorderDark,
                    focusedLabelColor = BrandIndigo,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
                    focusedContainerColor = CardDark,
                    unfocusedContainerColor = CardDark
                ),
                shape = RoundedCornerShape(14.dp)
            )
            AnimatedVisibility(visible = passwordError != null) {
                Text(
                    text = passwordError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.Start).padding(start = 4.dp, top = 2.dp)
                )
            }

            // Professional Profile Identity Selection (Sign Up mode)
            if (isSignUpMode) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Select Investor Role",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = BrandIndigo,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    avatarOptions.forEach { option ->
                        val isSelected = selectedAvatar == option.name
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { selectedAvatar = option.name }
                                .padding(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) option.themeColor.copy(alpha = 0.2f) else CardDark)
                                    .border(
                                        border = BorderStroke(
                                            width = if (isSelected) 2.5.dp else 1.dp,
                                            color = if (isSelected) option.themeColor else InputBorderDark
                                        ),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = option.emoji, fontSize = 24.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = option.name,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) option.themeColor else Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Primary Action Button
            Button(
                onClick = {
                    var hasError = false
                    if (isSignUpMode && name.isBlank()) {
                        nameError = "Full name is required"
                        hasError = true
                    }
                    if (email.isBlank() || !email.contains("@")) {
                        emailError = "Valid email address is required"
                        hasError = true
                    }
                    if (password.length < 6) {
                        passwordError = "Password must be at least 6 characters"
                        hasError = true
                    }

                    if (!hasError && !isLoading) {
                        isLoading = true
                        coroutineScope.launch {
                            try {
                                if (isSignUpMode) {
                                    val result = authRepository.register(email, password, name)
                                    when (result) {
                                        is AuthResult.RequiresVerification -> {
                                            Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                                            onNavigateToVerify(result.email)
                                        }
                                        is AuthResult.Error -> {
                                            Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                                        }
                                        else -> {}
                                    }
                                } else {
                                    val result = authRepository.login(email, password)
                                    when (result) {
                                        is AuthResult.Success -> {
                                            Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                                            onLoginSuccess(
                                                email.substringBefore("@").replaceFirstChar { it.uppercase() },
                                                email,
                                                selectedAvatar,
                                                "token_active"
                                            )
                                        }
                                        is AuthResult.RequiresVerification -> {
                                            Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                                            onNavigateToVerify(result.email)
                                        }
                                        is AuthResult.Error -> {
                                            Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Authentication error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandIndigo),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = if (isSignUpMode) "Create Account" else "Sign In",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Offline Mode Button
            OutlinedButton(
                onClick = {
                    val guestName = if (name.isBlank()) "Finance Investor" else name
                    val guestEmail = if (email.isBlank()) "investor@financeguide.ai" else email
                    Toast.makeText(context, "Continuing in Offline Mode", Toast.LENGTH_SHORT).show()
                    onLoginSuccess(guestName, guestEmail, selectedAvatar, "")
                },
                border = BorderStroke(1.dp, InputBorderDark),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White.copy(alpha = 0.8f)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isLoading
            ) {
                Text(
                    text = "Continue Offline",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Toggle Mode Link
            Text(
                text = if (isSignUpMode) "Already have an account? Sign In" else "Don't have an account? Register",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = BrandIndigo,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clickable { 
                        if (!isLoading) {
                            isSignUpMode = !isSignUpMode
                            nameError = null
                            emailError = null
                            passwordError = null
                        }
                    }
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun ProfessionalLogo(modifier: Modifier = Modifier) {
    androidx.compose.foundation.Image(
        painter = androidx.compose.ui.res.painterResource(id = com.example.financeguide.R.drawable.app_logo),
        contentDescription = "Finance Guide Logo",
        modifier = modifier
    )
}
