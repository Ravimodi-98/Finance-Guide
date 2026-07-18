package com.example.financeguide.data.remote.model

import com.google.gson.annotations.SerializedName

data class ChatRequestDto(
    @SerializedName("message") val message: String,
    @SerializedName("history") val history: List<ChatMessageDto>,
    @SerializedName("theme") val theme: String = "onepiece"
)
