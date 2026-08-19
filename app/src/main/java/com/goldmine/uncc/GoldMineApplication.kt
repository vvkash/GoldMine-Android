package com.goldmine.uncc

import android.app.Application
import com.goldmine.uncc.data.firebase.FirebaseAvailability
import com.goldmine.uncc.data.firebase.NotificationHelper

class GoldMineApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.ensureChannel(this)
        // Warms the Firebase check once, off the UI critical path.
        FirebaseAvailability.isAvailable(this)
    }
}
