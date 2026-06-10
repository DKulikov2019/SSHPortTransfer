package com.dkulikov2019.sshporttransfer.domain.model

sealed interface Credentials {
    data class Password(val value: String) : Credentials
    data class PrivateKey(
        val keyAlias: String,
        val passphrase: String? = null
    ) : Credentials
}
