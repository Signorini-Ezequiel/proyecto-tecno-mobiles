package com.example.superahorro.navigation

sealed class AppRoutes(val route: String) {
    data object Splash : AppRoutes("splash")
    data object Landing : AppRoutes("landing")
    data object Login : AppRoutes("login")
    data object Register : AppRoutes("register")
    data object Home : AppRoutes("home")
    data object NewPurchase : AppRoutes("new_purchase")
    data object NewProduct : AppRoutes("new_product")
    data object PurchaseList : AppRoutes("purchase_list")
    data object PurchaseDetail : AppRoutes("purchase_detail/{purchaseId}") {
        fun createRoute(purchaseId: Int): String = "purchase_detail/$purchaseId"
    }
    data object History : AppRoutes("history")
    data object Stats : AppRoutes("stats")
    data object Profile : AppRoutes("profile")
    data object PasswordChange : AppRoutes("password_change")
    data object Settings : AppRoutes("settings")
}
