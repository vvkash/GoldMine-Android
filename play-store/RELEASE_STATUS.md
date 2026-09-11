# Release preparation

The app is not yet published. The local release bundle uses placeholder Firebase configuration
and is unsigned. Do not upload that artifact as a production release.

## Prepared

Verified September 6, 2026: `testDebugUnitTest lintRelease assembleDebug bundleRelease` completed
successfully; all 34 tests passed and release lint reported 0 errors / 29 dependency-version
warnings. The rebuilt debug APK installed and launched on API 37 with no AndroidRuntime crash in
the inspected log. `jarsigner -verify` confirms the AAB is unsigned. `verifyPlayRelease` correctly
fails on the missing production inputs. This is not a full release-device or live-service test.

- Target API 36, compile SDK 36.1, AGP 8.13.0, Gradle 8.13.
- Store icon and clean 1024×500 feature graphic.
- Four phone screenshot candidates in `screenshots/`, captured on an API 37 emulator.
- Store description and Data safety working draft.
- Privacy HTML in `docs/privacy-policy.html`; operator review is still required.
- `./gradlew :app:preparePlayRelease` checks configuration and runs release checks.

## Owner inputs and external verification

1. Register `com.goldmine.uncc` and `com.goldmine.uncc.debug` in Firebase project `goldmineuncc`.
   Supply the downloaded config at `app/google-services.json`. Do not send service-account private keys.
2. Set Maps and OpenWeather keys in ignored `local.properties`. Enable Maps SDK for Android and
   restrict its key to the proper package/certificate combinations.
3. Create and back up an upload keystore and set all four RELEASE_* properties documented in README.
4. Review the privacy draft, supply a monitored private support/deletion contact, confirm retention
   practices, and publish the policy. The proposed GitHub Pages URL is not live until Pages is enabled.
5. Review live Firestore rules and backend notification functions. The Android repo has no Firebase
   Authentication integration; confirm the existing rules protect writes, tokens, and user data.
6. Confirm the moderation/reporting controls for public community submissions before launch.
7. Review the hardcoded dining and discount information for current accuracy before using those screenshots.
8. Build using `./gradlew :app:preparePlayRelease`, then test a Play-installed internal release:
   map authorization, social posting/voting across platforms, notifications, permissions, offline
   behavior, and upgrade persistence. Test supported older Android versions too.
9. Complete Play Console account verification, app declarations, content rating, Data safety,
   store listing, and any account-specific closed-testing requirement. Add the Play app-signing
   certificate SHA-1 to the Maps key restriction.

The generated privacy text and Data safety draft describe observed app code; they do not establish
the operator's backend retention, deletion, moderation, or vendor configuration practices.
