package com.example.financeguide.di

import android.content.Context
import com.example.financeguide.data.local.AuthDataStore
import com.example.financeguide.data.local.FinanceDatabase
import com.example.financeguide.data.local.SettingsManager
import com.example.financeguide.data.remote.CollaborativeApiService
import com.example.financeguide.data.remote.FinanceApiService
import com.example.financeguide.data.remote.SocketManager
import com.example.financeguide.data.repository.AuthRepository
import com.example.financeguide.data.repository.CollaborativeRepository
import com.example.financeguide.data.repository.FinanceAIRepositoryImpl
import com.example.financeguide.data.repository.FinanceRepository
import com.example.financeguide.domain.repository.FinanceAIRepository
import com.example.financeguide.domain.usecase.AskFinanceQuestionUseCase
import com.example.financeguide.domain.usecase.GenerateSuggestionsUseCase
import com.example.financeguide.domain.usecase.GetBudgetStatusUseCase
import com.example.financeguide.domain.usecase.GetMonthlySummaryUseCase
import com.example.financeguide.ui.FinanceViewModelFactory
import com.example.financeguide.ui.screen.ai.FinanceAIViewModelFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Manual Dependency Injection container.
 */
class AppContainer(private val context: Context) {

    val database: FinanceDatabase by lazy {
        FinanceDatabase.getInstance(context)
    }

    val authDataStore: AuthDataStore by lazy {
        AuthDataStore(context)
    }

    val financeRepository: FinanceRepository by lazy {
        FinanceRepository(
            transactionDao = database.transactionDao(),
            budgetDao = database.budgetDao()
        )
    }

    val getMonthlySummaryUseCase: GetMonthlySummaryUseCase by lazy {
        GetMonthlySummaryUseCase(financeRepository)
    }

    val getBudgetStatusUseCase: GetBudgetStatusUseCase by lazy {
        GetBudgetStatusUseCase(financeRepository)
    }

    val generateSuggestionsUseCase: GenerateSuggestionsUseCase by lazy {
        GenerateSuggestionsUseCase()
    }

    val settingsManager: SettingsManager by lazy {
        SettingsManager(context)
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val financeApiService: FinanceApiService by lazy {
        retrofit.create(FinanceApiService::class.java)
    }

    val collaborativeApiService: CollaborativeApiService by lazy {
        retrofit.create(CollaborativeApiService::class.java)
    }

    val financeAIRepository: FinanceAIRepository by lazy {
        FinanceAIRepositoryImpl(financeApiService)
    }

    val askFinanceQuestionUseCase: AskFinanceQuestionUseCase by lazy {
        AskFinanceQuestionUseCase(financeAIRepository)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepository(collaborativeApiService, authDataStore)
    }

    val collaborativeRepository: CollaborativeRepository by lazy {
        CollaborativeRepository(
            apiService = collaborativeApiService,
            collaborativeDao = database.collaborativeDao(),
            pendingSyncDao = database.pendingSyncDao()
        )
    }

    val socketManager: SocketManager by lazy {
        SocketManager()
    }

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
}
