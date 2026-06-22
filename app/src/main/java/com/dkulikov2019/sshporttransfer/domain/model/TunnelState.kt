package com.dkulikov2019.sshporttransfer.domain.model

sealed interface TunnelState {
    data object Disconnected : TunnelState
    data class Connecting(val message: String) : TunnelState
    data class Connected(
        val localHost: String,
        val localPort: Int,
        val remoteHost: String,
        val remotePort: Int
    ) : TunnelState
    data class Reconnecting(val attempt: Int) : TunnelState
    data class Failed(val message: String) : TunnelState
}
