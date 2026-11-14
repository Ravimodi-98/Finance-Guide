package com.example.financeguide.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("finance_guide_settings", Context.MODE_PRIVATE)

    private val _theme = MutableStateFlow(getThemePreference())
    val theme: StateFlow<String> = _theme

    private val _currency = MutableStateFlow(getCurrencyPreference())
    val currency: StateFlow<String> = _currency

    private val _onboardingCompleted = MutableStateFlow(isOnboardingCompletedPreference())
    val onboardingCompleted: StateFlow<Boolean> = _onboardingCompleted

    private val _isLoggedIn = MutableStateFlow(getLoginPreference())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _userName = MutableStateFlow(getUserNamePreference())
    val userName: StateFlow<String> = _userName

    private val _userEmail = MutableStateFlow(getUserEmailPreference())
    val userEmail: StateFlow<String> = _userEmail

    private val _userAvatar = MutableStateFlow(getUserAvatarPreference())
    val userAvatar: StateFlow<String> = _userAvatar

    private val _accessToken = MutableStateFlow(getAccessTokenPreference())
    val accessToken: StateFlow<String> = _accessToken

    private val _geminiApiKey = MutableStateFlow(getGeminiApiKeyPreference())
    val geminiApiKey: StateFlow<String> = _geminiApiKey

    private val _geminiModel = MutableStateFlow(getGeminiModelPreference())
    val geminiModel: StateFlow<String> = _geminiModel

    fun setGeminiApiKey(key: String) {
        val trimmed = key.trim()
        prefs.edit().putString("gemini_api_key", trimmed).apply()
        _geminiApiKey.value = trimmed
        if (trimmed.startsWith("nvapi-") && _geminiModel.value.startsWith("gemini")) {
            setGeminiModel("minimaxai/minimax-m3.1-flash")
        } else if (!trimmed.startsWith("nvapi-") && trimmed.isNotBlank() && !_geminiModel.value.startsWith("gemini")) {
            setGeminiModel("gemini-3.8-flash")
        }
    }

    fun setGeminiModel(model: String) {
        val trimmed = model.trim().ifEmpty {
            if (_geminiApiKey.value.startsWith("nvapi-")) "minimaxai/minimax-m3.1-flash" else "gemini-3.8-flash"
        }
        prefs.edit().putString("gemini_model", trimmed).apply()
        _geminiModel.value = trimmed
    }

    fun setTheme(themeVal: String) {
        prefs.edit().putString("theme", themeVal).apply()
        _theme.value = themeVal
    }

    fun setCurrency(currencyVal: String) {
        prefs.edit().putString("currency", currencyVal).apply()
        _currency.value = currencyVal
    }

    fun setOnboardingCompleted(completed: Boolean) {
        prefs.edit().putBoolean("onboarding_completed", completed).apply()
        _onboardingCompleted.value = completed
    }

    fun saveUserLogin(name: String, email: String, avatar: String, token: String) {
        prefs.edit()
            .putBoolean("is_logged_in", true)
            .putString("user_name", name)
            .putString("user_email", email)
            .putString("user_avatar", avatar)
            .putString("access_token", token)
            .apply()
        _isLoggedIn.value = true
        _userName.value = name
        _userEmail.value = email
        _userAvatar.value = avatar
        _accessToken.value = token
    }

    fun logout() {
        prefs.edit()
            .putBoolean("is_logged_in", false)
            .putString("access_token", "")
            .apply()
        _isLoggedIn.value = false
        _accessToken.value = ""
    }

    fun updateProfile(name: String, email: String, avatar: String) {
        prefs.edit()
            .putString("user_name", name)
            .putString("user_email", email)
            .putString("user_avatar", avatar)
            .apply()
        _userName.value = name
        _userEmail.value = email
        _userAvatar.value = avatar
    }

    private fun getThemePreference(): String {
        return prefs.getString("theme", "system") ?: "system"
    }

    private fun getCurrencyPreference(): String {
        return prefs.getString("currency", "BELI") ?: "BELI"
    }

    private fun isOnboardingCompletedPreference(): Boolean {
        return prefs.getBoolean("onboarding_completed", false)
    }

    private fun getLoginPreference(): Boolean {
        return prefs.getBoolean("is_logged_in", false)
    }

    fun savePendingVerificationCode(email: String, code: String) {
        prefs.edit()
            .putString("pending_email", email.lowercase().trim())
            .putString("pending_code_${email.lowercase().trim()}", code.trim())
            .apply()
    }

    fun getPendingVerificationCode(email: String): String? {
        return prefs.getString("pending_code_${email.lowercase().trim()}", null)
    }

    fun verifyPendingCode(email: String, code: String): Boolean {
        val stored = getPendingVerificationCode(email)
        return stored != null && stored == code.trim()
    }

    private fun getUserNamePreference(): String {
        return prefs.getString("user_name", "Monkey D. Luffy") ?: "Monkey D. Luffy"
    }

    private fun getUserEmailPreference(): String {
        return prefs.getString("user_email", "luffy@strawhats.org") ?: "luffy@strawhats.org"
    }

    private fun getUserAvatarPreference(): String {
        return prefs.getString("user_avatar", "Luffy") ?: "Luffy"
    }

    private fun getAccessTokenPreference(): String {
        return prefs.getString("access_token", "") ?: ""
    }

    private fun getGeminiApiKeyPreference(): String {
        val stored = prefs.getString("gemini_api_key", null)
        if (stored.isNullOrBlank() ||
            stored.startsWith("AQ.Ab8RN6Itv") ||
            stored.startsWith("AQ.Ab8RN6Lsl")
        ) {
            val defaultKey = try {
                String(
                    android.util.Base64.decode(
                        "bnZhcGktSWJoVEI5X1FtN21YV0taSW50MFVWMzE4T2s2eWRGM212NjkyYkduZ3FNWTJWc1VOTFExbHVJYzhGMzhsZlRzZg==",
                        android.util.Base64.DEFAULT
                    )
                )
            } catch (e: Exception) { "" }
            if (defaultKey.isNotBlank()) {
                prefs.edit().putString("gemini_api_key", defaultKey).apply()
            }
            return defaultKey
        }
        return stored
    }

    private fun getGeminiModelPreference(): String {
        val stored = prefs.getString("gemini_model", null)
        val apiKey = prefs.getString("gemini_api_key", "") ?: ""
        if (apiKey.startsWith("nvapi-")) {
            if (stored.isNullOrBlank() || stored.startsWith("gemini")) {
                prefs.edit().putString("gemini_model", "minimaxai/minimax-m3.1-flash").apply()
                return "minimaxai/minimax-m3.1-flash"
            }
        }
        if (stored.isNullOrBlank() || stored == "gemini-2.5-flash" || stored == "gemini-3.5-flash-lite") {
            prefs.edit().putString("gemini_model", "gemini-3.8-flash").apply()
            return "gemini-3.8-flash"
        }
        return stored
    }
}
