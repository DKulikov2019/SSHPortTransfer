package com.dkulikov2019.sshporttransfer.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dkulikov2019.sshporttransfer.domain.model.TunnelState
import com.dkulikov2019.sshporttransfer.domain.usecase.ConnectTunnelUseCase
import com.dkulikov2019.sshporttransfer.domain.usecase.DisconnectTunnelUseCase
import com.dkulikov2019.sshporttransfer.domain.usecase.GetProfilesUseCase
import com.dkulikov2019.sshporttransfer.domain.usecase.ObserveTunnelStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ProfilesViewModel @Inject constructor(
    private val getProfilesUseCase: GetProfilesUseCase,
    private val connectTunnelUseCase: ConnectTunnelUseCase,
    private val disconnectTunnelUseCase: DisconnectTunnelUseCase,
    observeTunnelStateUseCase: ObserveTunnelStateUseCase
) : ViewModel() {

    private val tunnelState = observeTunnelStateUseCase()
    private val _uiState = MutableStateFlow(ProfilesUiState())
    val uiState: StateFlow<ProfilesUiState> = _uiState.asStateFlow()

    init {
        observeProfiles()
    }

    private fun observeProfiles() {
        viewModelScope.launch {
            combine(getProfilesUseCase(), tunnelState) { profiles, tunnel ->
                val activeProfileId = when (tunnel) {
                    TunnelState.Disconnected,
                    TunnelState.Failed,
                    TunnelState.Connecting,
                    is TunnelState.Reconnecting -> _uiState.value.activeProfileId
                    is TunnelState.Connected -> _uiState.value.activeProfileId
                }
                ProfilesUiState(
                    profiles = profiles,
                    isLoading = false,
                    errorMessage = (tunnel as? TunnelState.Failed)?.message,
                    activeProfileId = activeProfileId
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun onConnectClicked(profileId: String) {
        val profile = _uiState.value.profiles.firstOrNull { it.id == profileId } ?: return
        _uiState.update { it.copy(activeProfileId = profileId, errorMessage = null) }
        viewModelScope.launch {
            runCatching {
                connectTunnelUseCase(profile)
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        activeProfileId = null,
                        errorMessage = throwable.message ?: "Failed to connect tunnel"
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
                _uiState.update { it.copy(activeProfileId = null, errorMessage = null) }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(errorMessage = throwable.message ?: "Failed to disconnect tunnel")
                }
            }
        }
    }
}
