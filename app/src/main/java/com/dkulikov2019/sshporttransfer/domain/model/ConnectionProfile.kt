package com.dkulikov2019.sshporttransfer.domain.model

data class ConnectionProfile(
    val id: String,
    val name: String,
    val sshHost: String,
    val sshPort: Int,
    val username: String,
    val authType: AuthType,
    val localHost: String,
    val localPort: Int,
    val remoteHost: String,
    val remotePort: Int,
    val keepAliveSeconds: Int,
    val autoReconnect: Boolean
)
