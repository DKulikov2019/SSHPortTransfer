package com.dkulikov2019.sshporttransfer.presentation.profile

import com.dkulikov2019.sshporttransfer.domain.model.ConnectionProfile
import com.dkulikov2019.sshporttransfer.domain.model.TunnelState

data class ProfilesUiState(
    val profiles: List<ConnectionProfile> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val activeProfileId: String? = null,
    val tunnelState: TunnelState = TunnelState.Disconnected
)
