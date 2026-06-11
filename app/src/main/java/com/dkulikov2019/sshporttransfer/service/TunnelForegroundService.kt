package com.dkulikov2019.sshporttransfer.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.dkulikov2019.sshporttransfer.domain.usecase.DisconnectTunnelUseCase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TunnelForegroundService : Service() {

    @Inject
    lateinit var disconnectTunnelUseCase: DisconnectTunnelUseCase

    private lateinit var notificationFactory: TunnelNotificationFactory
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        notificationFactory = TunnelNotificationFactory(this)
        notificationFactory.createChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            TunnelServiceAction.START -> {
                startForeground(
                    TunnelNotificationFactory.NOTIFICATION_ID,
                    notificationFactory.buildConnectedNotification()
                )
            }
            TunnelServiceAction.STOP -> {
                serviceScope.launch {
                    disconnectTunnelUseCase()
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
