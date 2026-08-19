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
        "Gold Mine UNCC is designed to provide UNC Charlotte students with easy access to " +
            "university resources. We take your privacy seriously.",
    ),
    PolicySection(
        "Data Collection",
        "We do not collect, store, or track personal data. The app does not track users across " +
            "apps and websites owned by other companies.",
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
        "Notifications & Community Reports",
        "If you enable freebie notifications, your device's push token and the display name " +
            "you chose are stored so alerts can be delivered. Freebie reports you submit are " +
            "shared with other GoldMine users. You can turn notifications off at any time in " +
            "Settings.",
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
