package com.dkulikov2019.sshporttransfer.domain.usecase

import com.dkulikov2019.sshporttransfer.domain.repository.ProfileRepository
import javax.inject.Inject

class DeleteProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(id: String) = profileRepository.deleteProfile(id)
}
