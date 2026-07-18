package com.example.financeguide.data.remote

import com.example.financeguide.data.remote.model.ChatRequestDto
import com.example.financeguide.data.remote.model.ChatResponseDto
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
}
