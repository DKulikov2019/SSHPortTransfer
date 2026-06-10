package com.dkulikov2019.sshporttransfer.data.ssh

import com.dkulikov2019.sshporttransfer.domain.repository.KnownHostsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HostKeyVerifierImpl @Inject constructor(
    private val knownHostsRepository: KnownHostsRepository
) {
    suspend fun verifyOrStore(host: String, port: Int, fingerprint: String): Boolean {
        val existing = knownHostsRepository.getFingerprint(host, port)
        return if (existing == null) {
            knownHostsRepository.saveFingerprint(host, port, fingerprint)
            true
        } else {
            existing == fingerprint
        }
    }
}
