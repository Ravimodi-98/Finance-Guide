package com.example.financeguide.data.remote.model

import com.google.gson.annotations.SerializedName

data class ChatMessageDto(
    @SerializedName("role") val role: String,
    @SerializedName("text") val text: String
)
