package com.example.financeguide.ui.screen.auth

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.example.financeguide.theme.*
import com.example.financeguide.ui.components.ThemeAppLogo
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    authRepository: AuthRepository,
    onLoginSuccess: (name: String, email: String, avatar: String, token: String) -> Unit,
    onNavigateToVerify: (email: String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val strings = LocalThemeStrings.current

    var isSignUpMode by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val isSpideyTheme = strings.chatbotName == "Spider-Man"
    val isBasicTheme = strings.chatbotName == "FinBot"

    val activeThemeMode = when {
        isSpideyTheme -> "spiderman"
        isBasicTheme  -> "basic"
        else          -> "onepiece"
    }

    var selectedAvatar by remember {
        mutableStateOf(
            when {
                isSpideyTheme -> "Spidey"
                isBasicTheme  -> "Investor"
                else          -> "Luffy"
            }
        )
    }

    val avatars = when {
        isSpideyTheme -> listOf(
            AvatarOption("Spidey", "🕷️", SpideyPrimary),
            AvatarOption("Miles", "⚡", Color(0xFFFF1744)),
            AvatarOption("Gwen", "🕸️", Color(0xFFF48FB1)),
            AvatarOption("Peter", "📷", SpideySecondary),
            AvatarOption("MJ", "💖", Color(0xFFE91E63))
        )
        isBasicTheme -> listOf(
            AvatarOption("Investor", "💼", Color(0xFF00E599)),
            AvatarOption("Trader", "📈", Color(0xFF00D2FF)),
            AvatarOption("Analyst", "📊", Color(0xFF8B5CF6)),
            AvatarOption("Planner", "🏛️", Color(0xFFFFC72C)),
            AvatarOption("Executive", "💎", Color(0xFF38BDF8))
        )
        else -> listOf(
            AvatarOption("Luffy", "🍖", Primary),
            AvatarOption("Zoro", "⚔️", IncomeGreen),
            AvatarOption("Nami", "🍊", Secondary),
            AvatarOption("Sanji", "🍳", Color(0xFF3B82F6)),
            AvatarOption("Chopper", "🌸", Color(0xFFEC4899))
        )
    }

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .systemBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ThemeAppLogo(
                themeMode = activeThemeMode,
                modifier = Modifier
                    .size(110.dp)
                    .padding(bottom = 8.dp)
            )

            Text(
                text = if (isSignUpMode) (
                    when {
                        isSpideyTheme -> "Suit Up with Hero Account"
                        isBasicTheme  -> "Create Finance Pro Account"
                        else          -> "Join the Crew"
                    }
                ) else strings.signIn,
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = if (isSignUpMode) 
                    "Create your account to receive a 6-digit email verification code." 
                    else strings.signInSubtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (isSignUpMode) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = null
                    },
                    label = { Text("Name") },
                    leadingIcon = { Icon(Icons.Rounded.Person, contentDescription = null, tint = Secondary) },
                    isError = nameError != null,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Secondary,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        focusedLabelColor = Secondary,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.5f)
                    )
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

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = null
                },
                label = { Text("Email Address") },
                leadingIcon = { Icon(Icons.Rounded.Email, contentDescription = null, tint = Secondary) },
                isError = emailError != null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Secondary,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                    focusedLabelColor = Secondary,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.5f)
                )
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

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = null
                },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Rounded.Lock, contentDescription = null, tint = Secondary) },
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
                    focusedBorderColor = Secondary,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                    focusedLabelColor = Secondary,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.5f)
                )
            )
            AnimatedVisibility(visible = passwordError != null) {
                Text(
                    text = passwordError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.Start).padding(start = 4.dp, top = 2.dp)
                )
            }

            if (isSignUpMode) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Choose Identity",
                    style = MaterialTheme.typography.titleMedium,
                    color = Secondary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    avatars.forEach { option ->
                        val isSelected = selectedAvatar == option.name
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { selectedAvatar = option.name }
                                .padding(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) option.themeColor.copy(alpha = 0.25f) else SurfaceDarkVariant)
                                    .border(
                                        border = BorderStroke(
                                            width = if (isSelected) 3.dp else 1.dp,
                                            color = if (isSelected) option.themeColor else Color.White.copy(alpha = 0.1f)
                                        ),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = option.emoji, fontSize = 26.sp)
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

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    var hasError = false
                    if (isSignUpMode && name.isBlank()) {
                        nameError = "Name is required"
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
                colors = ButtonDefaults.buttonColors(containerColor = Secondary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = OnSecondary, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = if (isSignUpMode) strings.registerButton else strings.signInButton,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    val defaultGuestName = when {
                        isSpideyTheme -> "Web Slinger Agent"
                        isBasicTheme  -> "Finance Investor"
                        else          -> "Guest Pirate"
                    }
                    val defaultGuestEmail = when {
                        isSpideyTheme -> "agent@shield.org"
                        isBasicTheme  -> "pro.user@financeguide.ai"
                        else          -> "guest@strawhats.org"
                    }
                    val guestName = if (name.isBlank()) defaultGuestName else name
                    val guestEmail = if (email.isBlank()) defaultGuestEmail else email
                    Toast.makeText(context, "Continuing in Offline Mode", Toast.LENGTH_SHORT).show()
                    onLoginSuccess(guestName, guestEmail, selectedAvatar, "")
                },
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading
            ) {
                Text(
                    text = strings.offlineButton,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isSignUpMode) "Already registered? Sign In" else "New to the app? Create account & verify email!",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Secondary,
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

data class AvatarOption(val name: String, val emoji: String, val themeColor: Color)
