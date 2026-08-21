package com.example.financeguide.data.repository

import com.example.financeguide.data.remote.GeminiApiService
import com.example.financeguide.data.remote.model.GeminiContent
import com.example.financeguide.data.remote.model.GeminiGenerationConfig
import com.example.financeguide.data.remote.model.GeminiPart
import com.example.financeguide.data.remote.model.GeminiRequest
import com.example.financeguide.domain.model.ChatMessage

class ChatbotRepository(
    private val geminiApiService: GeminiApiService,
    private val apiKey: String
) {

    suspend fun getChatbotResponse(
        conversationHistory: List<ChatMessage>,
        themeMode: String
    ): Result<String> {
        return try {
            val systemPrompt = when (themeMode) {
                "spiderman" -> """
                    You are Spider-Man, the friendly neighborhood financial guide and advisor!
                    Goal: Answer user financial and money questions clearly, simply, and practically.
                    Guidelines:
                    - Keep replies simple, friendly, encouraging, and very easy to understand.
                    - Break down complex financial concepts (e.g. compound interest, stocks, mutual funds, budgeting, debt) into simple everyday language.
                    - Use short bullet points and clear paragraphs.
                    - Occasionally add light, fun Spider-Man spirit (e.g., "With great power comes great budgeting!").
                """.trimIndent()
                "basic" -> """
                    You are FinBot, an intelligent and friendly AI financial advisor.
                    Goal: Explain financial topics, investments, budgeting, savings, and expense management in simple and clear terms.
                    Guidelines:
                    - Deliver concise, highly readable, and crystal-clear answers.
                    - Structure replies with clean bullet points and short summaries.
                    - Avoid heavy jargon without explaining it simply.
                    - Focus on actionable, practical financial wisdom for everyday life.
                """.trimIndent()
                else -> """
                    You are Nami, the master navigator and treasure manager from One Piece!
                    Goal: Guide the user to financial freedom with crystal-clear, smart, and simple financial advice.
                    Guidelines:
                    - Keep your replies simple, clear, practical, and very easy for beginners to understand.
                    - Explain budgeting, saving Beli/money, investing, and interest rates simply.
                    - Use concise bullet points and well-structured, readable responses.
                    - Infuse a touch of confident, friendly navigator personality.
                """.trimIndent()
            }

            val systemInstruction = GeminiContent(
                role = "user",
                parts = listOf(GeminiPart(text = systemPrompt))
            )

            // Convert conversation history to Gemini contents
            val contents = mutableListOf<GeminiContent>()
            conversationHistory.takeLast(10).forEach { message ->
                contents.add(
                    GeminiContent(
                        role = if (message.isUser) "user" else "model",
                        parts = listOf(GeminiPart(text = message.text))
                    )
                )
            }

            val request = GeminiRequest(
                contents = contents,
                systemInstruction = systemInstruction,
                generationConfig = GeminiGenerationConfig(
                    temperature = 0.7f,
                    maxOutputTokens = 1200
                )
            )

            val response = geminiApiService.generateContent(apiKey = apiKey, request = request)

            if (response.isSuccessful) {
                val candidateText = response.body()
                    ?.candidates
                    ?.firstOrNull()
                    ?.content
                    ?.parts
                    ?.firstOrNull()
                    ?.text

                if (!candidateText.isNullOrBlank()) {
                    Result.success(candidateText.trim())
                } else {
                    val errorMessage = response.body()?.error?.message ?: "Received an empty response from AI."
                    Result.failure(Exception(errorMessage))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: response.message()
                Result.failure(Exception("AI request failed (${response.code()}): $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
