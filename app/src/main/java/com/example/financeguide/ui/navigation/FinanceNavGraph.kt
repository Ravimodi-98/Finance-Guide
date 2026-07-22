package com.example.financeguide.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
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
import com.example.financeguide.ui.FinanceViewModel
import com.example.financeguide.ui.screen.budget.BudgetScreen
import com.example.financeguide.ui.screen.dashboard.DashboardScreen
import com.example.financeguide.ui.screen.insights.InsightsScreen
import com.example.financeguide.ui.screen.settings.SettingsScreen
import com.example.financeguide.ui.screen.transaction.AddTransactionScreen

@Composable
fun FinanceNavGraph(
    viewModel: FinanceViewModel,
    appContainer: com.example.financeguide.di.AppContainer
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: TopLevelDestination.DASHBOARD.route
    
    val currentDestination = TopLevelDestination.entries.find { it.route == currentRoute } 
        ?: TopLevelDestination.DASHBOARD

    Scaffold(
        bottomBar = {
            if (!currentRoute.startsWith("add_transaction")) {
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
            composable(TopLevelDestination.DASHBOARD.route) {
                DashboardScreen(
                    viewModel = viewModel,
                    onAddIncome = {
                        navController.navigate("add_transaction/INCOME")
                    },
                    onAddExpense = {
                        navController.navigate("add_transaction/EXPENSE")
                    }
                )
            }
            composable(TopLevelDestination.BUDGETS.route) {
                BudgetScreen(viewModel = viewModel)
            }
            // Default add_transaction route (from bottom nav, defaults to EXPENSE)
            composable(TopLevelDestination.ADD_TRANSACTION.route) {
                AddTransactionScreen(
                    viewModel = viewModel,
                    onBack = { navController.navigateUp() }
                )
            }
            // Add transaction with pre-selected type (from BalanceCard arrows)
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
        }
    }
}
