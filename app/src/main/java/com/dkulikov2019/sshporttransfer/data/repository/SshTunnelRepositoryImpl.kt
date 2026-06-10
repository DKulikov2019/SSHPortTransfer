package com.dkulikov2019.sshporttransfer.data.repository

import com.dkulikov2019.sshporttransfer.data.ssh.SshTunnelManager
import com.dkulikov2019.sshporttransfer.data.ssh.model.ForwardConfig
import com.dkulikov2019.sshporttransfer.domain.model.ConnectionProfile
import com.dkulikov2019.sshporttransfer.domain.model.Credentials
import com.dkulikov2019.sshporttransfer.domain.model.TunnelState
import com.dkulikov2019.sshporttransfer.domain.repository.SshTunnelRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class SshTunnelRepositoryImpl @Inject constructor(
    private val sshTunnelManager: SshTunnelManager
) : SshTunnelRepository {

    private val state = MutableStateFlow<TunnelState>(TunnelState.Disconnected)

    override fun observeState(): Flow<TunnelState> = state.asStateFlow()

    override suspend fun connect(profile: ConnectionProfile, credentials: Credentials?) {
        state.value = TunnelState.Connecting
        sshTunnelManager.connect(profile, credentials)
        sshTunnelManager.startLocalPortForwarding(
            ForwardConfig(
                localHost = profile.localHost,
                localPort = profile.localPort,
                remoteHost = profile.remoteHost,
                remotePort = profile.remotePort
            )
        )
        state.value = TunnelState.Connected(
            localHost = profile.localHost,
            localPort = profile.localPort,
            remoteHost = profile.remoteHost,
            remotePort = profile.remotePort
        )
    }

    override suspend fun disconnect() {
        sshTunnelManager.disconnect()
        state.value = TunnelState.Disconnected
    }
}
