package com.dkulikov2019.sshporttransfer.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dkulikov2019.sshporttransfer.data.local.db.dao.KnownHostDao
import com.dkulikov2019.sshporttransfer.data.local.db.dao.ProfileDao
import com.dkulikov2019.sshporttransfer.data.local.db.entity.ConnectionProfileEntity
import com.dkulikov2019.sshporttransfer.data.local.db.entity.KnownHostEntity

@Database(
    entities = [ConnectionProfileEntity::class, KnownHostEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun knownHostDao(): KnownHostDao
}
