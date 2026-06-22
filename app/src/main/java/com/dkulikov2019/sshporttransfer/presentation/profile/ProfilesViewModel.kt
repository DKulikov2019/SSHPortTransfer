package com.dkulikov2019.sshporttransfer.presentation.profile

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dkulikov2019.sshporttransfer.domain.model.TunnelState
import com.dkulikov2019.sshporttransfer.domain.repository.SecureCredentialsStore
import com.dkulikov2019.sshporttransfer.domain.usecase.ConnectTunnelUseCase
import com.dkulikov2019.sshporttransfer.domain.usecase.DeleteProfileUseCase
import com.dkulikov2019.sshporttransfer.domain.usecase.DisconnectTunnelUseCase
import com.dkulikov2019.sshporttransfer.domain.usecase.GetProfilesUseCase
import com.dkulikov2019.sshporttransfer.domain.usecase.ObserveTunnelStateUseCase
import com.dkulikov2019.sshporttransfer.service.TunnelForegroundService
import com.dkulikov2019.sshporttransfer.service.TunnelServiceAction
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ProfilesViewModel @Inject constructor(
    application: Application,
    private val getProfilesUseCase: GetProfilesUseCase,
    private val connectTunnelUseCase: ConnectTunnelUseCase,
    private val disconnectTunnelUseCase: DisconnectTunnelUseCase,
    private val deleteProfileUseCase: DeleteProfileUseCase,
    private val secureCredentialsStore: SecureCredentialsStore,
    observeTunnelStateUseCase: ObserveTunnelStateUseCase
) : AndroidViewModel(application) {

    private val tunnelStateFlow = observeTunnelStateUseCase()
    private val _uiState = MutableStateFlow(ProfilesUiState())
    val uiState: StateFlow<ProfilesUiState> = _uiState.asStateFlow()
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    init {
        observeProfiles()
    }

    private fun observeProfiles() {
        viewModelScope.launch {
            combine(getProfilesUseCase(), tunnelStateFlow) { profiles, tunnelState ->
                profiles to tunnelState
            }.collect { (profiles, tunnelState) ->
                _uiState.update { previous ->
                    val activeProfileId = when (tunnelState) {
                        TunnelState.Disconnected,
                        is TunnelState.Failed -> null
                        is TunnelState.Connecting,
                        is TunnelState.Reconnecting,
                        is TunnelState.Connected -> previous.activeProfileId
                    }
                    previous.copy(
                        profiles = profiles,
                        isLoading = false,
                        errorMessage = (tunnelState as? TunnelState.Failed)?.message,
                        activeProfileId = activeProfileId,
                        tunnelState = tunnelState,
                        connectionDiagnostics = updatedDiagnostics(previous.connectionDiagnostics, tunnelState)
                    )
                }
            }
        }
    }

    fun onConnectClicked(profileId: String) {
        val profile = _uiState.value.profiles.firstOrNull { it.id == profileId } ?: return
        _uiState.update {
            it.copy(
                activeProfileId = profileId,
                errorMessage = null,
                tunnelState = TunnelState.Connecting("Запускаем подключение"),
                connectionDiagnostics = listOf(
                    diagnosticEntry(
                        message = "Старт подключения к ${profile.sshHost}:${profile.sshPort}",
                        level = ConnectionDiagnosticLevel.INFO
                    )
                )
            )
        }
        startTunnelService()
        viewModelScope.launch {
            runCatching {
                connectTunnelUseCase(profile)
            }.onFailure { throwable ->
                stopTunnelService()
                _uiState.update {
                    it.copy(
                        activeProfileId = null,
                        errorMessage = throwable.message ?: "Failed to connect tunnel",
                        tunnelState = TunnelState.Failed(throwable.message ?: "Failed to connect tunnel")
                    )
                }
            }
        }
    }

    fun onDisconnectClicked() {
        viewModelScope.launch {
            runCatching {
                disconnectTunnelUseCase()
            }.onSuccess {
                stopTunnelService()
                _uiState.update {
                    it.copy(
                        activeProfileId = null,
                        errorMessage = null,
                        tunnelState = TunnelState.Disconnected,
                        connectionDiagnostics = emptyList()
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        errorMessage = throwable.message ?: "Failed to disconnect tunnel",
                        tunnelState = TunnelState.Failed(throwable.message ?: "Failed to disconnect tunnel")
                    )
                }
            }
        }
    }

    fun onDeleteClicked(profileId: String) {
        viewModelScope.launch {
            val isActiveProfile = _uiState.value.activeProfileId == profileId
            if (isActiveProfile) {
                runCatching { disconnectTunnelUseCase() }
                stopTunnelService()
            }

            runCatching {
                deleteProfileUseCase(profileId)
                secureCredentialsStore.clearCredentials(profileId)
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        activeProfileId = if (isActiveProfile) null else it.activeProfileId,
                        errorMessage = null,
                        tunnelState = if (isActiveProfile) TunnelState.Disconnected else it.tunnelState
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(errorMessage = throwable.message ?: "Не удалось удалить профиль")
                }
            }
        }
    }

    private fun startTunnelService() {
        val context = getApplication<Application>()
        val intent = Intent(context, TunnelForegroundService::class.java).apply {
            action = TunnelServiceAction.START
        }
        context.startForegroundService(intent)
    }

    private fun stopTunnelService() {
        val context = getApplication<Application>()
        val intent = Intent(context, TunnelForegroundService::class.java).apply {
            action = TunnelServiceAction.STOP
        }
        context.startService(intent)
    }

    private fun updatedDiagnostics(
        current: List<ConnectionDiagnosticEntry>,
        tunnelState: TunnelState
    ): List<ConnectionDiagnosticEntry> {
        val (message, level) = when (tunnelState) {
            is TunnelState.Connecting -> tunnelState.message to ConnectionDiagnosticLevel.INFO
            is TunnelState.Failed -> tunnelState.message to ConnectionDiagnosticLevel.ERROR
            is TunnelState.Connected -> "Туннель установлен" to ConnectionDiagnosticLevel.SUCCESS
            is TunnelState.Reconnecting -> "Повторное подключение, попытка ${tunnelState.attempt}" to ConnectionDiagnosticLevel.WARNING
            TunnelState.Disconnected -> return current
        }
        if (current.lastOrNull()?.message == message) return current
        return (current + diagnosticEntry(message, level)).takeLast(20)
    }

    private fun diagnosticEntry(
        message: String,
        level: ConnectionDiagnosticLevel
    ): ConnectionDiagnosticEntry {
        return ConnectionDiagnosticEntry(
            time = LocalTime.now().format(timeFormatter),
            message = message,
            level = level
        )
    }
}
