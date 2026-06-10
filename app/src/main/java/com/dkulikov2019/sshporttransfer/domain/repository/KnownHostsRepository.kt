package com.dkulikov2019.sshporttransfer.domain.repository

interface KnownHostsRepository {
    suspend fun getFingerprint(host: String, port: Int): String?
    suspend fun saveFingerprint(host: String, port: Int, fingerprint: String)
    suspend fun clearFingerprint(host: String, port: Int)
}
