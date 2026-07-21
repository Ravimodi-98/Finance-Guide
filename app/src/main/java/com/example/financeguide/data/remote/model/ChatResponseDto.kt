package com.example.financeguide.data.remote.model

import com.google.gson.annotations.SerializedName

data class CommandDto(
    @SerializedName("action") val action: String,
    @SerializedName("amount") val amount: Double? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("category") val category: String? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("limitAmount") val limitAmount: Double? = null,
    @SerializedName("monthYear") val monthYear: String? = null,
    @SerializedName("friendEmail") val friendEmail: String? = null,
    @SerializedName("friendName") val friendName: String? = null,
    @SerializedName("groupName") val groupName: String? = null,
    @SerializedName("members") val members: List<String>? = null,
    @SerializedName("payeeName") val payeeName: String? = null
)

data class ChatResponseDto(
    @SerializedName("response") val response: String,
    @SerializedName("commands") val commands: List<CommandDto>? = null
)
