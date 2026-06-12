package com.undef.superahorro.haronsignorini.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.annotation.StringRes
import com.undef.superahorro.haronsignorini.R
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    @param:StringRes val titleRes: Int,
    val route: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(
        titleRes = R.string.home,
        route = AppRoutes.Home.route,
        icon = Icons.Filled.Home
    ),
    BottomNavItem(
        titleRes = R.string.history,
        route = AppRoutes.History.route,
        icon = Icons.Filled.History
    ),
    BottomNavItem(
        titleRes = R.string.stats,
        route = AppRoutes.Stats.route,
        icon = Icons.Filled.BarChart
    ),
    BottomNavItem(
        titleRes = R.string.profile,
        route = AppRoutes.Profile.route,
        icon = Icons.Filled.Person
    )
)
