package com.dkulikov2019.sshporttransfer.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dkulikov2019.sshporttransfer.domain.model.ThemeMode
import com.dkulikov2019.sshporttransfer.ui.screens.EditProfileScreen
import com.dkulikov2019.sshporttransfer.ui.screens.ProfilesScreen
import com.dkulikov2019.sshporttransfer.ui.screens.SettingsScreen

private val bottomNavItems = listOf(
    Destinations.Profiles to ("Профили" to Icons.AutoMirrored.Filled.ViewList),
    Destinations.Settings to ("Настройки" to Icons.Default.Settings)
)

@Composable
fun AppNavGraph(
    selectedThemeMode: ThemeMode,
    onThemeModeSelected: (ThemeMode) -> Unit
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val showBottomBar = currentRoute in setOf(Destinations.Profiles, Destinations.Settings)

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { (route, meta) ->
                        val (label, icon) = meta
                        NavigationBarItem(
                            selected = currentRoute == route,
                            onClick = {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label) }
                        )
                    }
                }
            }
        }
    ) { _ ->
        NavHost(
            navController = navController,
            startDestination = Destinations.Profiles
        ) {
            composable(Destinations.Profiles) {
                ProfilesScreen(
                    onAddProfile = { navController.navigate(Destinations.EditProfile) },
                    onEditProfile = { profileId ->
                        navController.navigate(Destinations.editProfileRoute(profileId))
                    }
                )
            }
            composable(
                route = Destinations.EditProfileRoute,
                arguments = listOf(
                    navArgument(Destinations.EditProfileArg) {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) {
                EditProfileScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Destinations.Settings) {
                SettingsScreen(
                    selectedThemeMode = selectedThemeMode,
                    onThemeModeSelected = onThemeModeSelected
                )
            }
        }
    }
}
