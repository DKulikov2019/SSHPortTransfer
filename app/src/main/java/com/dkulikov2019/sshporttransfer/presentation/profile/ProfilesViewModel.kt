package com.dkulikov2019.sshporttransfer.presentation.profile

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dkulikov2019.sshporttransfer.domain.model.TunnelState
import com.dkulikov2019.sshporttransfer.domain.usecase.ConnectTunnelUseCase
import com.dkulikov2019.sshporttransfer.domain.usecase.DisconnectTunnelUseCase
import com.dkulikov2019.sshporttransfer.domain.usecase.GetProfilesUseCase
import com.dkulikov2019.sshporttransfer.domain.usecase.ObserveTunnelStateUseCase
import com.dkulikov2019.sshporttransfer.service.TunnelForegroundService
import com.dkulikov2019.sshporttransfer.service.TunnelServiceAction
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    observeTunnelStateUseCase: ObserveTunnelStateUseCase
) : AndroidViewModel(application) {

    private val tunnelStateFlow = observeTunnelStateUseCase()
    private val _uiState = MutableStateFlow(ProfilesUiState())
    val uiState: StateFlow<ProfilesUiState> = _uiState.asStateFlow()

    init {
        observeProfiles()
    }

    private fun observeProfiles() {
        viewModelScope.launch {
            combine(getProfilesUseCase(), tunnelStateFlow) { profiles, tunnelState ->
                val previousActive = _uiState.value.activeProfileId
                val activeProfileId = when (tunnelState) {
                    TunnelState.Disconnected,
                    is TunnelState.Failed -> null
                    TunnelState.Connecting,
                    is TunnelState.Reconnecting,
                    is TunnelState.Connected -> previousActive
                }
                ProfilesUiState(
                    profiles = profiles,
                    isLoading = false,
                    errorMessage = (tunnelState as? TunnelState.Failed)?.message,
                    activeProfileId = activeProfileId,
                    tunnelState = tunnelState
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun onConnectClicked(profileId: String) {
        val profile = _uiState.value.profiles.firstOrNull { it.id == profileId } ?: return
        _uiState.update {
            it.copy(
                activeProfileId = profileId,
                errorMessage = null,
                tunnelState = TunnelState.Connecting
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
                        tunnelState = TunnelState.Disconnected
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
}
