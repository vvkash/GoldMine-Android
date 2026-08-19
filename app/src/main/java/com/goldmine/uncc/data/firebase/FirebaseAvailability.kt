package com.goldmine.uncc.data.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp

/**
 * Guards every Firebase touch-point so the app still runs (with the social feed disabled)
 * when `google-services.json` has not been swapped in for a real one yet.
 */
object FirebaseAvailability {

    private const val TAG = "FirebaseAvailability"

    @Volatile
    private var cached: Boolean? = null

    fun isAvailable(context: Context): Boolean = cached ?: synchronized(this) {
        cached ?: runCatching {
            val app = FirebaseApp.getApps(context.applicationContext).firstOrNull()
                ?: FirebaseApp.initializeApp(context.applicationContext)
            val options = app?.options
            val configured = options != null &&
                options.projectId.isNullOrBlank().not() &&
                options.apiKey.isNullOrBlank().not() &&
                options.applicationId.isNullOrBlank().not() &&
                options.apiKey.startsWith(PLACEHOLDER_PREFIX).not()
            if (!configured) {
                Log.w(TAG, "Firebase is not configured; social features will be unavailable.")
            }
            configured
        }.onFailure { Log.w(TAG, "Firebase initialization failed", it) }
            .getOrDefault(false)
            .also { cached = it }
    }

    /** Placeholder keys in the checked-in template start with this marker. */
    private const val PLACEHOLDER_PREFIX = "REPLACE_WITH"
}
