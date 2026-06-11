package com.dkulikov2019.sshporttransfer.data.repository

import com.dkulikov2019.sshporttransfer.data.local.db.dao.KnownHostDao
import com.dkulikov2019.sshporttransfer.data.local.db.entity.KnownHostEntity
import com.dkulikov2019.sshporttransfer.domain.repository.KnownHostsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KnownHostsRepositoryImpl @Inject constructor(
    private val knownHostDao: KnownHostDao
) : KnownHostsRepository {
    override suspend fun getFingerprint(host: String, port: Int): String? {
        return knownHostDao.getByHostAndPort(host, port)?.fingerprint
    }

    override suspend fun saveFingerprint(host: String, port: Int, fingerprint: String) {
        knownHostDao.insert(
            KnownHostEntity(
                hostPortKey = "$host:$port",
                host = host,
                port = port,
                fingerprint = fingerprint
            )
        )
    }

    override suspend fun clearFingerprint(host: String, port: Int) {
        knownHostDao.deleteByHostAndPort(host, port)
    }
}
