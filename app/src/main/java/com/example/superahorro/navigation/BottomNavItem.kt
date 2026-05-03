package com.example.superahorro.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val title: String,
    val route: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(
        title = "Inicio",
        route = AppRoutes.Home.route,
        icon = Icons.Filled.Home
    ),
    BottomNavItem(
        title = "Historial",
        route = AppRoutes.History.route,
        icon = Icons.Filled.History
    ),
    BottomNavItem(
        title = "Estadisticas",
        route = AppRoutes.Stats.route,
        icon = Icons.Filled.BarChart
    ),
    BottomNavItem(
        title = "Perfil",
        route = AppRoutes.Profile.route,
        icon = Icons.Filled.Person
    )
)
