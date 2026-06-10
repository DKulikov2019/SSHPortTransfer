package com.dkulikov2019.sshporttransfer.data.local.db

import androidx.room.TypeConverter
import com.dkulikov2019.sshporttransfer.domain.model.AuthType

class RoomConverters {
    @TypeConverter
    fun fromAuthType(value: AuthType): String = value.name

    @TypeConverter
    fun toAuthType(value: String): AuthType = AuthType.valueOf(value)
}
