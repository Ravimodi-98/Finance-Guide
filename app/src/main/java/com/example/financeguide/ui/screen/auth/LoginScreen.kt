package com.example.financeguide.ui.screen.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeguide.StrawHatIcon
import com.example.financeguide.theme.*

@Composable
fun LoginScreen(
    onLoginSuccess: (name: String, email: String, avatar: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedAvatar by remember { mutableStateOf("Luffy") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val avatars = listOf(
        AvatarOption("Luffy", "🍖", Primary),
        AvatarOption("Zoro", "⚔️", IncomeGreen),
        AvatarOption("Nami", "🍊", Secondary),
        AvatarOption("Sanji", "🍳", Color(0xFF3B82F6)),
        AvatarOption("Chopper", "🌸", Color(0xFFEC4899))
    )

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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Custom Straw Hat Icon Logo
            StrawHatIcon(
                modifier = Modifier
                    .size(110.dp)
                    .padding(bottom = 8.dp)
            )

            Text(
                text = "Pirate Crew Sign In",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Enter your credentials and select your crew member profile to embark.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Name Field
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    nameError = null
                },
                label = { Text("Pirate Name") },
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

            // Email Field
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = null
                },
                label = { Text("Crew Email") },
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

            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = null
                },
                label = { Text("Crew Password") },
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

            Spacer(modifier = Modifier.height(24.dp))

            // Crew Avatar Selection
            Text(
                text = "Choose Crew Identity",
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

            Spacer(modifier = Modifier.height(32.dp))

            // Action Button
            Button(
                onClick = {
                    var hasError = false
                    if (name.isBlank()) {
                        nameError = "Pirate name is required"
                        hasError = true
                    }
                    if (email.isBlank() || !email.contains("@")) {
                        emailError = "Valid crew email is required"
                        hasError = true
                    }
                    if (password.length < 4) {
                        passwordError = "Password must be at least 4 characters"
                        hasError = true
                    }

                    if (!hasError) {
                        onLoginSuccess(name, email, selectedAvatar)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Secondary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "SET SAIL! ⚔️",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnSecondary
                )
            }
        }
    }
}

data class AvatarOption(val name: String, val emoji: String, val themeColor: Color)
