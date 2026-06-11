package com.dkulikov2019.sshporttransfer.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dkulikov2019.sshporttransfer.presentation.profile.EditProfileViewModel

@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Новый профиль") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::onNameChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Profile name") }
            )
            OutlinedTextField(
                value = state.sshHost,
                onValueChange = viewModel::onSshHostChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("SSH host") }
            )
            OutlinedTextField(
                value = state.sshPort,
                onValueChange = viewModel::onSshPortChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("SSH port") }
            )
            OutlinedTextField(
                value = state.username,
                onValueChange = viewModel::onUsernameChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Username") }
            )
            OutlinedTextField(
                value = state.password,
                onValueChange = viewModel::onPasswordChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Password") }
            )
            OutlinedTextField(
                value = state.localHost,
                onValueChange = viewModel::onLocalHostChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Local host") }
            )
            OutlinedTextField(
                value = state.localPort,
                onValueChange = viewModel::onLocalPortChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Local port") }
            )
            OutlinedTextField(
                value = state.remoteHost,
                onValueChange = viewModel::onRemoteHostChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Remote host") }
            )
            OutlinedTextField(
                value = state.remotePort,
                onValueChange = viewModel::onRemotePortChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Remote port") }
            )
            OutlinedTextField(
                value = state.keepAliveSeconds,
                onValueChange = viewModel::onKeepAliveChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Keep alive") }
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Auto reconnect")
                Switch(
                    checked = state.autoReconnect,
                    onCheckedChange = viewModel::onAutoReconnectChanged
                )
            }
            state.validationMessage?.let { message ->
                Text(text = message)
            }
            Button(
                onClick = viewModel::saveProfile,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Сохранить")
            }
        }
    }
}
