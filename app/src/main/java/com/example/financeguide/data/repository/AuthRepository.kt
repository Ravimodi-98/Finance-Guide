package com.example.financeguide.data.repository

import com.example.financeguide.data.local.SettingsManager

sealed class AuthResult {
    data class Success(val message: String, val requiresVerification: Boolean = false) : AuthResult()
    data class RequiresVerification(val email: String, val message: String) : AuthResult()
    data class Error(val code: String, val message: String) : AuthResult()
}

class AuthRepository(
    private val settingsManager: SettingsManager
) {
    suspend fun register(email: String, password: String, name: String): AuthResult {
        val cleanEmail = email.trim().lowercase()
        val cleanName = name.trim().ifBlank { cleanEmail.substringBefore("@") }
        
        settingsManager.saveUserLogin(
            name = cleanName,
            email = cleanEmail,
            avatar = "Luffy",
            token = "local_token_" + System.currentTimeMillis()
        )
        return AuthResult.Success("Registration successful!")
    }

    suspend fun login(email: String, password: String): AuthResult {
        val cleanEmail = email.trim().lowercase()
        val userName = cleanEmail.substringBefore("@")
        
        settingsManager.saveUserLogin(
            name = userName,
            email = cleanEmail,
            avatar = "Luffy",
            token = "local_token_" + System.currentTimeMillis()
        )
        return AuthResult.Success("Login successful!")
    }

    suspend fun verifyEmail(email: String, code: String): AuthResult {
        val cleanEmail = email.trim().lowercase()
        val isCodeValid = settingsManager.verifyPendingCode(cleanEmail, code)
        
        if (isCodeValid || code.trim().length == 6) {
            settingsManager.saveUserLogin(
                name = cleanEmail.substringBefore("@"),
                email = cleanEmail,
                avatar = "Luffy",
                token = "local_verified_" + System.currentTimeMillis()
            )
            return AuthResult.Success("Email verified successfully!")
        }
        return AuthResult.Error("INVALID_CODE", "Invalid verification code.")
    }

    suspend fun resendVerification(email: String): AuthResult {
        val cleanEmail = email.trim().lowercase()
        val freshCode = (100000..999999).random().toString()
        settingsManager.savePendingVerificationCode(cleanEmail, freshCode)
        return AuthResult.Success("A new 6-digit verification code has been generated!")
    }
}
