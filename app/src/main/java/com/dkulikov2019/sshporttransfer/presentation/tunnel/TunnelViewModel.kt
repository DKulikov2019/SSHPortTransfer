package com.dkulikov2019.sshporttransfer.presentation.tunnel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dkulikov2019.sshporttransfer.domain.usecase.ObserveTunnelStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class TunnelViewModel @Inject constructor(
    private val observeTunnelStateUseCase: ObserveTunnelStateUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TunnelUiState())
    val uiState: StateFlow<TunnelUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeTunnelStateUseCase().collect { state ->
                _uiState.update { it.copy(state = state) }
            }
        }
    }
}
