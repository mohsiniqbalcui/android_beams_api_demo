package com.example.beamsapidemo

import android.util.Log

import com.google.firebase.messaging.RemoteMessage
import com.pusher.pushnotifications.fcm.MessagingService

class NotificationsMessagingService : MessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.i("MessagingService", "Remote message was received")
    }

    override fun onNewToken(token: String) {
        Log.i("MessagingService", "FCM token was changed")
    }
}