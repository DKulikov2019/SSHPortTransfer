package com.dkulikov2019.sshporttransfer.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dkulikov2019.sshporttransfer.presentation.profile.ProfilesViewModel
import com.dkulikov2019.sshporttransfer.ui.components.TunnelStatusCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilesScreen(
    onAddProfile: () -> Unit,
    viewModel: ProfilesViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SSHPortTransfer") }
            )
        }
    ) { padding ->
        if (state.profiles.isEmpty()) {
            EmptyProfilesState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                onAddProfile = onAddProfile
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    TunnelStatusCard(tunnelState = state.tunnelState)
                }
                item {
                    state.errorMessage?.let { message ->
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }
                items(state.profiles, key = { it.id }) { profile ->
                    val isActive = state.activeProfileId == profile.id
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(profile.name, style = MaterialTheme.typography.titleMedium)
                            Text(profile.sshHost, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = "${profile.localHost}:${profile.localPort} → ${profile.remoteHost}:${profile.remotePort}",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (isActive) {
                                    Button(onClick = viewModel::onDisconnectClicked) {
                                        Text("Disconnect")
                                    }
                                } else {
                                    Button(onClick = { viewModel.onConnectClicked(profile.id) }) {
                                        Text("Connect")
                                    }
                                }
                            }
                        }
                    }
                }
                item {
                    Button(
                        onClick = onAddProfile,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Добавить ещё профиль")
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyProfilesState(
    modifier: Modifier = Modifier,
    onAddProfile: () -> Unit
) {
    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TunnelStatusCard(tunnelState = com.dkulikov2019.sshporttransfer.domain.model.TunnelState.Disconnected)
        Text(
            text = "Профили пока не созданы",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "Добавьте первый SSH-профиль. Поля подключения не предзаполняются.",
            style = MaterialTheme.typography.bodyMedium
        )
        Button(onClick = onAddProfile) {
            Text("Создать профиль")
        }
    }
}
