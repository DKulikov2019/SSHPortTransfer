package com.dkulikov2019.sshporttransfer.data.repository

import com.dkulikov2019.sshporttransfer.domain.repository.KnownHostsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KnownHostsRepositoryImpl @Inject constructor() : KnownHostsRepository {
    private val trustedHosts = mutableMapOf<String, String>()

    override suspend fun getFingerprint(host: String, port: Int): String? {
        return trustedHosts[key(host, port)]
    }

    override suspend fun saveFingerprint(host: String, port: Int, fingerprint: String) {
        trustedHosts[key(host, port)] = fingerprint
    }

    override suspend fun clearFingerprint(host: String, port: Int) {
        trustedHosts.remove(key(host, port))
    }

    private fun key(host: String, port: Int) = "$host:$port"
}
