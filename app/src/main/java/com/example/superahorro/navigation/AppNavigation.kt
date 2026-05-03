package com.example.superahorro.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.superahorro.ui.screens.HistoryScreen
import com.example.superahorro.ui.screens.HomeScreen
import com.example.superahorro.ui.screens.LandingScreen
import com.example.superahorro.ui.screens.LoginScreen
import com.example.superahorro.ui.screens.NewProductScreen
import com.example.superahorro.ui.screens.NewPurchaseScreen
import com.example.superahorro.ui.screens.ProfileScreen
import com.example.superahorro.ui.screens.PurchaseDetailScreen
import com.example.superahorro.ui.screens.PurchaseListScreen
import com.example.superahorro.ui.screens.RegisterScreen
import com.example.superahorro.ui.screens.SettingsScreen
import com.example.superahorro.ui.screens.SplashScreen
import com.example.superahorro.ui.screens.StatsScreen
import com.example.superahorro.viewmodel.AuthViewModel
import com.example.superahorro.viewmodel.PurchaseViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val purchaseViewModel: PurchaseViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry.value?.destination?.route
    val showBottomBar = bottomNavItems.any { it.route == currentRoute }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                SuperAhorroBottomBar(
                    navController = navController,
                    currentRoute = currentRoute
                )
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = AppRoutes.Splash.route,
            modifier = Modifier.padding(it)
        ) {
            composable(AppRoutes.Splash.route) {
                SplashScreen(
                    navController = navController,
                    isLoggedIn = authViewModel.isLoggedIn
                )
            }
            composable(AppRoutes.Landing.route) {
                LandingScreen(navController = navController)
            }
            composable(AppRoutes.Login.route) {
                LoginScreen(
                    navController = navController,
                    quickAccounts = authViewModel.quickAccounts,
                    onLogin = { email, password -> authViewModel.login(email, password) },
                    onQuickLogin = { account -> authViewModel.loginWithMockAccount(account) }
                )
            }
            composable(AppRoutes.Register.route) {
                RegisterScreen(
                    navController = navController,
                    onRegister = { email, password, username ->
                        authViewModel.register(email, password, username)
                    }
                )
            }
            composable(AppRoutes.Home.route) {
                HomeScreen(
                    navController = navController,
                    purchases = purchaseViewModel.purchases
                )
            }
            composable(AppRoutes.NewPurchase.route) {
                NewPurchaseScreen(navController = navController)
            }
            composable(AppRoutes.NewProduct.route) {
                NewProductScreen(navController = navController)
            }
            composable(AppRoutes.PurchaseList.route) {
                PurchaseListScreen(
                    navController = navController,
                    purchases = purchaseViewModel.purchases
                )
            }
            composable(
                route = AppRoutes.PurchaseDetail.route,
                arguments = listOf(navArgument("purchaseId") { type = NavType.IntType })
            ) { backStackEntry ->
                val purchaseId = backStackEntry.arguments?.getInt("purchaseId") ?: 0
                PurchaseDetailScreen(
                    navController = navController,
                    purchase = purchaseViewModel.getPurchaseById(purchaseId)
                )
            }
            composable(AppRoutes.History.route) {
                HistoryScreen(
                    navController = navController,
                    purchases = purchaseViewModel.purchases
                )
            }
            composable(AppRoutes.Stats.route) {
                StatsScreen(
                    purchases = purchaseViewModel.purchases
                )
            }
            composable(AppRoutes.Profile.route) {
                ProfileScreen(
                    navController = navController,
                    email = authViewModel.userEmail,
                    username = authViewModel.username,
                    onUsernameChange = { username -> authViewModel.updateUsername(username) },
                    onPasswordChange = { currentPassword, newPassword ->
                        authViewModel.changePassword(currentPassword, newPassword)
                    },
                    onLogout = { authViewModel.logout() }
                )
            }
            composable(AppRoutes.Settings.route) {
                SettingsScreen(navController = navController)
            }
        }
    }
}
