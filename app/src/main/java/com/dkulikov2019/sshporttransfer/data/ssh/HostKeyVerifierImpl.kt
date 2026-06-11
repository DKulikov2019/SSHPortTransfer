package com.dkulikov2019.sshporttransfer.data.ssh

import com.dkulikov2019.sshporttransfer.domain.repository.KnownHostsRepository
import java.security.MessageDigest
import java.security.PublicKey
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.runBlocking
import net.schmizz.sshj.transport.verification.HostKeyVerifier

@Singleton
class HostKeyVerifierImpl @Inject constructor(
    private val knownHostsRepository: KnownHostsRepository
) : HostKeyVerifier {

    override fun verify(hostname: String?, port: Int, key: PublicKey?): Boolean {
        if (hostname == null || key == null) return false
        val fingerprint = key.encoded.sha256()
        return try {
            val stored = runBlocking {
                knownHostsRepository.getFingerprint(hostname, port)
            }
            if (stored == null) {
                runBlocking {
                    knownHostsRepository.saveFingerprint(hostname, port, fingerprint)
                }
                true
            } else {
                stored == fingerprint
            }
        } catch (_: Throwable) {
            false
        }
    }

    private fun ByteArray.sha256(): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(this)
        return digest.joinToString("") { "%02x".format(it) }
    }
}
