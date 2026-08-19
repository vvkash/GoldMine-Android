package com.goldmine.uncc.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

/** Single DataStore instance shared by every repository (replaces iOS `UserDefaults.standard`). */
val Context.goldMineDataStore: DataStore<Preferences> by preferencesDataStore(name = "goldmine_prefs")
