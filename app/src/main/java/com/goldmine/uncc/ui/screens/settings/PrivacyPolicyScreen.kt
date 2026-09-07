package com.goldmine.uncc.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goldmine.uncc.ui.components.GoldMineHeader
import com.goldmine.uncc.ui.theme.LocalGoldMineColors

private data class PolicySection(val heading: String, val body: String)

private val POLICY_SECTIONS = listOf(
    PolicySection(
        "Our Commitment to Privacy",
        "GoldMine UNCC is an independent student-built campus companion and is not affiliated " +
            "with or endorsed by UNC Charlotte. This notice explains what data the app handles.",
    ),
    PolicySection(
        "Information Stored on Your Device",
        "Your display name, class schedule, appearance settings, home-screen layout, notification " +
            "choices, and a random app-install identifier are stored locally. Class schedules and " +
            "layout preferences are not uploaded to GoldMine's Firebase backend. Android backup " +
            "and device transfer may copy this local data according to your device settings.",
    ),
    PolicySection(
        "Community and Notification Data",
        "GoldMine uses Google Firebase. Your display name can be stored with votes and, if push " +
            "notifications are configured, with a Firebase messaging token, random install " +
            "identifier, platform, and notification preference. Freebie reports include the " +
            "submitted company, selected location name and coordinates, time, status, and votes. " +
            "Reports are visible to other GoldMine users.",
    ),
    PolicySection(
        "Location",
        "Location permission is optional and is requested only when you choose My Location on the " +
            "campus map. Google Maps may process device location to provide that feature. A " +
            "freebie report uploads the campus location you select on the map; it does not require " +
            "your live device location.",
    ),
    PolicySection(
        "Analytics and Service Providers",
        "Firebase Analytics may process app interactions, device or app identifiers, and " +
            "diagnostic information. Google provides Firebase, Cloud Messaging, Firestore, and " +
            "Maps services and processes data under its own terms. GoldMine does not sell personal " +
            "information or use it for cross-app advertising.",
    ),
    PolicySection(
        "WebView and Cookies",
        "The app includes WebViews that display official university websites. While these " +
            "websites may use cookies according to their own policies, we do not collect or " +
            "process any cookies or tracking information from these sites. We do not track " +
            "users through these WebViews.",
    ),
    PolicySection(
        "Third-Party Content",
        "When you access university websites through our app (such as maps.charlotte.edu, " +
            "charlotte49ers.com, etc.), you are subject to those websites' own privacy " +
            "policies. We do not control these third-party sites or use their data for " +
            "tracking purposes.",
    ),
    PolicySection(
        "Your Choices and Contact",
        "You can deny location or notification permission and turn freebie notifications off in " +
            "Settings. To request deletion of community, notification, or install data, open an " +
            "issue at github.com/vvkash/GoldMine-Android/issues. Include only the information " +
            "needed to identify the data; do not post sensitive information publicly.",
    ),
)

/** Privacy policy — the iOS `PrivacyPolicyView`. */
@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val extras = LocalGoldMineColors.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(extras.screenBackground)
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()),
    ) {
        GoldMineHeader(title = "Privacy Policy", onBack = onBack, backLabel = "Done")

        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp,
                bottom = WindowInsets.navigationBars.asPaddingValues()
                    .calculateBottomPadding() + 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            items(POLICY_SECTIONS) { section ->
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = section.heading,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Text(
                        text = section.body,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
        }
    }
}
