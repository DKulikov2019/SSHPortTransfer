package com.dkulikov2019.sshporttransfer.data.secure

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.dkulikov2019.sshporttransfer.domain.model.Credentials
import com.dkulikov2019.sshporttransfer.domain.repository.SecureCredentialsStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncryptedCredentialsStore @Inject constructor(
    @ApplicationContext context: Context
) : SecureCredentialsStore {

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        FILE_NAME,
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override suspend fun saveCredentials(profileId: String, credentials: Credentials) {
        when (credentials) {
            is Credentials.Password -> sharedPreferences.edit()
                .putString(passwordKey(profileId), credentials.value)
                .apply()
            is Credentials.PrivateKey -> {
                sharedPreferences.edit()
                    .putString(privateKeyAliasKey(profileId), credentials.keyAlias)
                    .putString(privateKeyPassphraseKey(profileId), credentials.passphrase)
                    .apply()
            }
        }
    }

    override suspend fun getCredentials(profileId: String): Credentials? {
        val password = sharedPreferences.getString(passwordKey(profileId), null)
        if (password != null) return Credentials.Password(password)

        val keyAlias = sharedPreferences.getString(privateKeyAliasKey(profileId), null)
        if (keyAlias != null) {
            return Credentials.PrivateKey(
                keyAlias = keyAlias,
                passphrase = sharedPreferences.getString(privateKeyPassphraseKey(profileId), null)
            )
        }

        return null
    }

    override suspend fun clearCredentials(profileId: String) {
        sharedPreferences.edit()
            .remove(passwordKey(profileId))
            .remove(privateKeyAliasKey(profileId))
            .remove(privateKeyPassphraseKey(profileId))
            .apply()
    }

    private fun passwordKey(profileId: String) = "credentials.password.$profileId"
    private fun privateKeyAliasKey(profileId: String) = "credentials.privateKey.alias.$profileId"
    private fun privateKeyPassphraseKey(profileId: String) = "credentials.privateKey.passphrase.$profileId"

    private companion object {
        const val FILE_NAME = "secure_credentials"
    }
}
