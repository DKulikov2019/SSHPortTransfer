package com.dkulikov2019.sshporttransfer.data.ssh

import android.util.Log
import com.dkulikov2019.sshporttransfer.domain.model.ConnectionProfile
import com.dkulikov2019.sshporttransfer.domain.model.Credentials
import java.net.InetSocketAddress
import java.net.ServerSocket
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.connection.channel.direct.LocalPortForwarder

@Singleton
class SshTunnelManager @Inject constructor(
    private val hostKeyVerifier: HostKeyVerifierImpl
) {

    private var sshClient: SSHClient? = null
    private var localPortForwarder: LocalPortForwarder? = null
    private var serverSocket: ServerSocket? = null
    private var forwarderThread: Thread? = null

    suspend fun connect(
        profile: ConnectionProfile,
        credentials: Credentials
    ) = withContext(Dispatchers.IO) {
        disconnect()

        val client = SSHClient()
        client.addHostKeyVerifier(hostKeyVerifier)
        client.connect(profile.sshHost, profile.sshPort)

        try {
            when (credentials) {
                is Credentials.Password -> client.authPassword(profile.username, credentials.value)
                is Credentials.PrivateKey -> {
                    val keyProvider = client.loadKeys(credentials.privateKeyPath, credentials.passphrase)
                    client.authPublickey(profile.username, keyProvider)
                }
            }

            val socket = ServerSocket().apply {
                reuseAddress = true
                bind(InetSocketAddress(profile.localHost, profile.localPort))
            }

            val parameters = LocalPortForwarder.Parameters(
                profile.localHost,
                profile.localPort,
                profile.remoteHost,
                profile.remotePort
            )
            val forwarder = client.newLocalPortForwarder(parameters, socket)
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
