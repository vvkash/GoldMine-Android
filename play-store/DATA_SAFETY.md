# Data safety working draft

This is a code-based working draft, not a substitute for reviewing the exact production Firebase,
Google Maps, WebView, and analytics configurations before submitting Play Console declarations.

## Data handled by first-party app code

| Play data category | Example | Collected off device? | Shared? | Purpose |
| --- | --- | --- | --- | --- |
| Personal info — Name | User-selected display name | Yes, with votes and FCM registration | Visible to backend operators; vote identity is stored in shared event records | App functionality, notifications |
| Location — Precise location | Coordinates chosen for a freebie report | Yes | Report location is visible to other users | App functionality |
| Device or other identifiers | Random install ID and FCM registration token | Yes | Sent to Google Firebase | App functionality, notifications |
| User-generated content — Other | Company, location title, coordinates, and freebie status | Yes | Visible to other users | App functionality |
| App activity — Other user-generated content/actions | Freebie votes and end-report actions | Yes | Stored in shared event records | App functionality, fraud/abuse prevention |

The locally saved class schedule, home layout, appearance choice, and most preferences remain on
the device in Android DataStore. The app does not upload them to Firebase, but Android cloud
backup and device transfer can copy them according to system settings.

## SDK disclosures to verify

The release includes Firebase Analytics, Firestore, Cloud Messaging, Google Maps, Google Play
services location, Retrofit/OkHttp, and WebViews. Use the current vendor disclosure pages when
answering Play Console because SDK behavior can change independently of first-party code.

At minimum, verify whether Firebase Analytics causes these additional declarations:

* Device or other identifiers
* App interactions
* Diagnostics

Also verify Google Maps processing for the optional My Location feature and the privacy practices
of university pages opened inside WebViews.

## Security and user controls

* Network calls use HTTPS/TLS endpoints.
* Location and notifications are optional runtime permissions.
* Freebie notifications can be disabled in Settings.
* There is no user account system, but backend records can still contain a display name and install
  identifier. The public privacy policy provides a deletion-request contact route.

## Before submission

1. Replace the deletion/contact route with a monitored support email if possible.
2. Confirm Firebase retention and deletion procedures.
3. Confirm whether production Firebase Analytics collection remains enabled. If it is unnecessary,
   remove the Analytics dependency before release to reduce collection and disclosure scope.
4. Review every active AAB, not only current source code.
5. Ensure the Play Console answers and public privacy policy describe the same behavior.
