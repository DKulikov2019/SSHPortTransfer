package com.dkulikov2019.sshporttransfer.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "known_hosts")
data class KnownHostEntity(
    @PrimaryKey val hostPortKey: String,
    val host: String,
    val port: Int,
    val fingerprint: String
)
