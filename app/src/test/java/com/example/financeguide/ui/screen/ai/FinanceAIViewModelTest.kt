package com.example.financeguide.ui.screen.ai

import android.content.Context
import android.content.SharedPreferences
import com.example.financeguide.data.local.SettingsManager
import com.example.financeguide.data.remote.model.ChatMessageDto
import com.example.financeguide.data.remote.model.ChatResponseDto
import com.example.financeguide.data.repository.FinanceRepository
import com.example.financeguide.domain.repository.FinanceAIRepository
import com.example.financeguide.domain.usecase.AskFinanceQuestionUseCase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

@OptIn(ExperimentalCoroutinesApi::class)
class FinanceAIViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeFinanceAIRepository
    private lateinit var mockLocalRepository: FinanceRepository
    private lateinit var askUseCase: AskFinanceQuestionUseCase
    private lateinit var settingsManager: SettingsManager

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        
        // Mock Context & SharedPreferences for SettingsManager instantiation
        val mockContext = Mockito.mock(Context::class.java)
        val mockPrefs = Mockito.mock(SharedPreferences::class.java)
        Mockito.`when`(mockContext.getSharedPreferences("finance_guide_settings", Context.MODE_PRIVATE))
            .thenReturn(mockPrefs)
        
        settingsManager = SettingsManager(mockContext)
        fakeRepository = FakeFinanceAIRepository()
        mockLocalRepository = Mockito.mock(FinanceRepository::class.java)
        askUseCase = AskFinanceQuestionUseCase(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initially_hasNoMessagesAndNotLoading() = runTest {
        val viewModel = FinanceAIViewModel(askUseCase, mockLocalRepository, settingsManager)
        val state = viewModel.uiState.value
        assertTrue(state.messages.isEmpty())
        assertFalse(state.isLoading)
        assertTrue(state.error == null)
    }

    @Test
    fun sendMessage_updatesHistoryAndGeneratesReply() = runTest {
        fakeRepository.nextResult = Result.success(ChatResponseDto(response = "Save 10% of Beli coins daily!"))
        val viewModel = FinanceAIViewModel(askUseCase, mockLocalRepository, settingsManager)
        
        viewModel.sendMessage("How to save money?")
        
        // Before execution finishes, it should show loading and the user's message
        var state = viewModel.uiState.value
        assertEquals(1, state.messages.size)
        assertTrue(state.messages[0].isFromUser)
        assertEquals("How to save money?", state.messages[0].text)
        assertTrue(state.isLoading)
        
        // Execute coroutine tasks
        testDispatcher.scheduler.advanceUntilIdle()
        
        state = viewModel.uiState.value
        assertEquals(2, state.messages.size)
        assertTrue(state.messages[0].isFromUser)
        assertFalse(state.messages[1].isFromUser)
        assertEquals("Save 10% of Beli coins daily!", state.messages[1].text)
        assertFalse(state.isLoading)
    }

    @Test
    fun sendMessage_onFailure_updatesErrorState() = runTest {
        fakeRepository.nextResult = Result.failure(Exception("Rough seas ahead! No connection."))
        val viewModel = FinanceAIViewModel(askUseCase, mockLocalRepository, settingsManager)
        
        viewModel.sendMessage("How to invest?")
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertEquals(1, state.messages.size) // Only user's message is there
        assertEquals("Rough seas ahead! No connection.", state.error)
        assertFalse(state.isLoading)
    }
}

private class FakeFinanceAIRepository : FinanceAIRepository {
    var nextResult: Result<ChatResponseDto> = Result.success(ChatResponseDto(response = "Mock reply"))
    var resetCalled = false

    override suspend fun askQuestion(message: String, history: List<ChatMessageDto>): Result<ChatResponseDto> {
        return nextResult
    }

    override suspend fun resetChat(): Result<Unit> {
        resetCalled = true
        return Result.success(Unit)
    }
}
