package com.dkulikov2019.sshporttransfer.domain.repository

import com.dkulikov2019.sshporttransfer.domain.model.ConnectionProfile
import com.dkulikov2019.sshporttransfer.domain.model.Credentials
import com.dkulikov2019.sshporttransfer.domain.model.TunnelState
import kotlinx.coroutines.flow.Flow

interface SshTunnelRepository {
    fun observeState(): Flow<TunnelState>
    suspend fun connect(profile: ConnectionProfile, credentials: Credentials?)
    suspend fun disconnect()
}
