package com.example.financeguide.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.*

// --- Auth DTOs ---

data class SupabaseUserMetadata(
    @SerializedName("name") val name: String,
    @SerializedName("avatar") val avatar: String
)

data class SupabaseSignUpRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("data") val data: SupabaseUserMetadata
)

data class SupabaseSignInRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class SupabaseUser(
    @SerializedName("id") val id: String,
    @SerializedName("user_metadata") val userMetadata: SupabaseUserMetadata?
)

data class SupabaseAuthResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("user") val user: SupabaseUser
)

// --- Database DTOs ---

data class SupabaseTransaction(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("amount") val amount: Double,
    @SerializedName("title") val title: String,
    @SerializedName("category") val category: String,
    @SerializedName("type") val type: String,
    @SerializedName("date") val date: Long,
    @SerializedName("note") val note: String?,
    @SerializedName("is_recurring") val isRecurring: Boolean
)

data class SupabaseBudget(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("category") val category: String,
    @SerializedName("month_year") val monthYear: String,
    @SerializedName("limit_amount") val limitAmount: Double
)

// --- Supabase API Interface ---

interface SupabaseApi {
    
    // Auth Endpoints
    @POST("auth/v1/signup")
    suspend fun signUp(
        @Header("apikey") apiKey: String,
        @Header("Authorization") token: String,
        @Body request: SupabaseSignUpRequest
    ): Response<SupabaseAuthResponse>

    @POST("auth/v1/token?grant_type=password")
    suspend fun signIn(
        @Header("apikey") apiKey: String,
        @Header("Authorization") token: String,
        @Body request: SupabaseSignInRequest
    ): Response<SupabaseAuthResponse>

    // Transactions Table
    @GET("rest/v1/transactions")
    suspend fun getTransactions(
        @Header("apikey") apiKey: String,
        @Header("Authorization") token: String,
        @Query("select") select: String = "*"
    ): Response<List<SupabaseTransaction>>

    @POST("rest/v1/transactions")
    suspend fun insertTransaction(
        @Header("apikey") apiKey: String,
        @Header("Authorization") token: String,
        @Header("Prefer") prefer: String = "return=representation",
        @Body transaction: SupabaseTransaction
    ): Response<List<SupabaseTransaction>>

    @DELETE("rest/v1/transactions")
    suspend fun deleteTransaction(
        @Header("apikey") apiKey: String,
        @Header("Authorization") token: String,
        @Query("id") filter: String
    ): Response<Unit>

    // Budgets Table
    @GET("rest/v1/budgets")
    suspend fun getBudgets(
        @Header("apikey") apiKey: String,
        @Header("Authorization") token: String,
        @Query("select") select: String = "*"
    ): Response<List<SupabaseBudget>>

    @POST("rest/v1/budgets")
    suspend fun insertBudget(
        @Header("apikey") apiKey: String,
        @Header("Authorization") token: String,
        @Header("Prefer") prefer: String = "return=representation",
        @Body budget: SupabaseBudget
    ): Response<List<SupabaseBudget>>

    @DELETE("rest/v1/budgets")
    suspend fun deleteBudget(
        @Header("apikey") apiKey: String,
        @Header("Authorization") token: String,
        @Query("id") filter: String
    ): Response<Unit>
}
