package com.dkulikov2019.sshporttransfer.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dkulikov2019.sshporttransfer.domain.model.TunnelState

@Composable
fun TunnelStatusCard(
    tunnelState: TunnelState,
    modifier: Modifier = Modifier
) {
    val title = when (tunnelState) {
        TunnelState.Disconnected -> "Tunnel disconnected"
        TunnelState.Connecting -> "Tunnel connecting"
        is TunnelState.Connected -> "Tunnel connected"
        is TunnelState.Reconnecting -> "Tunnel reconnecting"
        is TunnelState.Failed -> "Tunnel failed"
    }

    val subtitle = when (tunnelState) {
        TunnelState.Disconnected -> "No active SSH tunnel"
        TunnelState.Connecting -> "Establishing SSH session"
        is TunnelState.Connected -> "${tunnelState.localHost}:${tunnelState.localPort} → ${tunnelState.remoteHost}:${tunnelState.remotePort}"
        is TunnelState.Reconnecting -> "Attempt ${tunnelState.attempt}"
        is TunnelState.Failed -> tunnelState.message
    }

    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
