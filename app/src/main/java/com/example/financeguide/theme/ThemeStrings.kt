package com.example.financeguide.theme

import androidx.compose.runtime.compositionLocalOf

data class ThemeStrings(
    // App wide
    val appName: String,
    val signIn: String,
    val signInSubtitle: String,
    val emailLabel: String,
    val passwordLabel: String,
    val signInButton: String,
    val offlineButton: String,
    val registerPrompt: String,
    val registerButton: String,

    // Onboarding
    val onboardingTitle: String,
    val setSailButton: String,

    // Dashboard
    val dashboardTitle: String,
    val transactionListTitle: String,
    val totalBalance: String,
    val incomeLabel: String,
    val expenseLabel: String,
    val tipTitle: String,

    // Transactions
    val addTransactionTitle: String,
    val gainLabel: String,
    val lossLabel: String,
    val saveButton: String,
    val currencySettingLabel: String,

    // Budget
    val budgetScreenTitle: String,
    val addBudgetTitle: String,

    // Settings
    val currencyLabel: String,

    // Chatbot
    val chatbotName: String,
    val chatbotTitle: String,
    val chatbotStatusOnline: String,
    val chatbotWelcomeTitle: String,
    val chatbotWelcomeBody: String,
    val chatbotWelcomeQuote: String,
    val chatbotSuggest1: String,
    val chatbotSuggest2: String,
    val copiedToast: String,
)

val OnePieceStrings = ThemeStrings(
    appName = "Finance Guide",
    signIn = "Pirate Crew Sign In",
    signInSubtitle = "Enter your credentials to manage your Beli and check bounty lists.",
    emailLabel = "Crew Email",
    passwordLabel = "Crew Password",
    signInButton = "SET SAIL! ⚔️",
    offlineButton = "EMBARK OFFLINE ⚓",
    registerPrompt = "New to the crew?",
    registerButton = "Join and Register!",
    onboardingTitle = "Set Sail with FinanceGuide!",
    setSailButton = "SET SAIL! ⛵",
    dashboardTitle = "Bounty Board",
    transactionListTitle = "Logbook of Bounties",
    totalBalance = "Total Balance",
    incomeLabel = "Income",
    expenseLabel = "Expense",
    tipTitle = "Nami's Navigation Tip",
    addTransactionTitle = "Log New Bounty Entry",
    gainLabel = "Bounty Gain",
    lossLabel = "Bounty Loss",
    saveButton = "Log to Pirate Logbook",
    currencySettingLabel = "Bounty Currency",
    budgetScreenTitle = "Bounty Limits (Budgets)",
    addBudgetTitle = "Add Bounty Limit",
    currencyLabel = "Bounty Currency",
    chatbotName = "Nami",
    chatbotTitle = "Nami (Navigator)",
    chatbotStatusOnline = "online",
    chatbotWelcomeTitle = "Ahoy, Sailor!",
    chatbotWelcomeBody = "I am Nami, your Finance Navigator. Let's make sure our ship's ledger is in order! Ask me questions about saving money, budget limits, emergency funds, or expense logs.",
    chatbotWelcomeQuote = "🗺️ \"Every Pirate King starts by protecting their treasure!\"",
    chatbotSuggest1 = "I spent 500 Beli on food",
    chatbotSuggest2 = "I earned 10000 Beli from a treasure hunt",
    copiedToast = "Logbook copied to clipboard!",
)

val SpidermanStrings = ThemeStrings(
    appName = "Finance Guide",
    signIn = "S.H.I.E.L.D. Agent Sign In",
    signInSubtitle = "Enter your credentials to manage your Credits and track mission costs.",
    emailLabel = "Agent Email",
    passwordLabel = "Agent Password",
    signInButton = "SUIT UP! 🕷️",
    offlineButton = "GO OFFLINE 🕸️",
    registerPrompt = "New agent?",
    registerButton = "Join and Enlist!",
    onboardingTitle = "Suit Up with FinanceGuide!",
    setSailButton = "SUIT UP! 🕷️",
    dashboardTitle = "Crime Ledger",
    transactionListTitle = "Case Files",
    totalBalance = "Total Credits",
    incomeLabel = "Web Earnings",
    expenseLabel = "Mission Cost",
    tipTitle = "Spider-Sense Alert",
    addTransactionTitle = "File New Case",
    gainLabel = "Web Earnings",
    lossLabel = "Mission Cost",
    saveButton = "Log to Case Files",
    currencySettingLabel = "Shield Currency",
    budgetScreenTitle = "Mission Budgets",
    addBudgetTitle = "Add Mission Budget",
    currencyLabel = "Shield Currency",
    chatbotName = "MJ",
    chatbotTitle = "MJ (Web Guide)",
    chatbotStatusOnline = "available",
    chatbotWelcomeTitle = "Hey there, Hero!",
    chatbotWelcomeBody = "I'm MJ, your Financial Web Guide. Let's keep your budget in check — even heroes need to manage their Credits! Ask me about saving, mission budgets, emergency funds, or expense tracking.",
    chatbotWelcomeQuote = "🕸️ \"With great spending comes great responsibility!\"",
    chatbotSuggest1 = "I spent 500 Credits on mission gear",
    chatbotSuggest2 = "I earned 10000 Credits from a S.H.I.E.L.D. mission",
    copiedToast = "Case File copied to clipboard!",
)

val LocalThemeStrings = compositionLocalOf { OnePieceStrings }
