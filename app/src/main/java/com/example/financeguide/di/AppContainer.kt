package com.example.financeguide.di

import android.content.Context
import com.example.financeguide.data.local.FinanceDatabase
import com.example.financeguide.data.repository.FinanceRepository
import com.example.financeguide.domain.usecase.GenerateSuggestionsUseCase
import com.example.financeguide.domain.usecase.GetBudgetStatusUseCase
import com.example.financeguide.domain.usecase.GetMonthlySummaryUseCase
import com.example.financeguide.ui.FinanceViewModelFactory

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.financeguide.data.remote.FinanceApiService
import com.example.financeguide.data.repository.AuthRepository
import com.example.financeguide.data.repository.FinanceAIRepositoryImpl
import com.example.financeguide.data.repository.MarketsRepository
import com.example.financeguide.domain.repository.FinanceAIRepository
import com.example.financeguide.domain.usecase.AskFinanceQuestionUseCase
import com.example.financeguide.ui.screen.ai.FinanceAIViewModelFactory
import com.example.financeguide.ui.screen.markets.MarketsViewModelFactory
import java.io.IOException
import java.util.concurrent.TimeUnit


/**
 * Manual Dependency Injection container for the MVP.
 */
class AppContainer(private val context: Context) {

    private val database: FinanceDatabase by lazy {
        FinanceDatabase.getInstance(context)
    }

    private val financeRepository: FinanceRepository by lazy {
        FinanceRepository(
            transactionDao = database.transactionDao(),
            budgetDao = database.budgetDao()
        )
    }

    private val getMonthlySummaryUseCase: GetMonthlySummaryUseCase by lazy {
        GetMonthlySummaryUseCase(financeRepository)
    }

    private val getBudgetStatusUseCase: GetBudgetStatusUseCase by lazy {
        GetBudgetStatusUseCase(financeRepository)
    }

    private val generateSuggestionsUseCase: GenerateSuggestionsUseCase by lazy {
        GenerateSuggestionsUseCase()
    }

    val settingsManager: com.example.financeguide.data.local.SettingsManager by lazy {
        com.example.financeguide.data.local.SettingsManager(context)
    }

    // --- Gemini AI Remote Network Container ---

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request()
                try {
                    chain.proceed(request)
                } catch (e: IOException) {
                    val host = request.url.host
                    if (host == "127.0.0.1" || host == "localhost") {
                        val fallbackUrl = request.url.newBuilder()
                            .host("192.168.1.5")
                            .build()
                        val fallbackRequest = request.newBuilder()
                            .url(fallbackUrl)
                            .build()
                        chain.proceed(fallbackRequest)
                    } else {
                        throw e
                    }
                }
            }
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    private val aiRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            // Set to http://127.0.0.1:3000/ for physical phone USB debugging (via adb reverse tcp:3000 tcp:3000)
            .baseUrl("http://127.0.0.1:3000/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val financeApiService: FinanceApiService by lazy {
        aiRetrofit.create(FinanceApiService::class.java)
    }

    val financeAIRepository: FinanceAIRepository by lazy {
        FinanceAIRepositoryImpl(financeApiService)
    }

    val askFinanceQuestionUseCase: AskFinanceQuestionUseCase by lazy {
        AskFinanceQuestionUseCase(financeAIRepository)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepository(financeApiService, settingsManager)
    }

    val marketsRepository: MarketsRepository by lazy {
        MarketsRepository(financeApiService)
    }

    // --- Factories ---

    val financeViewModelFactory: FinanceViewModelFactory by lazy {
        FinanceViewModelFactory(
            repository = financeRepository,
            getMonthlySummaryUseCase = getMonthlySummaryUseCase,
            getBudgetStatusUseCase = getBudgetStatusUseCase,
            generateSuggestionsUseCase = generateSuggestionsUseCase,
            settingsManager = settingsManager
        )
    }

    val financeAIViewModelFactory: FinanceAIViewModelFactory by lazy {
        FinanceAIViewModelFactory(
            askFinanceQuestionUseCase = askFinanceQuestionUseCase,
            localRepository = financeRepository,
            settingsManager = settingsManager
        )
    }

    val marketsViewModelFactory: MarketsViewModelFactory by lazy {
        MarketsViewModelFactory(
            repository = marketsRepository
        )
    }
}
