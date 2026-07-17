package com.example.financeguide.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Response
import retrofit2.http.*

// --- Auth DTOs ---

@JsonClass(generateAdapter = true)
data class SupabaseUserMetadata(
    @Json(name = "name") val name: String,
    @Json(name = "avatar") val avatar: String
)

@JsonClass(generateAdapter = true)
data class SupabaseSignUpRequest(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String,
    @Json(name = "data") val data: SupabaseUserMetadata
)

@JsonClass(generateAdapter = true)
data class SupabaseSignInRequest(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String
)

@JsonClass(generateAdapter = true)
data class SupabaseUser(
    @Json(name = "id") val id: String,
    @Json(name = "user_metadata") val userMetadata: SupabaseUserMetadata?
)

@JsonClass(generateAdapter = true)
data class SupabaseAuthResponse(
    @Json(name = "access_token") val accessToken: String,
    @Json(name = "user") val user: SupabaseUser
)

// --- Database DTOs ---

@JsonClass(generateAdapter = true)
data class SupabaseTransaction(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "amount") val amount: Double,
    @Json(name = "title") val title: String,
    @Json(name = "category") val category: String,
    @Json(name = "type") val type: String,
    @Json(name = "date") val date: Long,
    @Json(name = "note") val note: String?,
    @Json(name = "is_recurring") val isRecurring: Boolean
)

@JsonClass(generateAdapter = true)
data class SupabaseBudget(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "category") val category: String,
    @Json(name = "month_year") val monthYear: String,
    @Json(name = "limit_amount") val limitAmount: Double
)

// --- Supabase API Interface ---

interface SupabaseApi {
    
    // Auth Endpoints
    @POST("auth/v1/signup")
    suspend fun signUp(
        @Header("apikey") apiKey: String,
        @Body request: SupabaseSignUpRequest
    ): Response<SupabaseAuthResponse>

    @POST("auth/v1/token?grant_type=password")
    suspend fun signIn(
        @Header("apikey") apiKey: String,
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
