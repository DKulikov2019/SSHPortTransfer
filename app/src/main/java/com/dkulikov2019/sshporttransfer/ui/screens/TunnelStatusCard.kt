package com.dkulikov2019.sshporttransfer.ui.screens

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
    state: TunnelState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Tunnel status", style = MaterialTheme.typography.titleMedium)
            Text(state.toString(), style = MaterialTheme.typography.bodyMedium)
        }
    }
}
