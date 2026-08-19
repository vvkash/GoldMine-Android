package com.goldmine.uncc.data.firebase

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.goldmine.uncc.MainActivity
import com.goldmine.uncc.R

/** Creates and posts the local notifications the iOS app raised via `UNUserNotificationCenter`. */
object NotificationHelper {

    const val FREEBIE_CHANNEL_ID = "freebies"

    fun ensureChannel(context: Context) {
        val channel = NotificationChannel(
            FREEBIE_CHANNEL_ID,
            context.getString(R.string.freebie_channel_name),
            NotificationManager.IMPORTANCE_HIGH,
        ).apply { description = context.getString(R.string.freebie_channel_description) }

        context.getSystemService(NotificationManager::class.java)
            ?.createNotificationChannel(channel)
    }

    fun canPostNotifications(context: Context): Boolean {
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) return false
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }

    // canPostNotifications() performs the POST_NOTIFICATIONS runtime check that lint cannot
    // follow across the helper call.
    @SuppressLint("MissingPermission")
    fun show(context: Context, id: String, title: String, body: String) {
        if (!canPostNotifications(context)) return
        ensureChannel(context)
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, FREEBIE_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        runCatching {
            NotificationManagerCompat.from(context).notify(id.hashCode(), notification)
        }
    }

    /** Mirrors the iOS "Free Stuff Alert!" local notification. */
    fun showFreebieConfirmed(context: Context, event: com.goldmine.uncc.data.model.FreebieEvent) {
        val dateString = java.text.SimpleDateFormat("MMMM d", java.util.Locale.US)
            .format(event.date)
        show(
            context = context,
            id = "freebie-${event.id}",
            title = "Free Stuff Alert!",
            body = "${event.company} at ${event.location.title} $dateString",
        )
    }

    /** Mirrors the iOS "Freebie No Longer Available" local notification. */
    fun showFreebieEnded(context: Context, event: com.goldmine.uncc.data.model.FreebieEvent) {
        show(
            context = context,
            id = "freebie-ended-${event.id}",
            title = "Freebie No Longer Available",
            body = "${event.company} at ${event.location.title} has been reported as no longer available.",
        )
    }
}
