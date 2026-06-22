package com.dkulikov2019.sshporttransfer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.dkulikov2019.sshporttransfer.presentation.navigation.AppNavGraph
import com.dkulikov2019.sshporttransfer.presentation.theme.ThemeViewModel
import com.dkulikov2019.sshporttransfer.ui.theme.SSHPortTransferTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val themeState by themeViewModel.uiState.collectAsState()

            SSHPortTransferTheme(themeMode = themeState.themeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavGraph(
                        selectedThemeMode = themeState.themeMode,
                        onThemeModeSelected = themeViewModel::onThemeModeSelected
                    )
                }
            }
        }
    }
}
