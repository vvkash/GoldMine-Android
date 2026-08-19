package com.goldmine.uncc.data.firebase

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.goldmine.uncc.data.local.goldMineDataStore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * Keeps this device's FCM registration token in the shared `fcmTokens` collection.
 *
 * The document shape matches the iOS `PushNotificationManager` (with `platform = "android"`),
 * so the existing Cloud Functions fan notifications out to iOS and Android subscribers alike.
 */
class PushNotificationManager(context: Context) {

    private val appContext = context.applicationContext
    private val dataStore = appContext.goldMineDataStore

    private val firestore: FirebaseFirestore?
        get() = if (FirebaseAvailability.isAvailable(appContext)) {
            runCatching { FirebaseFirestore.getInstance() }.getOrNull()
        } else {
            null
        }

    /** Stable, app-scoped install id — the Android equivalent of `identifierForVendor`. */
    suspend fun deviceId(): String {
        dataStore.data.first()[DEVICE_ID_KEY]?.let { return it }
        val generated = UUID.randomUUID().toString()
        dataStore.edit { it[DEVICE_ID_KEY] = generated }
        return generated
    }

    suspend fun currentToken(): String? = runCatching {
        if (!FirebaseAvailability.isAvailable(appContext)) return null
        FirebaseMessaging.getInstance().token.await()
    }.onFailure { Log.w(TAG, "Could not fetch FCM token", it) }.getOrNull()

    /** Pushes the current token plus the user's notification preference to Firestore. */
    suspend fun syncToken(userName: String, freebieNotificationsEnabled: Boolean) {
        val db = firestore ?: return
        val token = currentToken() ?: return
        saveToken(db, token, userName, freebieNotificationsEnabled)
    }

    /** Called by [GoldMineMessagingService] when Firebase rotates the token. */
    suspend fun onTokenRefreshed(token: String) {
        val db = firestore ?: return
        val prefs = dataStore.data.first()
        val userName = prefs[USER_NAME_KEY].orEmpty()
        val enabled = prefs[FREEBIE_NOTIFICATIONS_KEY] ?: false
        saveToken(db, token, userName, enabled)
    }

    suspend fun setFreebieNotificationsEnabled(enabled: Boolean, userName: String) {
        val db = firestore ?: return
        val token = currentToken() ?: return
        saveToken(db, token, userName, enabled)
    }

    private suspend fun saveToken(
        db: FirebaseFirestore,
        token: String,
        userName: String,
        freebieNotificationsEnabled: Boolean,
    ) {
        val deviceId = deviceId()
        val payload = mapOf(
            "token" to token,
            "userName" to userName,
            "deviceId" to deviceId,
            "platform" to "android",
            "energyDrinkNotifications" to freebieNotificationsEnabled,
            "updatedAt" to FieldValue.serverTimestamp(),
        )

        runCatching {
            db.collection(COLLECTION).document(deviceId).set(payload, SetOptions.merge()).await()
        }.onFailure { Log.w(TAG, "Could not save FCM token", it) }
    }

    companion object {
        private const val TAG = "PushNotificationManager"
        private const val COLLECTION = "fcmTokens"
        private val DEVICE_ID_KEY = stringPreferencesKey("deviceId")
        private val USER_NAME_KEY = stringPreferencesKey("userName")
        private val FREEBIE_NOTIFICATIONS_KEY =
            booleanPreferencesKey("energyDrinkNotificationsEnabled")
    }
}
