package com.goldmine.uncc.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.goldmine.uncc.data.firebase.PushNotificationManager
import com.goldmine.uncc.data.local.UserPreferencesRepository
import com.goldmine.uncc.data.model.AppTickerType
import com.goldmine.uncc.data.model.ClassItem
import com.goldmine.uncc.data.model.HomeButton
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Everything the shell (theme, onboarding gate, settings, classes) needs in one place. */
data class AppState(
    val loaded: Boolean = false,
    val userName: String = "",
    val hasCompletedOnboarding: Boolean = false,
    val darkModeOverride: Boolean? = null,
    val defaultTickerType: AppTickerType = AppTickerType.UREC_STATUS,
    val freebieNotificationsEnabled: Boolean = false,
    val clubsNotificationsEnabled: Boolean = false,
    val meetUpsNotificationsEnabled: Boolean = false,
    val classes: List<ClassItem> = emptyList(),
    val homeButtons: List<HomeButton> = HomeButton.defaults,
)

private data class Profile(
    val userName: String,
    val hasCompletedOnboarding: Boolean,
    val darkModeOverride: Boolean?,
    val tickerType: AppTickerType,
)

private data class NotificationPrefs(
    val freebies: Boolean,
    val clubs: Boolean,
    val meetUps: Boolean,
)

/**
 * Application-scoped view model, replacing the iOS `@EnvironmentObject` trio
 * (`UserSettings`, `ClassManager`, `PushNotificationManager`).
 */
class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = UserPreferencesRepository(application)
    private val push = PushNotificationManager(application)

    private val profileFlow = combine(
        prefs.userName,
        prefs.hasCompletedOnboarding,
        prefs.darkModeOverride,
        prefs.defaultTickerType,
        ::Profile,
    )

    private val notificationsFlow = combine(
        prefs.freebieNotificationsEnabled,
        prefs.clubsNotificationsEnabled,
        prefs.meetUpsNotificationsEnabled,
        ::NotificationPrefs,
    )

    val state: StateFlow<AppState> = combine(
        profileFlow,
        notificationsFlow,
        prefs.classes,
        prefs.homeButtons,
    ) { profile, notifications, classes, buttons ->
        AppState(
            loaded = true,
            userName = profile.userName,
            hasCompletedOnboarding = profile.hasCompletedOnboarding,
            darkModeOverride = profile.darkModeOverride,
            defaultTickerType = profile.tickerType,
            freebieNotificationsEnabled = notifications.freebies,
            clubsNotificationsEnabled = notifications.clubs,
            meetUpsNotificationsEnabled = notifications.meetUps,
            classes = classes,
            homeButtons = buttons,
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, AppState())

    fun completeOnboarding(name: String) = viewModelScope.launch {
        prefs.setUserName(name.trim())
        prefs.setHasCompletedOnboarding(true)
    }

    fun setUserName(name: String) = viewModelScope.launch { prefs.setUserName(name.trim()) }

    fun setDarkMode(enabled: Boolean) = viewModelScope.launch { prefs.setDarkMode(enabled) }

    fun useSystemAppearance() = viewModelScope.launch { prefs.useSystemAppearance() }

    fun setTickerType(type: AppTickerType) =
        viewModelScope.launch { prefs.setDefaultTickerType(type) }

    fun setFreebieNotifications(enabled: Boolean) = viewModelScope.launch {
        prefs.setFreebieNotificationsEnabled(enabled)
        push.setFreebieNotificationsEnabled(enabled, prefs.userName.first())
    }

    fun setClubsNotifications(enabled: Boolean) =
        viewModelScope.launch { prefs.setClubsNotificationsEnabled(enabled) }

    fun setMeetUpsNotifications(enabled: Boolean) =
        viewModelScope.launch { prefs.setMeetUpsNotificationsEnabled(enabled) }

    fun toggleHomeButton(id: String) = viewModelScope.launch { prefs.toggleHomeButton(id) }

    fun resetHomeButtons() = viewModelScope.launch { prefs.resetHomeButtons() }

    fun moveHomeButton(from: Int, to: Int) =
        viewModelScope.launch { prefs.moveHomeButton(from, to) }

    fun addClass(item: ClassItem) = viewModelScope.launch { prefs.addClass(item) }

    fun updateClass(item: ClassItem) = viewModelScope.launch { prefs.updateClass(item) }

    fun deleteClass(id: String) = viewModelScope.launch { prefs.deleteClass(id) }

    /** Re-registers this device so the Cloud Functions can reach it. */
    fun syncPushToken() = viewModelScope.launch {
        push.syncToken(prefs.userName.first(), prefs.freebieNotificationsEnabled.first())
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(
                    extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY],
                )
                return AppViewModel(application) as T
            }
        }
    }
}
