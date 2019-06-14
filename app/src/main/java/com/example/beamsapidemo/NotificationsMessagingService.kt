package com.example.beamsapidemo

import android.util.Log

import com.google.firebase.messaging.RemoteMessage
import com.pusher.pushnotifications.fcm.MessagingService

class NotificationsMessagingService : MessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.i("MessagingService", "Remote message was received")
    }

    // This method is only for integrating with other 3rd party services.
    // For most use cases you can omit it.
    override fun onNewToken(token: String) {
        Log.i("MessagingService", "FCM token was changed")
    }
}