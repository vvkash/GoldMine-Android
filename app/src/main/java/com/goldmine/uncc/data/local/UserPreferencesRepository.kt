package com.goldmine.uncc.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.goldmine.uncc.data.model.AppTickerType
import com.goldmine.uncc.data.model.ClassItem
import com.goldmine.uncc.data.model.HomeButton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Persists everything the iOS app kept in `UserDefaults` / `@AppStorage`:
 * profile, appearance, ticker choice, notification toggles, saved classes and home layout.
 */
class UserPreferencesRepository(context: Context) {

    private val dataStore = context.applicationContext.goldMineDataStore

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private object Keys {
        val USER_NAME = stringPreferencesKey("userName")
        val HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("hasCompletedOnboarding")
        val IS_DARK_MODE = booleanPreferencesKey("isDarkMode")
        val HAS_SET_DARK_MODE = booleanPreferencesKey("hasSetDarkMode")
        val DEFAULT_TICKER_TYPE = stringPreferencesKey("defaultTickerType")
        val FREEBIE_NOTIFICATIONS = booleanPreferencesKey("energyDrinkNotificationsEnabled")
        val CLUBS_NOTIFICATIONS = booleanPreferencesKey("clubsNotificationsEnabled")
        val MEETUPS_NOTIFICATIONS = booleanPreferencesKey("meetUpsNotificationsEnabled")
        val SAVED_CLASSES = stringPreferencesKey("savedClasses")
        val HOME_BUTTONS = stringPreferencesKey("homeButtonVisibility")
    }

    val userName: Flow<String> = dataStore.data.map { it[Keys.USER_NAME].orEmpty() }

    val hasCompletedOnboarding: Flow<Boolean> =
        dataStore.data.map { it[Keys.HAS_COMPLETED_ONBOARDING] ?: false }

    /** `null` means "follow the system setting" — the iOS app had no such option. */
    val darkModeOverride: Flow<Boolean?> = dataStore.data.map { prefs ->
        if (prefs[Keys.HAS_SET_DARK_MODE] == true) prefs[Keys.IS_DARK_MODE] ?: true else null
    }

    val defaultTickerType: Flow<AppTickerType> =
        dataStore.data.map { AppTickerType.fromRaw(it[Keys.DEFAULT_TICKER_TYPE]) }

    val freebieNotificationsEnabled: Flow<Boolean> =
        dataStore.data.map { it[Keys.FREEBIE_NOTIFICATIONS] ?: false }

    val clubsNotificationsEnabled: Flow<Boolean> =
        dataStore.data.map { it[Keys.CLUBS_NOTIFICATIONS] ?: false }

    val meetUpsNotificationsEnabled: Flow<Boolean> =
        dataStore.data.map { it[Keys.MEETUPS_NOTIFICATIONS] ?: false }

    val classes: Flow<List<ClassItem>> = dataStore.data.map { prefs ->
        prefs[Keys.SAVED_CLASSES]?.let { raw ->
            runCatching { json.decodeFromString<List<ClassItem>>(raw) }.getOrNull()
        } ?: emptyList()
    }

    /**
     * Home shortcuts, sorted by order. New shortcuts shipped in later app versions are appended
     * to an existing saved layout instead of resetting it — same reconciliation the iOS
     * `ButtonVisibilityManager` performs on launch.
     */
    val homeButtons: Flow<List<HomeButton>> = dataStore.data.map { prefs ->
        val saved = prefs[Keys.HOME_BUTTONS]?.let { raw ->
            runCatching { json.decodeFromString<List<HomeButton>>(raw) }.getOrNull()
        } ?: return@map HomeButton.defaults

        val savedIds = saved.map { it.id }.toSet()
        val missing = HomeButton.defaults.filter { it.id !in savedIds }
        (saved + missing).sortedBy { it.order }
    }

    suspend fun setUserName(name: String) = dataStore.edit { it[Keys.USER_NAME] = name }

    suspend fun setHasCompletedOnboarding(value: Boolean) =
        dataStore.edit { it[Keys.HAS_COMPLETED_ONBOARDING] = value }

    suspend fun setDarkMode(enabled: Boolean) = dataStore.edit {
        it[Keys.IS_DARK_MODE] = enabled
        it[Keys.HAS_SET_DARK_MODE] = true
    }

    suspend fun useSystemAppearance() = dataStore.edit {
        it.remove(Keys.HAS_SET_DARK_MODE)
        it.remove(Keys.IS_DARK_MODE)
    }

    suspend fun setDefaultTickerType(type: AppTickerType) =
        dataStore.edit { it[Keys.DEFAULT_TICKER_TYPE] = type.rawValue }

    suspend fun setFreebieNotificationsEnabled(enabled: Boolean) =
        dataStore.edit { it[Keys.FREEBIE_NOTIFICATIONS] = enabled }

    suspend fun setClubsNotificationsEnabled(enabled: Boolean) =
        dataStore.edit { it[Keys.CLUBS_NOTIFICATIONS] = enabled }

    suspend fun setMeetUpsNotificationsEnabled(enabled: Boolean) =
        dataStore.edit { it[Keys.MEETUPS_NOTIFICATIONS] = enabled }

    suspend fun addClass(item: ClassItem) = updateClasses { it + item }

    suspend fun updateClass(item: ClassItem) = updateClasses { current ->
        current.map { if (it.id == item.id) item else it }
    }

    suspend fun deleteClass(id: String) = updateClasses { current -> current.filterNot { it.id == id } }

    suspend fun toggleHomeButton(buttonId: String) = updateHomeButtons { buttons ->
        buttons.map { if (it.id == buttonId) it.copy(isVisible = !it.isVisible) else it }
    }

    suspend fun resetHomeButtons() = updateHomeButtons { HomeButton.defaults }

    /** Reorders the visible shortcuts, keeping hidden ones anchored around them. */
    suspend fun moveHomeButton(fromVisibleIndex: Int, toVisibleIndex: Int) = updateHomeButtons { buttons ->
        val ordered = buttons.sortedBy { it.order }
        val visible = ordered.filter { it.isVisible }.toMutableList()
        if (fromVisibleIndex !in visible.indices || toVisibleIndex !in visible.indices) {
            return@updateHomeButtons ordered
        }
        val moved = visible.removeAt(fromVisibleIndex)
        visible.add(toVisibleIndex, moved)

        val hidden = ordered.filter { !it.isVisible }
        (visible + hidden).mapIndexed { index, button -> button.copy(order = index) }
    }

    private suspend fun updateClasses(transform: (List<ClassItem>) -> List<ClassItem>) {
        val current = classes.first()
        dataStore.edit { it[Keys.SAVED_CLASSES] = json.encodeToString(transform(current)) }
    }

    private suspend fun updateHomeButtons(transform: (List<HomeButton>) -> List<HomeButton>) {
        val current = homeButtons.first()
        dataStore.edit { it[Keys.HOME_BUTTONS] = json.encodeToString(transform(current)) }
    }
}
