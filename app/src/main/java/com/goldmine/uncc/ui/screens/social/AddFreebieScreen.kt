package com.goldmine.uncc.ui.screens.social

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.goldmine.uncc.data.model.FreebieEvent
import com.goldmine.uncc.data.model.MapLocation
import com.goldmine.uncc.ui.components.GoldMineHeader
import com.goldmine.uncc.ui.theme.GoldMineColors
import com.goldmine.uncc.ui.theme.LocalGoldMineColors
import java.util.Date

/** Report form for a new freebie — the iOS `AddEnergyDrinkEventView`. */
@Composable
fun AddFreebieScreen(
    isSubmitting: Boolean,
    onCancel: () -> Unit,
    onSubmit: (FreebieEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val extras = LocalGoldMineColors.current
    var company by remember { mutableStateOf("") }
    var location by remember { mutableStateOf<MapLocation?>(null) }
    var pickingLocation by remember { mutableStateOf(false) }

    if (pickingLocation) {
        LocationPickerScreen(
            onCancel = { pickingLocation = false },
            onConfirm = {
                location = it
                pickingLocation = false
            },
        )
        return
    }

    val isValid = company.isNotBlank() && location != null && !isSubmitting

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(extras.screenBackground)
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()),
    ) {
        GoldMineHeader(title = "Add Freebie", onBack = onCancel, backLabel = "Cancel")

        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Company",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                OutlinedTextField(
                    value = company,
                    onValueChange = { company = it },
                    singleLine = true,
                    placeholder = { Text("Enter company name") },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldMineColors.CharlotteGreen,
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                    ),
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Location",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (extras.isDark) Color.Black.copy(alpha = 0.6f) else Color.White,
                        )
                        .border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .clickable { pickingLocation = true }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = location?.title ?: "Select location on map",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (location == null) {
                            Color.Gray
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        modifier = Modifier.weight(1f),
                    )
                    Icon(
                        imageVector = Icons.Filled.Map,
                        contentDescription = null,
                        tint = GoldMineColors.CharlotteGreen,
                    )
                }
            }
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = {
                val picked = location ?: return@Button
                onSubmit(
                    FreebieEvent(
                        company = company.trim(),
                        location = picked,
                        votes = 1,
                        date = Date(),
                    ),
                )
            },
            enabled = isValid,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = GoldMineColors.CharlotteGreen,
                contentColor = Color.White,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.White,
            ),
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .height(52.dp),
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.height(20.dp),
                )
            } else {
                Text("Add Event", style = MaterialTheme.typography.titleSmall)
            }
        }

        Box(
            Modifier.height(
                WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 30.dp,
            ),
        )
    }
}
