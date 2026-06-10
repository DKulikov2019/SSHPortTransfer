package com.dkulikov2019.sshporttransfer.data.local.mapper

import com.dkulikov2019.sshporttransfer.data.local.db.entity.ConnectionProfileEntity
import com.dkulikov2019.sshporttransfer.domain.model.ConnectionProfile

fun ConnectionProfileEntity.toDomain(): ConnectionProfile = ConnectionProfile(
    id = id,
    name = name,
    sshHost = sshHost,
    sshPort = sshPort,
    username = username,
    authType = authType,
    localHost = localHost,
    localPort = localPort,
    remoteHost = remoteHost,
    remotePort = remotePort,
    keepAliveSeconds = keepAliveSeconds,
    autoReconnect = autoReconnect
)

fun ConnectionProfile.toEntity(): ConnectionProfileEntity = ConnectionProfileEntity(
    id = id,
    name = name,
    sshHost = sshHost,
    sshPort = sshPort,
    username = username,
    authType = authType,
    localHost = localHost,
    localPort = localPort,
    remoteHost = remoteHost,
    remotePort = remotePort,
    keepAliveSeconds = keepAliveSeconds,
    autoReconnect = autoReconnect
)
