package com.dkulikov2019.sshporttransfer.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

class TunnelForegroundService : Service() {

    private lateinit var notificationFactory: TunnelNotificationFactory

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
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
