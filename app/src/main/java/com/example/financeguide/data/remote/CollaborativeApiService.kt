package com.example.financeguide.data.remote

import com.example.financeguide.data.remote.model.*
import retrofit2.Response
import retrofit2.http.*

interface CollaborativeApiService {

    // --- Auth ---
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequestDto): Response<AuthResponseDto>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<AuthResponseDto>

    @POST("api/auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequestDto): Response<RefreshTokenResponseDto>

    @POST("api/auth/logout")
    suspend fun logout(@Body request: RefreshTokenRequestDto): Response<Unit>

    // --- Users & Profile ---
    @GET("api/users/me")
    suspend fun getProfile(): Response<UserDto>

    @GET("api/users/search")
    suspend fun searchUsers(@Query("q") query: String): Response<List<UserDto>>

    // --- Friends ---
    @GET("api/friends")
    suspend fun getFriends(): Response<List<UserDto>>

    @POST("api/friends/request")
    suspend fun sendFriendRequest(@Body body: Map<String, String>): Response<Unit>

    @POST("api/friends/accept")
    suspend fun acceptFriendRequest(@Body body: Map<String, String>): Response<Unit>

    // --- Groups ---
    @POST("api/groups")
    suspend fun createGroup(@Body request: CreateGroupRequestDto): Response<GroupDto>

    @GET("api/groups")
    suspend fun getGroups(): Response<List<GroupDto>>

    @GET("api/groups/{id}")
    suspend fun getGroupById(@Path("id") groupId: String): Response<GroupDto>

    @POST("api/groups/{id}/invite")
    suspend fun inviteMember(@Path("id") groupId: String, @Body body: Map<String, String>): Response<Unit>

    // --- Expenses ---
    @POST("api/expenses")
    suspend fun createExpense(@Body request: CreateExpenseRequestDto): Response<CollaborativeExpenseDto>

    @GET("api/groups/{groupId}/expenses")
    suspend fun getExpensesByGroup(@Path("groupId") groupId: String): Response<List<CollaborativeExpenseDto>>

    @DELETE("api/expenses/{id}")
    suspend fun deleteExpense(@Path("id") expenseId: String): Response<Unit>

    // --- Balances & Settlements ---
    @GET("api/groups/{groupId}/balances")
    suspend fun getBalancesByGroup(@Path("groupId") groupId: String): Response<GroupBalancesResponseDto>

    @POST("api/settlements")
    suspend fun createSettlement(@Body request: CreateSettlementRequestDto): Response<SettlementDto>

    @GET("api/settlements")
    suspend fun getSettlements(): Response<List<SettlementDto>>
}
