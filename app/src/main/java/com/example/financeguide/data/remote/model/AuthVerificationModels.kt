package com.example.financeguide.data.remote.model

import com.google.gson.annotations.SerializedName

data class RegisterResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("requiresVerification") val requiresVerification: Boolean = false,
    @SerializedName("message") val message: String
)

data class VerifyEmailRequestDto(
    @SerializedName("email") val email: String,
    @SerializedName("code") val code: String
)

data class VerifyEmailResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("accessToken") val accessToken: String? = null,
    @SerializedName("refreshToken") val refreshToken: String? = null
)

data class ResendVerificationRequestDto(
    @SerializedName("email") val email: String
)

data class ResendVerificationResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("remainingSeconds") val remainingSeconds: Int? = null
)

data class AuthLoginResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("error") val error: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("accessToken") val accessToken: String? = null,
    @SerializedName("refreshToken") val refreshToken: String? = null
)
