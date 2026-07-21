package com.example.financeguide.data.repository

import com.example.financeguide.data.local.AuthDataStore
import com.example.financeguide.data.remote.CollaborativeApiService
import com.example.financeguide.data.remote.model.LoginRequestDto
import com.example.financeguide.data.remote.model.RegisterRequestDto
import com.example.financeguide.data.remote.model.UserDto
import kotlinx.coroutines.flow.Flow

class AuthRepository(
    private val apiService: CollaborativeApiService,
    private val authDataStore: AuthDataStore
) {
    val accessTokenFlow: Flow<String?> = authDataStore.accessTokenFlow
    val userIdFlow: Flow<String?> = authDataStore.userIdFlow

    suspend fun register(
        email: String,
        password: String,
        name: String,
        phone: String? = null,
        theme: String = "PRO"
    ): Result<UserDto> {
        return try {
            val response = apiService.register(
                RegisterRequestDto(email, password, name, phone, theme)
            )
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                authDataStore.saveSession(
                    body.accessToken,
                    body.refreshToken,
                    body.user.id,
                    body.user.email,
                    body.user.name,
                    body.user.themePreference
                )
                Result.success(body.user)
            } else {
                Result.failure(Exception("Registration failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<UserDto> {
        return try {
            val response = apiService.login(LoginRequestDto(email, password))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                authDataStore.saveSession(
                    body.accessToken,
                    body.refreshToken,
                    body.user.id,
                    body.user.email,
                    body.user.name,
                    body.user.themePreference
                )
                Result.success(body.user)
            } else {
                Result.failure(Exception("Login failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        authDataStore.clearSession()
    }
}
