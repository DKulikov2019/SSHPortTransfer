package com.dkulikov2019.sshporttransfer.domain.repository

import com.dkulikov2019.sshporttransfer.domain.model.Credentials

interface SecureCredentialsStore {
    suspend fun saveCredentials(profileId: String, credentials: Credentials)
    suspend fun getCredentials(profileId: String): Credentials?
    suspend fun clearCredentials(profileId: String)
}
