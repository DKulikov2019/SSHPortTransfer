package com.dkulikov2019.sshporttransfer.domain.repository

import com.dkulikov2019.sshporttransfer.domain.model.ConnectionProfile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun observeProfiles(): Flow<List<ConnectionProfile>>
    suspend fun getProfileById(id: String): ConnectionProfile?
    suspend fun saveProfile(profile: ConnectionProfile)
    suspend fun deleteProfile(id: String)
}
