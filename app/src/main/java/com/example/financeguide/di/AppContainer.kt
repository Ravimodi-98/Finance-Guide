package com.example.financeguide.di

import android.content.Context
import com.example.financeguide.data.local.FinanceDatabase
import com.example.financeguide.data.repository.FinanceRepository
import com.example.financeguide.domain.usecase.GenerateSuggestionsUseCase
import com.example.financeguide.domain.usecase.GetBudgetStatusUseCase
import com.example.financeguide.domain.usecase.GetMonthlySummaryUseCase
import com.example.financeguide.ui.FinanceViewModelFactory

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.financeguide.data.remote.FinanceApiService
import com.example.financeguide.data.repository.AuthRepository
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

    // --- Remote Network Container ---

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    private val apiRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://127.0.0.1:3000/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val financeApiService: FinanceApiService by lazy {
        apiRetrofit.create(FinanceApiService::class.java)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepository(financeApiService, settingsManager)
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
}
