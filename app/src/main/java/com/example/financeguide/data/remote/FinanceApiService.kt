package com.example.financeguide.data.remote

import com.example.financeguide.data.remote.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface FinanceApiService {
    @POST("api/chat")
    suspend fun chat(
        @Body request: ChatRequestDto
    ): Response<ChatResponseDto>

    @POST("api/chat/reset")
    suspend fun resetChat(): Response<Unit>

    @POST("api/auth/register")
    suspend fun register(
        @Body request: Map<String, String>
    ): Response<RegisterResponseDto>

    @POST("api/auth/verify-email")
    suspend fun verifyEmail(
        @Body request: VerifyEmailRequestDto
    ): Response<VerifyEmailResponseDto>

    @POST("api/auth/login")
    suspend fun login(
        @Body request: Map<String, String>
    ): Response<AuthLoginResponseDto>

    @POST("api/auth/resend-verification")
    suspend fun resendVerification(
        @Body request: ResendVerificationRequestDto
    ): Response<ResendVerificationResponseDto>
}
