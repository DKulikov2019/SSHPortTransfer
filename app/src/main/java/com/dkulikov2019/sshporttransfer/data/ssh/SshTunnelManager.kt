package com.dkulikov2019.sshporttransfer.data.ssh

import android.util.Log
import com.dkulikov2019.sshporttransfer.domain.model.ConnectionProfile
import com.dkulikov2019.sshporttransfer.domain.model.Credentials
import java.security.Security
import java.net.InetSocketAddress
import java.net.ServerSocket
import javax.inject.Inject
import javax.inject.Singleton
import javax.crypto.KeyAgreement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.connection.channel.direct.LocalPortForwarder
import net.schmizz.sshj.connection.channel.direct.Parameters
import org.bouncycastle.jce.provider.BouncyCastleProvider

internal fun authenticateClient(
    client: SSHClient,
    profile: ConnectionProfile,
    credentials: Credentials
) {
    when (credentials) {
        is Credentials.Password -> client.authPassword(profile.username, credentials.value)
        is Credentials.PrivateKey -> {
            val keyProvider = client.loadKeys(credentials.keyAlias, credentials.passphrase)
            client.authPublickey(profile.username, keyProvider)
        }
    }
}

@Singleton
class SshTunnelManager @Inject constructor(
    private val hostKeyVerifier: HostKeyVerifierImpl
) {

    private var sshClient: SSHClient? = null
    private var localPortForwarder: LocalPortForwarder? = null
    private var serverSocket: ServerSocket? = null
    private var forwarderThread: Thread? = null

    private fun cryptoDiagnosticsLine(): String {
        val provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME)
        if (provider == null) {
            return "bcRegistered=false,x25519Supported=false"
        }

        val x25519Supported = runCatching {
            KeyAgreement.getInstance("X25519", BouncyCastleProvider.PROVIDER_NAME)
        }.isSuccess

        return "bcRegistered=true,bcClass=${provider.javaClass.name},bcVersion=${provider.version},x25519Supported=$x25519Supported"
    }

    suspend fun connect(
        profile: ConnectionProfile,
        credentials: Credentials,
        onProgress: (String) -> Unit
    ) = withContext(Dispatchers.IO) {
        disconnect()
        onProgress("Подготавливаем SSH-клиент")

        val client = SSHClient()
        client.addHostKeyVerifier(hostKeyVerifier)
        onProgress("Подключаемся к ${profile.sshHost}:${profile.sshPort}")
        Log.i("SshTunnelManager", "SSH connect diagnostics: ${cryptoDiagnosticsLine()}")
        client.connect(profile.sshHost, profile.sshPort)

        try {
            onProgress("Проверяем учетные данные (${profile.username})")
            authenticateClient(client, profile, credentials)

            onProgress("Открываем локальный порт ${profile.localHost}:${profile.localPort}")
            val socket = ServerSocket().apply {
                reuseAddress = true
                bind(InetSocketAddress(profile.localHost, profile.localPort))
            }

            onProgress("Настраиваем проброс к ${profile.remoteHost}:${profile.remotePort}")
            val parameters = Parameters(
                profile.localHost,
                profile.localPort,
                profile.remoteHost,
                profile.remotePort
            )
            val forwarder = client.newLocalPortForwarder(parameters, socket)
            onProgress("Запускаем локальный forwarder")
            val thread = Thread {
                runCatching {
                    forwarder.listen()
                }.onFailure {
                    Log.e("SshTunnelManager", "Local port forwarder stopped", it)
                }
            }.apply {
                isDaemon = true
                name = "ssh-local-port-forwarder"
                start()
            }

            sshClient = client
            localPortForwarder = forwarder
            serverSocket = socket
            forwarderThread = thread
        } catch (throwable: Throwable) {
            runCatching { client.disconnect() }
            runCatching { client.close() }
            throw throwable
        }
    }

    suspend fun disconnect() = withContext(Dispatchers.IO) {
        runCatching { localPortForwarder?.close() }
        runCatching { serverSocket?.close() }
        runCatching { sshClient?.disconnect() }
        runCatching { sshClient?.close() }
        runCatching { forwarderThread?.interrupt() }
        localPortForwarder = null
        serverSocket = null
        sshClient = null
        forwarderThread = null
    }
}
