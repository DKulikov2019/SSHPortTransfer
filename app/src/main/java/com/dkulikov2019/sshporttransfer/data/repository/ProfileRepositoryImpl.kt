package com.dkulikov2019.sshporttransfer.data.repository

import com.dkulikov2019.sshporttransfer.data.local.db.dao.ProfileDao
import com.dkulikov2019.sshporttransfer.data.local.mapper.toDomain
import com.dkulikov2019.sshporttransfer.data.local.mapper.toEntity
import com.dkulikov2019.sshporttransfer.domain.model.ConnectionProfile
import com.dkulikov2019.sshporttransfer.domain.repository.ProfileRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val profileDao: ProfileDao
) : ProfileRepository {
    override fun observeProfiles(): Flow<List<ConnectionProfile>> =
        profileDao.observeProfiles().map { list -> list.map { it.toDomain() } }

    override suspend fun getProfileById(id: String): ConnectionProfile? =
        profileDao.getProfileById(id)?.toDomain()

    override suspend fun saveProfile(profile: ConnectionProfile) {
        profileDao.insertProfile(profile.toEntity())
    }

    override suspend fun deleteProfile(id: String) {
        profileDao.deleteProfileById(id)
    }
}
