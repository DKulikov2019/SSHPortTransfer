package com.dkulikov2019.sshporttransfer.presentation.profile

data class EditProfileUiState(
    val name: String = "",
    val sshHost: String = "",
    val sshPort: String = "",
    val username: String = "",
    val password: String = "",
    val localHost: String = "",
    val localPort: String = "",
    val remoteHost: String = "",
    val remotePort: String = "",
    val keepAliveSeconds: String = "",
    val autoReconnect: Boolean = false,
    val validationMessage: String? = null
)
