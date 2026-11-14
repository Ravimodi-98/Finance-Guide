package com.example.financeguide.data.repository

import com.example.financeguide.data.local.SettingsManager
import com.example.financeguide.data.remote.GeminiApiService
import com.example.financeguide.data.remote.model.GeminiContent
import com.example.financeguide.data.remote.model.GeminiGenerationConfig
import com.example.financeguide.data.remote.model.GeminiPart
import com.example.financeguide.data.remote.model.GeminiRequest
import com.example.financeguide.domain.model.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

class ChatbotRepository(
    private val geminiApiService: GeminiApiService,
    private val okHttpClient: OkHttpClient,
    private val settingsManager: SettingsManager
) {

    suspend fun getChatbotResponse(
        conversationHistory: List<ChatMessage>,
        themeMode: String
    ): Result<String> {
        val apiKey = settingsManager.geminiApiKey.value.trim()
        if (apiKey.isBlank()) {
            return Result.failure(
                Exception("API Key Required: Please paste your API key to activate the Finance AI.")
            )
        }

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

        return if (apiKey.startsWith("nvapi-") || apiKey.startsWith("sk-")) {
            callOpenAiCompatible(apiKey, conversationHistory, systemPrompt)
        } else {
            callGemini(apiKey, conversationHistory, systemPrompt)
        }
    }

    private suspend fun callOpenAiCompatible(
        apiKey: String,
        conversationHistory: List<ChatMessage>,
        systemPrompt: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val rawModel = settingsManager.geminiModel.value.trim()
            val model = if (rawModel.isBlank() || rawModel.startsWith("gemini")) {
                "minimaxai/minimax-m3.1-flash"
            } else {
                rawModel
            }

            val messagesArray = JSONArray()
            messagesArray.put(JSONObject().apply {
                put("role", "system")
                put("content", systemPrompt)
            })
            conversationHistory.takeLast(10).forEach { message ->
                messagesArray.put(JSONObject().apply {
                    put("role", if (message.isUser) "user" else "assistant")
                    put("content", message.text)
                })
            }

            val requestJson = JSONObject().apply {
                put("model", model)
                put("messages", messagesArray)
                put("temperature", 0.7)
                put("max_tokens", 1024)
            }

            val request = Request.Builder()
                .url("https://integrate.api.nvidia.com/v1/chat/completions")
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(requestJson.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string() ?: ""
                val json = JSONObject(responseBody)
                val candidate = json.optJSONArray("choices")
                    ?.optJSONObject(0)
                    ?.optJSONObject("message")
                    ?.optString("content")

                if (!candidate.isNullOrBlank()) {
                    Result.success(candidate.trim())
                } else {
                    Result.failure(Exception("Received empty response from AI."))
                }
            } else {
                val rawError = response.body?.string() ?: response.message
                val parsedMessage = try {
                    val json = JSONObject(rawError)
                    json.optJSONObject("error")?.optString("message") ?: rawError
                } catch (e: Exception) {
                    rawError
                }
                Result.failure(Exception(parsedMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun callGemini(
        apiKey: String,
        conversationHistory: List<ChatMessage>,
        systemPrompt: String
    ): Result<String> {
        val rawModel = settingsManager.geminiModel.value.trim()
        val model = if (rawModel.isBlank() || rawModel == "gemini-2.5-flash" || rawModel == "gemini-3.5-flash-lite") {
            "gemini-3.8-flash"
        } else {
            rawModel
        }

        return try {
            val systemInstruction = GeminiContent(
                role = "user",
                parts = listOf(GeminiPart(text = systemPrompt))
            )

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

            val response = geminiApiService.generateContent(
                model = model,
                apiKey = apiKey,
                request = request
            )

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
                val rawError = response.errorBody()?.string() ?: response.message()
                val parsedMessage = try {
                    val json = JSONObject(rawError)
                    val errObj = json.optJSONObject("error")
                    errObj?.optString("message") ?: rawError
                } catch (e: Exception) {
                    rawError
                }
                Result.failure(Exception(parsedMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
