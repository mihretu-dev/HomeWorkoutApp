package com.base.androidstartertemplate.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Workouts : BottomNavItem(
        route = "tab_workouts",
        title = "Workouts",
        icon = Icons.Default.FitnessCenter
    )

    object Analytics : BottomNavItem(
        route = "tab_analytics",
        title = "Analytics",
        icon = Icons.Default.Analytics
    )

    object Settings : BottomNavItem(
        route = "tab_settings",
        title = "Settings",
        icon = Icons.Default.Settings
    )

    companion object {
        val items = listOf(Workouts, Analytics, Settings)
    }
}
