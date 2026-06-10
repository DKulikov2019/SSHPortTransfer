package com.dkulikov2019.sshporttransfer.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dkulikov2019.sshporttransfer.ui.screens.EditProfileScreen
import com.dkulikov2019.sshporttransfer.ui.screens.ProfilesScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Destinations.Profiles
    ) {
        composable(Destinations.Profiles) {
            ProfilesScreen(
                onAddProfile = { navController.navigate(Destinations.EditProfile) }
            )
        }
        composable(Destinations.EditProfile) {
            EditProfileScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
