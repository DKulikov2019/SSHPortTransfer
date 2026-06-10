package com.dkulikov2019.sshporttransfer.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dkulikov2019.sshporttransfer.domain.model.AuthType
import com.dkulikov2019.sshporttransfer.domain.model.ConnectionProfile
import com.dkulikov2019.sshporttransfer.domain.model.Credentials
import com.dkulikov2019.sshporttransfer.domain.repository.SecureCredentialsStore
import com.dkulikov2019.sshporttransfer.domain.usecase.SaveProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val saveProfileUseCase: SaveProfileUseCase,
    private val secureCredentialsStore: SecureCredentialsStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    fun onNameChanged(value: String) = _uiState.update { it.copy(name = value) }
    fun onSshHostChanged(value: String) = _uiState.update { it.copy(sshHost = value) }
    fun onSshPortChanged(value: String) = _uiState.update { it.copy(sshPort = value) }
    fun onUsernameChanged(value: String) = _uiState.update { it.copy(username = value) }
    fun onPasswordChanged(value: String) = _uiState.update { it.copy(password = value) }
    fun onLocalHostChanged(value: String) = _uiState.update { it.copy(localHost = value) }
    fun onLocalPortChanged(value: String) = _uiState.update { it.copy(localPort = value) }
    fun onRemoteHostChanged(value: String) = _uiState.update { it.copy(remoteHost = value) }
    fun onRemotePortChanged(value: String) = _uiState.update { it.copy(remotePort = value) }
    fun onKeepAliveChanged(value: String) = _uiState.update { it.copy(keepAliveSeconds = value) }
    fun onAutoReconnectChanged(value: Boolean) = _uiState.update { it.copy(autoReconnect = value) }

    fun saveProfile() {
        val state = _uiState.value
        val profileId = UUID.randomUUID().toString()
        val profile = ConnectionProfile(
            id = profileId,
            name = state.name,
            sshHost = state.sshHost,
            sshPort = state.sshPort.toIntOrNull() ?: 0,
            username = state.username,
            authType = AuthType.PASSWORD,
            localHost = state.localHost,
            localPort = state.localPort.toIntOrNull() ?: 0,
            remoteHost = state.remoteHost,
            remotePort = state.remotePort.toIntOrNull() ?: 0,
            keepAliveSeconds = state.keepAliveSeconds.toIntOrNull() ?: 0,
            autoReconnect = state.autoReconnect
        )

        viewModelScope.launch {
            saveProfileUseCase(profile)
            secureCredentialsStore.saveCredentials(
                profileId = profileId,
                credentials = Credentials.Password(state.password)
            )
        }
    }
}
