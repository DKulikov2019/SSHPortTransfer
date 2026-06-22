package com.dkulikov2019.sshporttransfer.data.ssh

import com.dkulikov2019.sshporttransfer.domain.model.AuthType
import com.dkulikov2019.sshporttransfer.domain.model.ConnectionProfile
import com.dkulikov2019.sshporttransfer.domain.model.Credentials
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.userauth.keyprovider.KeyProvider
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever

class SshTunnelManagerAuthTest {

    private val profile = ConnectionProfile(
        id = "p1",
        name = "Test",
        sshHost = "example.com",
        sshPort = 22,
        username = "alice",
        authType = AuthType.PASSWORD,
        localHost = "127.0.0.1",
        localPort = 8080,
        remoteHost = "127.0.0.1",
        remotePort = 80,
        keepAliveSeconds = 30,
        autoReconnect = false
    )

    @Test
    fun `authenticateClient uses password auth for password credentials`() {
        val client = mock<SSHClient>()
        val credentials = Credentials.Password("secret")

        authenticateClient(client, profile, credentials)

        verify(client).authPassword("alice", "secret")
        verifyNoMoreInteractions(client)
    }

    @Test
    fun `authenticateClient uses key auth for private key credentials`() {
        val client = mock<SSHClient>()
        val keyProvider = mock<KeyProvider>()
        whenever(client.loadKeys("/keys/id_ed25519", "passphrase")).thenReturn(keyProvider)

        val credentials = Credentials.PrivateKey(
            keyAlias = "/keys/id_ed25519",
            passphrase = "passphrase"
        )

        authenticateClient(client, profile, credentials)

        verify(client).loadKeys("/keys/id_ed25519", "passphrase")
        verify(client).authPublickey("alice", keyProvider)
        verify(client, never()).authPassword(any(), any<String>())
    }
}



