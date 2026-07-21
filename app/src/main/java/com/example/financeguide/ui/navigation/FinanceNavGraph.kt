package com.example.financeguide.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financeguide.domain.model.TransactionType
import com.example.financeguide.theme.AppTheme
import com.example.financeguide.ui.FinanceViewModel
import com.example.financeguide.ui.screen.budget.BudgetScreen
import com.example.financeguide.ui.screen.dashboard.DashboardScreen
import com.example.financeguide.ui.screen.insights.InsightsScreen
import com.example.financeguide.ui.screen.settings.SettingsScreen
import com.example.financeguide.ui.screen.transaction.AddTransactionScreen
import com.example.financeguide.ui.screen.ai.FinanceAIScreen
import com.example.financeguide.ui.screen.ai.FinanceAIViewModel
import com.example.financeguide.ui.screen.collaborative.*

@Composable
fun FinanceNavGraph(
    viewModel: FinanceViewModel,
    appContainer: com.example.financeguide.di.AppContainer
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: TopLevelDestination.DASHBOARD.route

    val currentThemeRaw by appContainer.settingsManager.theme.collectAsState()
    val currentTheme = AppTheme.fromString(currentThemeRaw)
    
    val currentDestination = TopLevelDestination.entries.find { it.route == currentRoute } 
        ?: TopLevelDestination.DASHBOARD

    Scaffold(
        bottomBar = {
            if (currentRoute != "finance_ai" && !currentRoute.startsWith("add_transaction") && !currentRoute.startsWith("auth")) {
                BottomNavBar(
                    currentDestination = currentDestination,
                    onNavigateToDestination = { destination ->
                        if (destination == TopLevelDestination.ADD_TRANSACTION) {
                            navController.navigate(destination.route) {
                                launchSingleTop = true
                            }
                            return@BottomNavBar
                        }
                        navController.navigate(destination.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = TopLevelDestination.DASHBOARD.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("auth") {
                AuthScreen(
                    authRepository = appContainer.authRepository,
                    currentTheme = currentTheme,
                    onAuthSuccess = { navController.navigate(TopLevelDestination.GROUPS.route) }
                )
            }
            composable(TopLevelDestination.DASHBOARD.route) {
                DashboardScreen(
                    viewModel = viewModel,
                    onAddIncome = { navController.navigate("add_transaction/INCOME") },
                    onAddExpense = { navController.navigate("add_transaction/EXPENSE") },
                    onNavigateToAI = { navController.navigate("finance_ai") }
                )
            }
            composable(TopLevelDestination.GROUPS.route) {
                GroupsScreen(
                    repository = appContainer.collaborativeRepository,
                    currentTheme = currentTheme,
                    onGroupClick = { groupId -> navController.navigate("group_detail/$groupId") },
                    onCreateGroupClick = {
                        navController.navigate("group_detail/new")
                    }
                )
            }
            composable(
                route = "group_detail/{groupId}",
                arguments = listOf(navArgument("groupId") { type = NavType.StringType })
            ) { entry ->
                val groupId = entry.arguments?.getString("groupId") ?: ""
                GroupDetailScreen(
                    groupId = groupId,
                    repository = appContainer.collaborativeRepository,
                    currentTheme = currentTheme,
                    onBackClick = { navController.navigateUp() },
                    onAddExpenseClick = { gId -> navController.navigate("add_collaborative_expense/$gId") },
                    onSettleClick = { payeeId, payeeName, amount ->
                        navController.navigate("settlement/$groupId/$payeeId/$payeeName/$amount")
                    }
                )
            }
            composable(
                route = "add_collaborative_expense/{groupId}",
                arguments = listOf(navArgument("groupId") { type = NavType.StringType })
            ) { entry ->
                val groupId = entry.arguments?.getString("groupId") ?: ""
                AddCollaborativeExpenseScreen(
                    groupId = groupId,
                    repository = appContainer.collaborativeRepository,
                    onBackClick = { navController.navigateUp() },
                    onExpenseAdded = { navController.navigateUp() }
                )
            }
            composable(
                route = "settlement/{groupId}/{payeeId}/{payeeName}/{amount}",
                arguments = listOf(
                    navArgument("groupId") { type = NavType.StringType },
                    navArgument("payeeId") { type = NavType.StringType },
                    navArgument("payeeName") { type = NavType.StringType },
                    navArgument("amount") { type = NavType.FloatType }
                )
            ) { entry ->
                val groupId = entry.arguments?.getString("groupId")
                val payeeId = entry.arguments?.getString("payeeId") ?: ""
                val payeeName = entry.arguments?.getString("payeeName") ?: ""
                val amount = entry.arguments?.getFloat("amount")?.toDouble() ?: 0.0

                SettlementScreen(
                    groupId = groupId,
                    payeeId = payeeId,
                    payeeName = payeeName,
                    initialAmount = amount,
                    repository = appContainer.collaborativeRepository,
                    onBackClick = { navController.navigateUp() },
                    onSettled = { navController.navigateUp() }
                )
            }
            composable(TopLevelDestination.BUDGETS.route) {
                BudgetScreen(viewModel = viewModel)
            }
            composable(TopLevelDestination.ADD_TRANSACTION.route) {
                AddTransactionScreen(
                    viewModel = viewModel,
                    onBack = { navController.navigateUp() }
                )
            }
            composable(
                route = "add_transaction/{type}",
                arguments = listOf(navArgument("type") { type = NavType.StringType })
            ) { backStackEntry ->
                val typeArg = backStackEntry.arguments?.getString("type")
                val initialType = when (typeArg) {
                    "INCOME" -> TransactionType.INCOME
                    else -> TransactionType.EXPENSE
                }
                AddTransactionScreen(
                    viewModel = viewModel,
                    onBack = { navController.navigateUp() },
                    initialType = initialType
                )
            }
            composable(TopLevelDestination.INSIGHTS.route) {
                InsightsScreen(viewModel = viewModel)
            }
            composable(TopLevelDestination.SETTINGS.route) {
                SettingsScreen(settingsManager = appContainer.settingsManager)
            }
            composable("finance_ai") {
                val aiViewModel: FinanceAIViewModel = viewModel(
                    factory = appContainer.financeAIViewModelFactory
                )
                FinanceAIScreen(
                    viewModel = aiViewModel,
                    onBack = { navController.navigateUp() }
                )
            }
        }
    }
}
