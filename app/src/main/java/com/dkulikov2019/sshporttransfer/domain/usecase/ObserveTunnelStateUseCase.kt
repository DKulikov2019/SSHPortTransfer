package com.dkulikov2019.sshporttransfer.domain.usecase

import com.dkulikov2019.sshporttransfer.domain.model.TunnelState
import com.dkulikov2019.sshporttransfer.domain.repository.SshTunnelRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveTunnelStateUseCase @Inject constructor(
    private val sshTunnelRepository: SshTunnelRepository
) {
    operator fun invoke(): Flow<TunnelState> = sshTunnelRepository.observeState()
}
