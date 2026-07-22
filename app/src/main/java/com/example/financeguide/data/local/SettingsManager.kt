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
}
