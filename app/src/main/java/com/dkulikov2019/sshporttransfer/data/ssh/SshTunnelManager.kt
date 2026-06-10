package com.dkulikov2019.sshporttransfer.data.ssh

import com.dkulikov2019.sshporttransfer.data.ssh.model.ForwardConfig
import com.dkulikov2019.sshporttransfer.domain.model.ConnectionProfile
import com.dkulikov2019.sshporttransfer.domain.model.Credentials
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SshTunnelManager @Inject constructor() {
    suspend fun connect(profile: ConnectionProfile, credentials: Credentials?) {
        // Placeholder for sshj integration.
    }

    suspend fun startLocalPortForwarding(config: ForwardConfig) {
        // Placeholder for local forwarding startup.
    }

    suspend fun disconnect() {
        // Placeholder for SSH disconnect.
    }
}
