package com.goldmine.uncc.data.firebase

import androidx.datastore.preferences.core.booleanPreferencesKey
import com.goldmine.uncc.data.local.goldMineDataStore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/** Receives the pushes fanned out by the project's existing Cloud Functions. */
class GoldMineMessagingService : FirebaseMessagingService() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        scope.launch { PushNotificationManager(applicationContext).onTokenRefreshed(token) }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title ?: message.data["title"] ?: return
        val body = message.notification?.body ?: message.data["body"].orEmpty()
        val id = message.data["eventId"] ?: message.messageId ?: title

        scope.launch {
            val enabled = applicationContext.goldMineDataStore.data.first()[FREEBIE_NOTIFICATIONS]
                ?: false
            if (!enabled) return@launch
            NotificationHelper.show(applicationContext, id, title, body)
        }
    }

    private companion object {
        val FREEBIE_NOTIFICATIONS = booleanPreferencesKey("energyDrinkNotificationsEnabled")
    }
}
