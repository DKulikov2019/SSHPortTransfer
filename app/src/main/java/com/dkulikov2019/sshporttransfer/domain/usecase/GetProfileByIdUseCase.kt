package com.dkulikov2019.sshporttransfer.domain.usecase

import com.dkulikov2019.sshporttransfer.domain.model.ConnectionProfile
import com.dkulikov2019.sshporttransfer.domain.repository.ProfileRepository
import javax.inject.Inject

class GetProfileByIdUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(id: String): ConnectionProfile? = profileRepository.getProfileById(id)
}
