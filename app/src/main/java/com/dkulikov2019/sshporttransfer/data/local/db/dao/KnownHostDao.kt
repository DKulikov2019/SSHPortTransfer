package com.dkulikov2019.sshporttransfer.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dkulikov2019.sshporttransfer.data.local.db.entity.KnownHostEntity

@Dao
interface KnownHostDao {
    @Query("SELECT * FROM known_hosts WHERE host = :host AND port = :port LIMIT 1")
    suspend fun getByHostAndPort(host: String, port: Int): KnownHostEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: KnownHostEntity)

    @Query("DELETE FROM known_hosts WHERE host = :host AND port = :port")
    suspend fun deleteByHostAndPort(host: String, port: Int)
}
