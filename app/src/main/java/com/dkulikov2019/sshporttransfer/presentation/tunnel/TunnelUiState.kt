package com.dkulikov2019.sshporttransfer.presentation.tunnel

import com.dkulikov2019.sshporttransfer.domain.model.TunnelState

data class TunnelUiState(
    val state: TunnelState = TunnelState.Disconnected
)
