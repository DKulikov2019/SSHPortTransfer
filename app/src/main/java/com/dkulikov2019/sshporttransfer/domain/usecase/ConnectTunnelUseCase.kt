package com.dkulikov2019.sshporttransfer.domain.usecase

import com.dkulikov2019.sshporttransfer.domain.model.ConnectionProfile
import com.dkulikov2019.sshporttransfer.domain.repository.SecureCredentialsStore
import com.dkulikov2019.sshporttransfer.domain.repository.SshTunnelRepository
import javax.inject.Inject

class ConnectTunnelUseCase @Inject constructor(
    private val credentialsStore: SecureCredentialsStore,
    private val sshTunnelRepository: SshTunnelRepository
) {
    suspend operator fun invoke(profile: ConnectionProfile) {
        val credentials = credentialsStore.getCredentials(profile.id)
        sshTunnelRepository.connect(profile, credentials)
    }
}
