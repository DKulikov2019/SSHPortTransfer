package com.dkulikov2019.sshporttransfer.presentation.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dkulikov2019.sshporttransfer.domain.model.AuthType
import com.dkulikov2019.sshporttransfer.domain.model.ConnectionProfile
import com.dkulikov2019.sshporttransfer.domain.model.Credentials
import com.dkulikov2019.sshporttransfer.domain.repository.SecureCredentialsStore
import com.dkulikov2019.sshporttransfer.domain.usecase.GetProfileByIdUseCase
import com.dkulikov2019.sshporttransfer.domain.usecase.SaveProfileUseCase
import com.dkulikov2019.sshporttransfer.presentation.navigation.Destinations
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
    savedStateHandle: SavedStateHandle,
    private val saveProfileUseCase: SaveProfileUseCase,
    private val getProfileByIdUseCase: GetProfileByIdUseCase,
    private val secureCredentialsStore: SecureCredentialsStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        val profileId = savedStateHandle.get<String?>(Destinations.EditProfileArg)
        if (!profileId.isNullOrBlank()) {
            loadProfile(profileId)
        }
    }

    fun onNameChanged(value: String) = _uiState.update { it.copy(name = value, validationMessage = null) }
    fun onSshHostChanged(value: String) = _uiState.update { it.copy(sshHost = value, validationMessage = null) }
    fun onSshPortChanged(value: String) = _uiState.update { it.copy(sshPort = value, validationMessage = null) }
    fun onUsernameChanged(value: String) = _uiState.update { it.copy(username = value, validationMessage = null) }
    fun onPasswordChanged(value: String) = _uiState.update { it.copy(password = value, validationMessage = null) }
    fun onLocalHostChanged(value: String) = _uiState.update { it.copy(localHost = value, validationMessage = null) }
    fun onLocalPortChanged(value: String) = _uiState.update { it.copy(localPort = value, validationMessage = null) }
    fun onRemoteHostChanged(value: String) = _uiState.update { it.copy(remoteHost = value, validationMessage = null) }
    fun onRemotePortChanged(value: String) = _uiState.update { it.copy(remotePort = value, validationMessage = null) }
    fun onKeepAliveChanged(value: String) = _uiState.update { it.copy(keepAliveSeconds = value, validationMessage = null) }
    fun onAutoReconnectChanged(value: Boolean) = _uiState.update { it.copy(autoReconnect = value, validationMessage = null) }

    fun saveProfile() {
        val state = _uiState.value
        val sshPort = state.sshPort.toIntOrNull()
        val localPort = state.localPort.toIntOrNull()
        val remotePort = state.remotePort.toIntOrNull()
        val keepAlive = state.keepAliveSeconds.toIntOrNull()

        val validationMessage = when {
            state.name.isBlank() -> "Profile name is required"
            state.sshHost.isBlank() -> "SSH host is required"
            sshPort == null || sshPort !in 1..65535 -> "SSH port must be between 1 and 65535"
            state.username.isBlank() -> "Username is required"
            state.password.isBlank() -> "Password is required"
            state.localHost.isBlank() -> "Local host is required"
            localPort == null || localPort !in 1..65535 -> "Local port must be between 1 and 65535"
            state.remoteHost.isBlank() -> "Remote host is required"
            remotePort == null || remotePort !in 1..65535 -> "Remote port must be between 1 and 65535"
            keepAlive == null || keepAlive < 0 -> "Keep alive must be 0 or greater"
            else -> null
        }

        if (validationMessage != null) {
            _uiState.update { it.copy(validationMessage = validationMessage, isSaved = false) }
            return
        }

        val profileId = state.profileId ?: UUID.randomUUID().toString()
        val profile = ConnectionProfile(
            id = profileId,
            name = state.name,
            sshHost = state.sshHost,
            sshPort = sshPort!!,
            username = state.username,
            authType = AuthType.PASSWORD,
            localHost = state.localHost,
            localPort = localPort!!,
            remoteHost = state.remoteHost,
            remotePort = remotePort!!,
            keepAliveSeconds = keepAlive!!,
            autoReconnect = state.autoReconnect
        )

        viewModelScope.launch {
            saveProfileUseCase(profile)
            secureCredentialsStore.saveCredentials(
                profileId = profileId,
                credentials = Credentials.Password(state.password)
            )
            _uiState.update { it.copy(validationMessage = null, isSaved = true) }
        }
    }

    private fun loadProfile(profileId: String) {
        viewModelScope.launch {
            val profile = getProfileByIdUseCase(profileId)
            if (profile == null) {
                _uiState.update {
                    it.copy(validationMessage = "Профиль не найден")
                }
                return@launch
            }

            val credentials = secureCredentialsStore.getCredentials(profileId)
            _uiState.update {
                it.copy(
                    profileId = profile.id,
                    name = profile.name,
                    sshHost = profile.sshHost,
                    sshPort = profile.sshPort.toString(),
                    username = profile.username,
                    password = (credentials as? Credentials.Password)?.value.orEmpty(),
                    localHost = profile.localHost,
                    localPort = profile.localPort.toString(),
                    remoteHost = profile.remoteHost,
                    remotePort = profile.remotePort.toString(),
                    keepAliveSeconds = profile.keepAliveSeconds.toString(),
                    autoReconnect = profile.autoReconnect,
                    validationMessage = null,
                    isSaved = false
                )
            }
        }
    }
}
