package com.example.financeguide.theme

import androidx.compose.runtime.compositionLocalOf

data class ThemeStrings(
    val appName: String,
    val signIn: String,
    val signInSubtitle: String,
    val emailLabel: String,
    val passwordLabel: String,
    val signInButton: String,
    val offlineButton: String,
    val registerPrompt: String,
    val registerButton: String,
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

    // Character Persona
    val chatbotName: String,
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
)

val SpidermanStrings = ThemeStrings(
    appName = "Finance Guide",
    signIn = "Web Slinger Sign In",
    signInSubtitle = "Enter your credentials to manage your Credits and track suit & mission costs.",
    emailLabel = "Hero Email",
    passwordLabel = "Secret Password",
    signInButton = "SUIT UP! 🕷️",
    offlineButton = "GO OFFLINE 🕸️",
    registerPrompt = "New to the Neighborhood?",
    registerButton = "Suit Up & Register!",
    onboardingTitle = "Suit Up with FinanceGuide!",
    setSailButton = "SUIT UP! 🕷️",
    dashboardTitle = "Web Ledger",
    transactionListTitle = "Case Files",
    totalBalance = "Total Credits",
    incomeLabel = "Web Earnings",
    expenseLabel = "Mission Cost",
    tipTitle = "Spider-Sense Alert 🕷️",
    addTransactionTitle = "File New Case",
    gainLabel = "Web Earnings",
    lossLabel = "Mission Cost",
    saveButton = "Log to Case Files",
    currencySettingLabel = "Web Currency",
    budgetScreenTitle = "Mission Budgets",
    addBudgetTitle = "Add Mission Budget",
    currencyLabel = "Web Currency",
    chatbotName = "Spider-Man",
)

val LocalThemeStrings = compositionLocalOf { OnePieceStrings }

val FinanceProStrings = ThemeStrings(
    appName = "Finance Guide",
    signIn = "Sign In to Finance Pro",
    signInSubtitle = "Enter your credentials to manage your money and track your financial goals.",
    emailLabel = "Email Address",
    passwordLabel = "Password",
    signInButton = "SIGN IN 🚀",
    offlineButton = "CONTINUE OFFLINE ⚡",
    registerPrompt = "Don't have an account?",
    registerButton = "Create Account",
    onboardingTitle = "Welcome to Finance Pro!",
    setSailButton = "GET STARTED 🚀",
    dashboardTitle = "Dashboard",
    transactionListTitle = "Recent Transactions",
    totalBalance = "Net Balance",
    incomeLabel = "Income",
    expenseLabel = "Expense",
    tipTitle = "💡 FinBot Insight",
    addTransactionTitle = "Log Transaction",
    gainLabel = "Income",
    lossLabel = "Expense",
    saveButton = "Save Transaction",
    currencySettingLabel = "Currency",
    budgetScreenTitle = "My Budgets",
    addBudgetTitle = "Add Budget",
    currencyLabel = "Currency",
    chatbotName = "FinBot",
)
