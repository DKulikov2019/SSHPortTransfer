package com.dkulikov2019.sshporttransfer.data.repository

import com.dkulikov2019.sshporttransfer.data.ssh.HostKeyVerifierImpl
import com.dkulikov2019.sshporttransfer.data.ssh.SshTunnelManager
import com.dkulikov2019.sshporttransfer.domain.model.ConnectionProfile
import com.dkulikov2019.sshporttransfer.domain.model.Credentials
import com.dkulikov2019.sshporttransfer.domain.model.TunnelState
import com.dkulikov2019.sshporttransfer.domain.repository.SecureCredentialsStore
import com.dkulikov2019.sshporttransfer.domain.repository.SshTunnelRepository
import java.io.FileNotFoundException
import java.net.BindException
import java.net.ConnectException
import java.net.NoRouteToHostException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.schmizz.sshj.userauth.UserAuthException

@Singleton
class SshTunnelRepositoryImpl @Inject constructor(
    private val secureCredentialsStore: SecureCredentialsStore,
    private val sshTunnelManager: SshTunnelManager,
    private val hostKeyVerifierImpl: HostKeyVerifierImpl
) : SshTunnelRepository {

    private val state = MutableStateFlow<TunnelState>(TunnelState.Disconnected)

    override fun observeState(): StateFlow<TunnelState> = state.asStateFlow()

    override suspend fun connect(profile: ConnectionProfile, credentials: Credentials?) {
        state.value = TunnelState.Connecting("Проверяем сохраненные учетные данные")
        val resolvedCredentials = credentials ?: secureCredentialsStore.getCredentials(profile.id)
            ?: throw IllegalStateException("Credentials not found for profile ${profile.name}")

        runCatching {
            sshTunnelManager.connect(profile, resolvedCredentials) { progress ->
                state.value = TunnelState.Connecting(progress)
            }
        }.onSuccess {
            state.value = TunnelState.Connected(
                localHost = profile.localHost,
                localPort = profile.localPort,
                remoteHost = profile.remoteHost,
                remotePort = profile.remotePort
            )
        }.onFailure { throwable ->
            state.value = TunnelState.Failed(toUserFriendlyMessage(profile, throwable))
            throw throwable
        }
    }

    override suspend fun disconnect() {
        sshTunnelManager.disconnect()
        state.value = TunnelState.Disconnected
    }

    private fun toUserFriendlyMessage(profile: ConnectionProfile, throwable: Throwable): String {
        val reason = when (throwable) {
            is UnknownHostException -> "Хост ${profile.sshHost} не найден. Проверьте адрес сервера."
            is ConnectException -> "Не удалось подключиться к ${profile.sshHost}:${profile.sshPort}. Проверьте порт и доступность сервера."
            is NoRouteToHostException -> "Нет маршрута до ${profile.sshHost}. Проверьте сеть и VPN."
            is SocketTimeoutException -> "Таймаут подключения к ${profile.sshHost}:${profile.sshPort}."
            is UserAuthException -> "Ошибка аутентификации. Проверьте логин, пароль или приватный ключ."
            is BindException -> "Локальный порт ${profile.localPort} уже занят. Выберите другой локальный порт."
            is FileNotFoundException -> "Не найден приватный ключ: ${throwable.message ?: "unknown path"}."
            is IllegalStateException -> throwable.message ?: "Учетные данные не найдены."
            else -> throwable.message ?: "SSH tunnel connection failed"
        }
        return reason
    }
}
