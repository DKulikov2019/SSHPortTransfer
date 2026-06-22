package com.dkulikov2019.sshporttransfer.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.dkulikov2019.sshporttransfer.domain.model.TunnelState
import com.dkulikov2019.sshporttransfer.presentation.profile.ConnectionDiagnosticEntry
import com.dkulikov2019.sshporttransfer.presentation.profile.ConnectionDiagnosticLevel

@Composable
fun TunnelStatusCard(
    tunnelState: TunnelState,
    diagnostics: List<ConnectionDiagnosticEntry> = emptyList(),
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    val title = when (tunnelState) {
        TunnelState.Disconnected -> "Туннель отключен"
        is TunnelState.Connecting -> "Туннель подключается"
        is TunnelState.Connected -> "Туннель подключен"
        is TunnelState.Reconnecting -> "Повторное подключение"
        is TunnelState.Failed -> "Ошибка туннеля"
    }

    val subtitle = when (tunnelState) {
        TunnelState.Disconnected -> "Активных SSH-туннелей нет"
        is TunnelState.Connecting -> tunnelState.message
        is TunnelState.Connected -> "${tunnelState.localHost}:${tunnelState.localPort} → ${tunnelState.remoteHost}:${tunnelState.remotePort}"
        is TunnelState.Reconnecting -> "Попытка ${tunnelState.attempt}"
        is TunnelState.Failed -> tunnelState.message
    }
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (tunnelState is TunnelState.Connected) {
                Color(0xFFE8F5E9)
            } else {
                MaterialTheme.colorScheme.surfaceContainerHighest
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium)
            if (diagnostics.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Диагностика подключения",
                        style = MaterialTheme.typography.labelMedium
                    )
                    TextButton(
                        onClick = {
                            val report = diagnostics.joinToString("\n") { entry ->
                                "[${entry.time}] ${entry.message}"
                            }
                            clipboardManager.setText(AnnotatedString(report))
                        }
                    ) {
                        Text("Скопировать диагностику")
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(104.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        diagnostics.takeLast(5).forEach { entry ->
                            val messageColor = when (entry.level) {
                                ConnectionDiagnosticLevel.INFO -> MaterialTheme.colorScheme.onSurface
                                ConnectionDiagnosticLevel.SUCCESS -> MaterialTheme.colorScheme.primary
                                ConnectionDiagnosticLevel.WARNING -> MaterialTheme.colorScheme.tertiary
                                ConnectionDiagnosticLevel.ERROR -> MaterialTheme.colorScheme.error
                            }
                            Text(
                                text = "${entry.time}  ${entry.message}",
                                style = MaterialTheme.typography.bodySmall,
                                color = messageColor
                            )
                        }
                    }
                }
            }
        }
    }
}
