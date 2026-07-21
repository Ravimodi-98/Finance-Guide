package com.example.financeguide.data.repository

import com.example.financeguide.data.local.SettingsManager
import com.example.financeguide.data.remote.FinanceApiService
import com.example.financeguide.data.remote.model.*

sealed class AuthResult {
    data class Success(val message: String, val requiresVerification: Boolean = false) : AuthResult()
    data class RequiresVerification(val email: String, val message: String) : AuthResult()
    data class Error(val code: String, val message: String) : AuthResult()
}

class AuthRepository(
    private val apiService: FinanceApiService,
    private val settingsManager: SettingsManager
) {
    suspend fun register(email: String, password: String, name: String): AuthResult {
        return try {
            val response = apiService.register(mapOf("email" to email, "password" to password, "name" to name))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                AuthResult.RequiresVerification(
                    email = email,
                    message = body.message
                )
            } else {
                AuthResult.Error("REGISTRATION_FAILED", response.message())
            }
        } catch (e: Exception) {
            AuthResult.Error("NETWORK_ERROR", e.message ?: "Failed to connect to backend server")
        }
    }

    suspend fun login(email: String, password: String): AuthResult {
        return try {
            val response = apiService.login(mapOf("email" to email, "password" to password))
            val body = response.body()

            if (response.code() == 403 || (body != null && body.error == "EMAIL_NOT_VERIFIED")) {
                return AuthResult.RequiresVerification(
                    email = email,
                    message = body?.message ?: "Please verify your email first."
                )
            }

            if (response.isSuccessful && body != null && body.success) {
                settingsManager.saveUserLogin(
                    name = email.substringBefore("@"),
                    email = email,
                    avatar = "Luffy",
                    token = body.accessToken ?: ""
                )
                AuthResult.Success("Login successful!")
            } else {
                AuthResult.Error(body?.error ?: "LOGIN_FAILED", body?.message ?: "Invalid email or password")
            }
        } catch (e: Exception) {
            AuthResult.Error("NETWORK_ERROR", e.message ?: "Failed to connect to backend server")
        }
    }

    suspend fun verifyEmail(email: String, code: String): AuthResult {
        return try {
            val response = apiService.verifyEmail(VerifyEmailRequestDto(email, code))
            val body = response.body()
            if (response.isSuccessful && body != null && body.success) {
                if (!body.accessToken.isNullOrEmpty()) {
                    settingsManager.saveUserLogin(
                        name = email.substringBefore("@"),
                        email = email,
                        avatar = "Luffy",
                        token = body.accessToken
                    )
                }
                AuthResult.Success(body.message)
            } else {
                val errorMsg = body?.message ?: "Invalid or expired verification code."
                AuthResult.Error("VERIFICATION_FAILED", errorMsg)
            }
        } catch (e: Exception) {
            AuthResult.Error("NETWORK_ERROR", e.message ?: "Failed to connect to backend server")
        }
    }

    suspend fun resendVerification(email: String): AuthResult {
        return try {
            val response = apiService.resendVerification(ResendVerificationRequestDto(email))
            val body = response.body()
            if (response.isSuccessful && body != null && body.success) {
                AuthResult.Success(body.message)
            } else {
                val errorMsg = body?.message ?: "Failed to resend verification code."
                AuthResult.Error("RESEND_FAILED", errorMsg)
            }
        } catch (e: Exception) {
            AuthResult.Error("NETWORK_ERROR", e.message ?: "Failed to connect to backend server")
        }
    }
}
