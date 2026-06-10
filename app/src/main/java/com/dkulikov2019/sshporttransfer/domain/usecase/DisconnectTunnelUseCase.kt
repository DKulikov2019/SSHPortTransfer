package com.dkulikov2019.sshporttransfer.domain.usecase

import com.dkulikov2019.sshporttransfer.domain.repository.SshTunnelRepository
import javax.inject.Inject

class DisconnectTunnelUseCase @Inject constructor(
    private val sshTunnelRepository: SshTunnelRepository
) {
    suspend operator fun invoke() = sshTunnelRepository.disconnect()
}
