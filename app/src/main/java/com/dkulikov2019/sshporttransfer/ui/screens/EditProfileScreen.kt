package com.dkulikov2019.sshporttransfer.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.annotation.StringRes
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dkulikov2019.sshporttransfer.R
import com.dkulikov2019.sshporttransfer.presentation.profile.EditProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var helpDialogContent by remember { mutableStateOf<HelpDialogContent?>(null) }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (state.isEditing) {
                            stringResource(R.string.edit_profile_title)
                        } else {
                            stringResource(R.string.new_profile_title)
                        }
                    )
                }
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
            FieldTitleWithHelp(
                title = stringResource(R.string.profile_name_label),
                onHelpClick = {
                    helpDialogContent = HelpDialogContent(
                        titleRes = R.string.profile_name_label,
                        messageRes = R.string.help_profile_name
                    )
                }
            )
            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::onNameChanged,
                modifier = Modifier.fillMaxWidth()
            )
            FieldTitleWithHelp(
                title = stringResource(R.string.ssh_host_label),
                onHelpClick = {
                    helpDialogContent = HelpDialogContent(
                        titleRes = R.string.ssh_host_label,
                        messageRes = R.string.help_ssh_host
                    )
                }
            )
            OutlinedTextField(
                value = state.sshHost,
                onValueChange = viewModel::onSshHostChanged,
                modifier = Modifier.fillMaxWidth()
            )
            FieldTitleWithHelp(
                title = stringResource(R.string.ssh_port_label),
                onHelpClick = {
                    helpDialogContent = HelpDialogContent(
                        titleRes = R.string.ssh_port_label,
                        messageRes = R.string.help_ssh_port
                    )
                }
            )
            OutlinedTextField(
                value = state.sshPort,
                onValueChange = viewModel::onSshPortChanged,
                modifier = Modifier.fillMaxWidth()
            )
            FieldTitleWithHelp(
                title = stringResource(R.string.username_label),
                onHelpClick = {
                    helpDialogContent = HelpDialogContent(
                        titleRes = R.string.username_label,
                        messageRes = R.string.help_username
                    )
                }
            )
            OutlinedTextField(
                value = state.username,
                onValueChange = viewModel::onUsernameChanged,
                modifier = Modifier.fillMaxWidth()
            )
            FieldTitleWithHelp(
                title = stringResource(R.string.password_label),
                onHelpClick = {
                    helpDialogContent = HelpDialogContent(
                        titleRes = R.string.password_label,
                        messageRes = R.string.help_password
                    )
                }
            )
            OutlinedTextField(
                value = state.password,
                onValueChange = viewModel::onPasswordChanged,
                modifier = Modifier.fillMaxWidth()
            )
            FieldTitleWithHelp(
                title = stringResource(R.string.local_host_label),
                onHelpClick = {
                    helpDialogContent = HelpDialogContent(
                        titleRes = R.string.local_host_label,
                        messageRes = R.string.help_local_host
                    )
                }
            )
            OutlinedTextField(
                value = state.localHost,
                onValueChange = viewModel::onLocalHostChanged,
                modifier = Modifier.fillMaxWidth()
            )
            FieldTitleWithHelp(
                title = stringResource(R.string.local_port_label),
                onHelpClick = {
                    helpDialogContent = HelpDialogContent(
                        titleRes = R.string.local_port_label,
                        messageRes = R.string.help_local_port
                    )
                }
            )
            OutlinedTextField(
                value = state.localPort,
                onValueChange = viewModel::onLocalPortChanged,
                modifier = Modifier.fillMaxWidth()
            )
            FieldTitleWithHelp(
                title = stringResource(R.string.remote_host_label),
                onHelpClick = {
                    helpDialogContent = HelpDialogContent(
                        titleRes = R.string.remote_host_label,
                        messageRes = R.string.help_remote_host
                    )
                }
            )
            OutlinedTextField(
                value = state.remoteHost,
                onValueChange = viewModel::onRemoteHostChanged,
                modifier = Modifier.fillMaxWidth()
            )
            FieldTitleWithHelp(
                title = stringResource(R.string.remote_port_label),
                onHelpClick = {
                    helpDialogContent = HelpDialogContent(
                        titleRes = R.string.remote_port_label,
                        messageRes = R.string.help_remote_port
                    )
                }
            )
            OutlinedTextField(
                value = state.remotePort,
                onValueChange = viewModel::onRemotePortChanged,
                modifier = Modifier.fillMaxWidth()
            )
            FieldTitleWithHelp(
                title = stringResource(R.string.keep_alive_label),
                onHelpClick = {
                    helpDialogContent = HelpDialogContent(
                        titleRes = R.string.keep_alive_label,
                        messageRes = R.string.help_keep_alive
                    )
                }
            )
            OutlinedTextField(
                value = state.keepAliveSeconds,
                onValueChange = viewModel::onKeepAliveChanged,
                modifier = Modifier.fillMaxWidth()
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FieldTitleWithHelp(
                    title = stringResource(R.string.auto_reconnect_label),
                    onHelpClick = {
                        helpDialogContent = HelpDialogContent(
                            titleRes = R.string.auto_reconnect_label,
                            messageRes = R.string.help_auto_reconnect
                        )
                    }
                )
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
                Text(
                    if (state.isEditing) {
                        stringResource(R.string.save_profile_changes)
                    } else {
                        stringResource(R.string.save_profile)
                    }
                )
            }

            helpDialogContent?.let { dialog ->
                AlertDialog(
                    onDismissRequest = { helpDialogContent = null },
                    title = {
                        Text(
                            text = stringResource(dialog.titleRes),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    text = {
                        Text(
                            text = stringResource(dialog.messageRes),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = { helpDialogContent = null }) {
                            Text(stringResource(R.string.help_dialog_ok))
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun FieldTitleWithHelp(
    title: String,
    onHelpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title)
        IconButton(onClick = onHelpClick, modifier = Modifier.size(24.dp)) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = stringResource(R.string.help_icon_content_description, title),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f)
            )
        }
    }
}

private data class HelpDialogContent(
    @StringRes val titleRes: Int,
    @StringRes val messageRes: Int
)
