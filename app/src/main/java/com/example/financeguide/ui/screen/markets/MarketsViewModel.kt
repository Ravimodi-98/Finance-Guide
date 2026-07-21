package com.example.financeguide.ui.screen.markets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.financeguide.data.remote.model.FinnhubNewsArticleDto
import com.example.financeguide.data.remote.model.CurrencyRateItem
import com.example.financeguide.data.repository.MarketsRepository
import com.example.financeguide.data.repository.MarketsResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MarketsUiState(
    val isLoadingNews: Boolean = true,
    val isLoadingExchange: Boolean = true,
    val newsError: String? = null,
    val exchangeError: String? = null,
    val allNews: List<FinnhubNewsArticleDto> = emptyList(),
    val filteredNews: List<FinnhubNewsArticleDto> = emptyList(),
    val selectedCategory: String = "All",
    val searchQuery: String = "",
    val exchangeRates: Map<String, Double> = emptyMap(),
    val topCurrencies: List<CurrencyRateItem> = emptyList(),
    val lastUpdatedText: String = "",
    // Converter state
    val convertAmount: String = "100",
    val fromCurrency: String = "USD",
    val toCurrency: String = "EUR",
    val convertedResult: Double = 92.0
)

class MarketsViewModel(
    private val repository: MarketsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MarketsUiState())
    val uiState: StateFlow<MarketsUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        fetchNews()
        fetchExchangeRates()
    }

    fun fetchNews() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingNews = true, newsError = null)
            when (val result = repository.getNews()) {
                is MarketsResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoadingNews = false,
                        allNews = result.data,
                        filteredNews = filterArticles(result.data, _uiState.value.selectedCategory, _uiState.value.searchQuery)
                    )
                }
                is MarketsResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoadingNews = false,
                        newsError = result.message
                    )
                }
            }
        }
    }

    fun fetchExchangeRates() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingExchange = true, exchangeError = null)
            when (val result = repository.getExchangeRates()) {
                is MarketsResult.Success -> {
                    val ratesMap = result.data.rates ?: emptyMap()
                    val topList = buildTopCurrencies(ratesMap)
                    val unixTime = result.data.timeLastUpdateUnix ?: (System.currentTimeMillis() / 1000)
                    val updatedStr = java.text.SimpleDateFormat("hh:mm a, dd MMM yyyy", java.util.Locale.US)
                        .format(java.util.Date(unixTime * 1000))

                    _uiState.value = _uiState.value.copy(
                        isLoadingExchange = false,
                        exchangeRates = ratesMap,
                        topCurrencies = topList,
                        lastUpdatedText = updatedStr
                    )
                    calculateConversion()
                }
                is MarketsResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoadingExchange = false,
                        exchangeError = result.message
                    )
                }
            }
        }
    }

    fun selectCategory(category: String) {
        _uiState.value = _uiState.value.copy(
            selectedCategory = category,
            filteredNews = filterArticles(_uiState.value.allNews, category, _uiState.value.searchQuery)
        )
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            filteredNews = filterArticles(_uiState.value.allNews, _uiState.value.selectedCategory, query)
        )
    }

    fun updateConvertAmount(amount: String) {
        _uiState.value = _uiState.value.copy(convertAmount = amount)
        calculateConversion()
    }

    fun setFromCurrency(code: String) {
        _uiState.value = _uiState.value.copy(fromCurrency = code)
        calculateConversion()
    }

    fun setToCurrency(code: String) {
        _uiState.value = _uiState.value.copy(toCurrency = code)
        calculateConversion()
    }

    fun swapCurrencies() {
        val oldFrom = _uiState.value.fromCurrency
        val oldTo = _uiState.value.toCurrency
        _uiState.value = _uiState.value.copy(fromCurrency = oldTo, toCurrency = oldFrom)
        calculateConversion()
    }

    private fun calculateConversion() {
        val amount = _uiState.value.convertAmount.toDoubleOrNull() ?: 0.0
        val rates = _uiState.value.exchangeRates
        val fromRate = rates[_uiState.value.fromCurrency] ?: 1.0
        val toRate = rates[_uiState.value.toCurrency] ?: 1.0

        // Convert base USD to target currency
        val inUsd = if (fromRate != 0.0) amount / fromRate else amount
        val converted = inUsd * toRate

        _uiState.value = _uiState.value.copy(convertedResult = converted)
    }

    private fun filterArticles(
        articles: List<FinnhubNewsArticleDto>,
        category: String,
        query: String
    ): List<FinnhubNewsArticleDto> {
        return articles.filter { article ->
            val matchesCategory = when (category) {
                "All" -> true
                "Markets" -> article.category?.contains("business", ignoreCase = true) == true || article.headline.contains("market", ignoreCase = true)
                "Forex" -> article.headline.contains("forex", ignoreCase = true) || article.headline.contains("currency", ignoreCase = true) || article.summary?.contains("dollar", ignoreCase = true) == true
                "Crypto" -> article.headline.contains("crypto", ignoreCase = true) || article.headline.contains("bitcoin", ignoreCase = true)
                "Economy" -> article.headline.contains("economy", ignoreCase = true) || article.headline.contains("fed", ignoreCase = true) || article.headline.contains("inflation", ignoreCase = true)
                "Technology" -> article.headline.contains("tech", ignoreCase = true) || article.headline.contains("ai", ignoreCase = true)
                else -> true
            }

            val matchesQuery = if (query.isBlank()) true else {
                article.headline.contains(query, ignoreCase = true) ||
                (article.summary?.contains(query, ignoreCase = true) == true) ||
                (article.source?.contains(query, ignoreCase = true) == true)
            }

            matchesCategory && matchesQuery
        }
    }

    private fun buildTopCurrencies(rates: Map<String, Double>): List<CurrencyRateItem> {
        val currencyDefs = listOf(
            Triple("USD", "US Dollar", "🇺🇸"),
            Triple("EUR", "Euro", "🇪🇺"),
            Triple("GBP", "British Pound", "🇬🇧"),
            Triple("INR", "Indian Rupee", "🇮🇳"),
            Triple("JPY", "Japanese Yen", "🇯🇵"),
            Triple("CAD", "Canadian Dollar", "🇨🇦"),
            Triple("AUD", "Australian Dollar", "🇦🇺"),
            Triple("CHF", "Swiss Franc", "🇨🇭"),
            Triple("CNY", "Chinese Yuan", "🇨🇳"),
            Triple("SGD", "Singapore Dollar", "🇸🇬"),
            Triple("AED", "UAE Dirham", "🇦🇪")
        )

        return currencyDefs.map { (code, name, flag) ->
            val rate = rates[code] ?: 1.0
            // Compare relative to baseline
            val isAppreciating = when (code) {
                "USD" -> true
                "EUR" -> rate < 0.95
                "GBP" -> rate < 0.80
                "INR" -> rate < 84.0
                "JPY" -> rate < 155.0
                else -> true
            }
            CurrencyRateItem(code, name, flag, rate, isAppreciating)
        }
    }
}

class MarketsViewModelFactory(
    private val repository: MarketsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MarketsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MarketsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
