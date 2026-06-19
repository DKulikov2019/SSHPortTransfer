package com.dkulikov2019.sshporttransfer.data.repository

import com.dkulikov2019.sshporttransfer.data.ssh.HostKeyVerifierImpl
import com.dkulikov2019.sshporttransfer.data.ssh.SshTunnelManager
import com.dkulikov2019.sshporttransfer.domain.model.ConnectionProfile
import com.dkulikov2019.sshporttransfer.domain.model.Credentials
import com.dkulikov2019.sshporttransfer.domain.model.TunnelState
import com.dkulikov2019.sshporttransfer.domain.repository.SecureCredentialsStore
import com.dkulikov2019.sshporttransfer.domain.repository.SshTunnelRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class SshTunnelRepositoryImpl @Inject constructor(
    private val secureCredentialsStore: SecureCredentialsStore,
    private val sshTunnelManager: SshTunnelManager,
    private val hostKeyVerifierImpl: HostKeyVerifierImpl
) : SshTunnelRepository {

    private val state = MutableStateFlow<TunnelState>(TunnelState.Disconnected)

    override fun observeState(): StateFlow<TunnelState> = state.asStateFlow()

    override suspend fun connect(profile: ConnectionProfile, credentials: Credentials?) {
        state.value = TunnelState.Connecting
        val resolvedCredentials = credentials ?: secureCredentialsStore.getCredentials(profile.id)
            ?: throw IllegalStateException("Credentials not found for profile ${profile.name}")

        runCatching {
            sshTunnelManager.connect(profile, resolvedCredentials)
        }.onSuccess {
            state.value = TunnelState.Connected(
                localHost = profile.localHost,
                localPort = profile.localPort,
                remoteHost = profile.remoteHost,
                remotePort = profile.remotePort
            )
        }.onFailure { throwable ->
            state.value = TunnelState.Failed(throwable.message ?: "SSH tunnel connection failed")
            throw throwable
        }
    }

    override suspend fun disconnect() {
        sshTunnelManager.disconnect()
        state.value = TunnelState.Disconnected
    }
}
