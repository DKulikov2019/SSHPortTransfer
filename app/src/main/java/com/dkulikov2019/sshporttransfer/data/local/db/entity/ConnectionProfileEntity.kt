package com.dkulikov2019.sshporttransfer.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dkulikov2019.sshporttransfer.domain.model.AuthType

@Entity(tableName = "connection_profiles")
data class ConnectionProfileEntity(
    @PrimaryKey val id: String,
    val name: String,
    val sshHost: String,
    val sshPort: Int,
    val username: String,
    val authType: AuthType,
    val localHost: String,
    val localPort: Int,
    val remoteHost: String,
    val remotePort: Int,
    val keepAliveSeconds: Int,
    val autoReconnect: Boolean
)
