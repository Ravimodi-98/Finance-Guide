package com.example.financeguide.data.remote.model

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("avatarUrl") val avatarUrl: String? = null,
    @SerializedName("themePreference") val themePreference: String = "PRO",
    @SerializedName("onlineStatus") val onlineStatus: Boolean = false
)

data class AuthResponseDto(
    @SerializedName("user") val user: UserDto,
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String
)

data class RegisterRequestDto(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("themePreference") val themePreference: String = "PRO"
)

data class LoginRequestDto(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class RefreshTokenRequestDto(
    @SerializedName("refreshToken") val refreshToken: String
)

data class RefreshTokenResponseDto(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String
)

data class GroupDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("icon") val icon: String? = "group",
    @SerializedName("description") val description: String? = null,
    @SerializedName("createdById") val createdById: String,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("members") val members: List<GroupMemberDto>? = emptyList()
)

data class GroupMemberDto(
    @SerializedName("id") val id: String,
    @SerializedName("groupId") val groupId: String,
    @SerializedName("userId") val userId: String,
    @SerializedName("role") val role: String = "MEMBER",
    @SerializedName("user") val user: UserDto? = null
)

data class CreateGroupRequestDto(
    @SerializedName("name") val name: String,
    @SerializedName("icon") val icon: String? = "group",
    @SerializedName("description") val description: String? = null,
    @SerializedName("memberIds") val memberIds: List<String> = emptyList()
)

data class ExpenseSplitDto(
    @SerializedName("userId") val userId: String,
    @SerializedName("owedAmount") val owedAmount: Double,
    @SerializedName("percentage") val percentage: Double? = null,
    @SerializedName("shareCount") val shareCount: Double? = null
)

data class CreateExpenseRequestDto(
    @SerializedName("groupId") val groupId: String? = null,
    @SerializedName("description") val description: String,
    @SerializedName("totalAmount") val totalAmount: Double,
    @SerializedName("currency") val currency: String = "USD",
    @SerializedName("category") val category: String = "General",
    @SerializedName("paidById") val paidById: String,
    @SerializedName("splitType") val splitType: String = "EQUAL",
    @SerializedName("splits") val splits: List<ExpenseSplitDto>,
    @SerializedName("notes") val notes: String? = null
)

data class CollaborativeExpenseDto(
    @SerializedName("id") val id: String,
    @SerializedName("groupId") val groupId: String? = null,
    @SerializedName("description") val description: String,
    @SerializedName("totalAmount") val totalAmount: Double,
    @SerializedName("currency") val currency: String = "USD",
    @SerializedName("category") val category: String = "General",
    @SerializedName("paidById") val paidById: String,
    @SerializedName("paidByName") val paidByName: String? = null,
    @SerializedName("splitType") val splitType: String = "EQUAL",
    @SerializedName("notes") val notes: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("splits") val splits: List<ExpenseSplitDto>? = emptyList()
)

data class DebtTransactionDto(
    @SerializedName("fromUserId") val fromUserId: String,
    @SerializedName("fromUserName") val fromUserName: String,
    @SerializedName("toUserId") val toUserId: String,
    @SerializedName("toUserName") val toUserName: String,
    @SerializedName("amount") val amount: Double
)

data class GroupBalancesResponseDto(
    @SerializedName("groupId") val groupId: String,
    @SerializedName("simplifiedDebts") val simplifiedDebts: List<DebtTransactionDto>
)

data class CreateSettlementRequestDto(
    @SerializedName("groupId") val groupId: String? = null,
    @SerializedName("payeeId") val payeeId: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("currency") val currency: String = "USD",
    @SerializedName("note") val note: String? = null
)

data class SettlementDto(
    @SerializedName("id") val id: String,
    @SerializedName("groupId") val groupId: String? = null,
    @SerializedName("payerId") val payerId: String,
    @SerializedName("payerName") val payerName: String? = null,
    @SerializedName("payeeId") val payeeId: String,
    @SerializedName("payeeName") val payeeName: String? = null,
    @SerializedName("amount") val amount: Double,
    @SerializedName("currency") val currency: String = "USD",
    @SerializedName("note") val note: String? = null,
    @SerializedName("settledAt") val settledAt: String? = null
)
