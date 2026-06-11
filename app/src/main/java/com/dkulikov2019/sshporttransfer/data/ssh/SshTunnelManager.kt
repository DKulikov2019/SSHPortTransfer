package com.dkulikov2019.sshporttransfer.data.ssh

import com.dkulikov2019.sshporttransfer.domain.model.ConnectionProfile
import com.dkulikov2019.sshporttransfer.domain.model.Credentials
import java.net.ServerSocket
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.connection.channel.direct.LocalPortForwarder
import net.schmizz.sshj.transport.verification.PromiscuousVerifier

@Singleton
class SshTunnelManager @Inject constructor() {

    private var sshClient: SSHClient? = null
    private var localPortForwarder: LocalPortForwarder? = null
    private var forwarderThread: Thread? = null

    suspend fun connect(
        profile: ConnectionProfile,
        credentials: Credentials
    ) = withContext(Dispatchers.IO) {
        disconnect()

        val client = SSHClient()
        client.addHostKeyVerifier(PromiscuousVerifier())
        client.connect(profile.sshHost, profile.sshPort)

        when (credentials) {
            is Credentials.Password -> client.authPassword(profile.username, credentials.value)
            is Credentials.PrivateKey -> {
                val keyProvider = client.loadKeys(credentials.privateKeyPath, credentials.passphrase)
                client.authPublickey(profile.username, keyProvider)
            }
        }

        val parameters = LocalPortForwarder.Parameters(
            profile.localHost,
            profile.localPort,
            profile.remoteHost,
            profile.remotePort
        )
        val serverSocket = ServerSocket()
        serverSocket.reuseAddress = true
        serverSocket.bind(java.net.InetSocketAddress(profile.localHost, profile.localPort))
        val forwarder = client.newLocalPortForwarder(parameters, serverSocket)
        val thread = Thread {
            runCatching {
                forwarder.listen()
            }
        }.apply {
            isDaemon = true
            name = "ssh-local-port-forwarder"
            start()
        }

        sshClient = client
        localPortForwarder = forwarder
        forwarderThread = thread
    }

    suspend fun disconnect() = withContext(Dispatchers.IO) {
        runCatching { localPortForwarder?.close() }
        runCatching { sshClient?.disconnect() }
        runCatching { sshClient?.close() }
        runCatching { forwarderThread?.interrupt() }
        localPortForwarder = null
        sshClient = null
        forwarderThread = null
    }
}
