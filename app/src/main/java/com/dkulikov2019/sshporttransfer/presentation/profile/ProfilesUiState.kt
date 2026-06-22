package com.dkulikov2019.sshporttransfer.presentation.profile

import com.dkulikov2019.sshporttransfer.domain.model.ConnectionProfile
import com.dkulikov2019.sshporttransfer.domain.model.TunnelState

data class ProfilesUiState(
    val profiles: List<ConnectionProfile> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val activeProfileId: String? = null,
    val tunnelState: TunnelState = TunnelState.Disconnected,
    val connectionDiagnostics: List<ConnectionDiagnosticEntry> = emptyList()
)

data class ConnectionDiagnosticEntry(
    val time: String,
    val message: String,
    val level: ConnectionDiagnosticLevel = ConnectionDiagnosticLevel.INFO
)

enum class ConnectionDiagnosticLevel {
    INFO,
    SUCCESS,
    WARNING,
    ERROR
}

