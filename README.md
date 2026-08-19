# GoldMine UNCC — Android

A native Android port of the [GoldMine UNCC](https://github.com/vvkash/GoldMine-UNCC) iOS app
(SwiftUI + Firebase), rebuilt in **Kotlin + Jetpack Compose (Material 3)**.

It is feature-identical to the iOS app and **shares the same live Firestore backend**, so an
Android user posting a freebie appears instantly in the iOS feed and vice-versa.

---

## Feature parity map

| iOS (SwiftUI) | Android (Compose) | Notes |
| --- | --- | --- |
| `OnboardingView` | `ui/screens/onboarding/OnboardingScreen.kt` | Name capture, always-dark, pickaxe logo |
| `ContentView` / `MainTabView` | `ui/screens/MainTabScreen.kt` | Floating pill tab bar with gold indicator dot |
| `HomeView` | `ui/screens/home/HomeScreen.kt` | Greeting, live weather, ticker badge, 2-col shortcut grid |
| `GymStatusBadgeView` | `ui/screens/home/StatusBadges.kt` → `GymStatusBadge` | Canvas occupancy ring |
| `TodaysClassesBadgeView_New` | `ui/screens/home/StatusBadges.kt` → `TodaysClassesBadge` | Next-class countdown |
| `SocialEventsView` | `ui/screens/social/SocialScreen.kt` | Freebie feed, add sheet, map view |
| `EnergyDrinkService` | `data/firebase/FreebieRepository.kt` | Realtime listener + **transactional** voting |
| `MainClassesView` / `AddClassView` | `ui/screens/classes/ClassesScreen.kt`, `ClassFormScreen.kt` | Weekly scheduler |
| `CampusMapView` | `ui/screens/maps/CampusMapScreen.kt` | 35 campus pins on Google Maps |
| `SettingsView` | `ui/screens/settings/SettingsScreen.kt` | Profile, appearance, ticker, notifications |
| `PrivacyPolicy` | `ui/screens/settings/PrivacyPolicyScreen.kt` | Same copy |
| `EatsView` / `DiningMenuView` | `ui/screens/discounts/DiningScreen.kt` | Native venue list |
| `DiscountsView` | `ui/screens/discounts/DiscountsScreen.kt` | **Improvement:** renders the 20-entry `Discount` catalogue the iOS app never surfaced |
| `GymStatusView` (WebView) | `ui/screens/urec/UrecScreen.kt` | Native prediction ring **+** live connect2mycloud widget |
| `WebView.swift` | `ui/screens/web/WebScreen.kt` | 8 campus destinations |

### Deliberate improvements over iOS

1. **Server push actually fires.** The iOS `voteOnEvent` writes `hasNotifiedUsers = true` in the
   *same* update that crosses the 2-vote confirmation threshold, but the Cloud Function guard is
   `!afterData.hasNotifiedUsers` — so the fan-out notification never triggers from an iOS vote.
   Android does **not** set that flag, letting `firebase-functions/index.js` fire as designed.
2. **Concurrency-safe voting** via Firestore transactions (iOS uses read-modify-write, which can
   silently drop simultaneous votes).
3. **Tri-state dark mode** (`Light` / `Dark` / `Follow system`); iOS is binary.
4. **Timezone-stable class times** — stored as minutes-of-day integers rather than absolute dates.
5. **Graceful Firebase degradation** — with an unconfigured `google-services.json` the app still
   runs; only the social feed shows an "unavailable" state instead of crashing.

---

## Backend compatibility contract

Do **not** change these shapes — iOS clients read the same documents.

`energyDrinkEvents/{autoId}`

| Field | Type |
| --- | --- |
| `id` | String (uppercase UUID) |
| `company` | String |
| `location` | Map `{ id, title, latitude, longitude }` |
| `votes` | Number |
| `noVotes` | Number |
| `date` | Timestamp |
| `isEnded` | Bool |
| `hasNotifiedUsers` | Bool |
| `votedUserIds` | Array&lt;String&gt; |
| `noVotedUserIds` | Array&lt;String&gt; |

`fcmTokens/{deviceId}` → `token`, `userName`, `deviceId`, `platform` (`"android"`),
`energyDrinkNotifications`, `updatedAt`.

Enforced by `data/model/FreebieEvent.kt` (`toMap()` / `fromSnapshot()`).

---

## Setup

### 1. Prerequisites

* Android Studio Ladybug or newer
* JDK 17+ (Android Studio's bundled JBR works)
* Android SDK Platform 35

### 2. Register the Android app in Firebase

The repo ships a **template** `app/google-services.json` with the correct project
(`goldmineuncc`, sender `215102035656`) but placeholder keys.

1. Firebase Console → project **goldmineuncc** → *Add app* → Android.
2. Register **two** package names so debug and release can be installed side-by-side:
   * `com.goldmine.uncc`
   * `com.goldmine.uncc.debug`
3. Download the generated `google-services.json` and replace `app/google-services.json`.

Until you do this, the app builds and runs but the social feed reports "unavailable"
(see `data/firebase/FirebaseAvailability.kt`).

### 3. API keys

Add to `local.properties` (git-ignored — **never commit these**):

```properties
sdk.dir=/Users/you/Library/Android/sdk

# https://openweathermap.org/api  (same key the iOS app uses)
OPENWEATHER_API_KEY=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx

# Google Cloud Console -> Maps SDK for Android
MAPS_API_KEY=AIzaSyXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
```

Both are injected at build time (`BuildConfig.OPENWEATHER_API_KEY` and the
`${MAPS_API_KEY}` manifest placeholder).

### 4. Build

```bash
./gradlew assembleDebug        # debug APK
./gradlew bundleRelease        # Play Store AAB
```

---

## Release signing

Generate an upload keystore once:

```bash
keytool -genkeypair -v \
  -keystore goldmine-upload.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias goldmine
```

Then add to `local.properties`:

```properties
RELEASE_STORE_FILE=goldmine-upload.jks
RELEASE_STORE_PASSWORD=...
RELEASE_KEY_ALIAS=goldmine
RELEASE_KEY_PASSWORD=...
```

`app/build.gradle.kts` only wires the signing config when the keystore actually resolves, so
unsigned CI builds still succeed.

**Keep `goldmine-upload.jks` out of git** (already covered by `.gitignore`) and back it up —
losing it means you cannot ship updates without a Play key reset.

---

## Play Store checklist

- [ ] Real `google-services.json` in `app/`
- [ ] `MAPS_API_KEY` restricted to the app's SHA-1 + package name in Google Cloud Console
- [ ] Bump `versionCode` / `versionName` in `app/build.gradle.kts`
- [ ] `./gradlew bundleRelease` → upload `app/build/outputs/bundle/release/app-release.aab`
- [ ] Data safety form: declare **approximate location** (freebie posting) and **device ID** (FCM token)
- [ ] Link the privacy policy (mirrors `PrivacyPolicyScreen.kt`)
- [ ] Content rating questionnaire
- [ ] Target audience: 13+
- [ ] Phone + 7" / 10" tablet screenshots

---

## Architecture

```
com.goldmine.uncc
├── GoldMineApplication.kt      notification channel bootstrap
├── MainActivity.kt             edge-to-edge host, theme resolution
├── core/                       colour + time helpers
├── data/
│   ├── model/                  ClassItem, FreebieEvent, HomeButton, Discount, campus buildings…
│   ├── local/                  DataStore-backed UserPreferencesRepository
│   ├── remote/                 OpenWeather (Retrofit) + gym occupancy calculator
│   └── firebase/               FreebieRepository, FCM service, notification helpers
└── ui/
    ├── theme/                  Charlotte green / Niner gold Material 3 palette
    ├── components/             shared cards, headers, WebView wrapper
    ├── navigation/             Routes + WebDestination
    ├── AppViewModel.kt         app-scoped AppState
    ├── GoldMineApp.kt          NavHost
    └── screens/                onboarding, home, social, classes, maps, settings, …
```

**Pattern:** unidirectional data flow — repositories expose `Flow`, ViewModels expose
`StateFlow<UiState>`, Compose screens are stateless and receive callbacks.

---

## Minimum requirements

* **minSdk 26** (Android 8.0) — covers ~98% of active devices
* **targetSdk / compileSdk 35** (Android 15)
