package com.undef.superahorro.haronsignorini.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.undef.superahorro.haronsignorini.ui.screens.HistoryScreen
import com.undef.superahorro.haronsignorini.ui.screens.HomeScreen
import com.undef.superahorro.haronsignorini.ui.screens.LandingScreen
import com.undef.superahorro.haronsignorini.ui.screens.LoginScreen
import com.undef.superahorro.haronsignorini.ui.screens.NewProductScreen
import com.undef.superahorro.haronsignorini.ui.screens.NewPurchaseScreen
import com.undef.superahorro.haronsignorini.ui.screens.ProfileScreen
import com.undef.superahorro.haronsignorini.ui.screens.PurchaseDetailScreen
import com.undef.superahorro.haronsignorini.ui.screens.PurchaseListScreen
import com.undef.superahorro.haronsignorini.ui.screens.RegisterScreen
import com.undef.superahorro.haronsignorini.ui.screens.PasswordChangeScreen
import com.undef.superahorro.haronsignorini.ui.screens.SettingsScreen
import com.undef.superahorro.haronsignorini.ui.screens.SplashScreen
import com.undef.superahorro.haronsignorini.ui.screens.StatsScreen
import com.undef.superahorro.haronsignorini.viewmodel.AuthViewModel
import com.undef.superahorro.haronsignorini.viewmodel.NewPurchaseViewModel
import com.undef.superahorro.haronsignorini.viewmodel.PurchaseListViewModel

@Composable
fun AppNavigation(
    darkModeEnabled: Boolean,
    onDarkModeChange: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    val purchaseListViewModel: PurchaseListViewModel = hiltViewModel()
    val newPurchaseViewModel: NewPurchaseViewModel = hiltViewModel()
    val authViewModel: AuthViewModel = hiltViewModel()
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry.value?.destination?.route
    val showBottomBar = currentRoute !in listOf(
        AppRoutes.Splash.route,
        AppRoutes.Landing.route,
        AppRoutes.Login.route,
        AppRoutes.Register.route
    )

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
                    onQuickLogin = { account -> authViewModel.loginWithMockAccount(account) },
                    onPasswordRecovery = { email, newPassword ->
                        authViewModel.recoverPassword(email, newPassword)
                    }
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
                    username = authViewModel.username,
                    purchaseViewModel = purchaseListViewModel
                )
            }
            composable(AppRoutes.NewPurchase.route) {
                NewPurchaseScreen(
                    navController = navController,
                    newPurchaseViewModel = newPurchaseViewModel
                )
            }
            composable(AppRoutes.NewProduct.route) {
                NewProductScreen(
                    navController = navController,
                    newPurchaseViewModel = newPurchaseViewModel
                )
            }
            composable(
                route = AppRoutes.EditPurchase.route,
                arguments = listOf(navArgument("purchaseId") { type = NavType.IntType })
            ) { backStackEntry ->
                val purchaseId = backStackEntry.arguments?.getInt("purchaseId") ?: 0
                NewPurchaseScreen(
                    navController = navController,
                    purchase = purchaseListViewModel.getPurchaseById(purchaseId),
                    isEditing = true,
                    newPurchaseViewModel = newPurchaseViewModel
                )
            }
            composable(
                route = AppRoutes.EditPurchaseProducts.route,
                arguments = listOf(navArgument("purchaseId") { type = NavType.IntType })
            ) { backStackEntry ->
                val purchaseId = backStackEntry.arguments?.getInt("purchaseId") ?: 0
                NewProductScreen(
                    navController = navController,
                    isEditingPurchase = true,
                    finishRoute = AppRoutes.PurchaseDetail.createRoute(purchaseId),
                    newPurchaseViewModel = newPurchaseViewModel
                )
            }
            composable(AppRoutes.PurchaseList.route) {
                PurchaseListScreen(
                    navController = navController,
                    purchaseViewModel = purchaseListViewModel
                )
            }
            composable(
                route = AppRoutes.PurchaseDetail.route,
                arguments = listOf(navArgument("purchaseId") { type = NavType.IntType })
            ) { backStackEntry ->
                val purchaseId = backStackEntry.arguments?.getInt("purchaseId") ?: 0
                PurchaseDetailScreen(
                    navController = navController,
                    purchase = purchaseListViewModel.getPurchaseById(purchaseId),
                    purchaseListViewModel = purchaseListViewModel
                )
            }
            composable(AppRoutes.History.route) {
                HistoryScreen(
                    navController = navController,
                    purchaseViewModel = purchaseListViewModel
                )
            }
            composable(AppRoutes.Stats.route) {
                StatsScreen(purchaseViewModel = purchaseListViewModel)
            }
            composable(AppRoutes.Profile.route) {
                ProfileScreen(
                    navController = navController,
                    email = authViewModel.userEmail,
                    username = authViewModel.username,
                    onUsernameChange = { username -> authViewModel.updateUsername(username) },
                    onLogout = { authViewModel.logout() }
                )
            }
            composable(AppRoutes.PasswordChange.route) {
                PasswordChangeScreen(
                    navController = navController,
                    onPasswordChange = { currentPassword, newPassword ->
                        authViewModel.changePassword(currentPassword, newPassword)
                    }
                )
            }
            composable(AppRoutes.Settings.route) {
                SettingsScreen(
                    navController = navController,
                    darkModeEnabled = darkModeEnabled,
                    onDarkModeChange = onDarkModeChange
                )
            }
        }
    }
}
