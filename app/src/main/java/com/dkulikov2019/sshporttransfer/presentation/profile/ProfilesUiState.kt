package com.dkulikov2019.sshporttransfer.presentation.profile

import com.dkulikov2019.sshporttransfer.domain.model.ConnectionProfile

data class ProfilesUiState(
    val profiles: List<ConnectionProfile> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val activeProfileId: String? = null
)
