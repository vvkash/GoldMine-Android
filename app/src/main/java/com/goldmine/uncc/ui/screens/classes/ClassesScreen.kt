package com.goldmine.uncc.ui.screens.classes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
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
import com.goldmine.uncc.core.colorFromHex
import com.goldmine.uncc.core.formatMinutesOfDay
import com.goldmine.uncc.data.model.ClassItem
import com.goldmine.uncc.data.model.classesOn
import com.goldmine.uncc.ui.components.GoldMineHeader
import com.goldmine.uncc.ui.theme.GoldMineColors
import com.goldmine.uncc.ui.theme.LocalGoldMineColors
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

/** Schedule browser — the iOS `MainClassesView`. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassesScreen(
    classes: List<ClassItem>,
    onBack: () -> Unit,
    onAddClass: () -> Unit,
    onEditClass: (ClassItem) -> Unit,
    onDeleteClass: (String) -> Unit,
    onOpenMap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val extras = LocalGoldMineColors.current
    val today = remember { LocalDate.now() }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = today.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
    )

    val selectedDate = datePickerState.selectedDateMillis?.let { millis ->
        Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDate()
    } ?: today

    val visibleClasses = classes.classesOn(selectedDate).sortedBy { it.startMinutes }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(extras.screenBackground)
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()),
    ) {
        GoldMineHeader(title = "Classes", onBack = onBack, backLabel = "Done")

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = extras.cardBackground),
        ) {
            DatePicker(
                state = datePickerState,
                title = null,
                headline = null,
                showModeToggle = false,
            )
        }

        if (classes.isEmpty()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "No classes added yet",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = "Add your class schedule to see it here",
                    style = MaterialTheme.typography.bodyMedium,
                    color = extras.secondaryText,
                    modifier = Modifier.padding(bottom = 20.dp, top = 4.dp),
                )
                PrimaryActionButton(
                    text = "Add Class",
                    onClick = onAddClass,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (visibleClasses.isEmpty()) {
                    items(listOf(Unit)) {
                        Text(
                            text = "No classes on this day",
                            style = MaterialTheme.typography.bodyMedium,
                            color = extras.secondaryText,
                            modifier = Modifier.padding(vertical = 24.dp),
                        )
                    }
                }

                items(visibleClasses, key = ClassItem::id) { item ->
                    ClassRow(
                        item = item,
                        onClick = { onEditClass(item) },
                        onDelete = { onDeleteClass(item.id) },
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                PrimaryActionButton(
                    text = "View on Map",
                    onClick = onOpenMap,
                    modifier = Modifier.weight(1f),
                )
                PrimaryActionButton(
                    text = "Add Class",
                    onClick = onAddClass,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        Box(
            Modifier.height(
                WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 16.dp,
            ),
        )
    }
}

@Composable
private fun ClassRow(item: ClassItem, onClick: () -> Unit, onDelete: () -> Unit) {
    val extras = LocalGoldMineColors.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = extras.cardBackground),
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 8.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(colorFromHex(item.color)),
                    )
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                DetailLine(
                    icon = Icons.Filled.Apartment,
                    text = "${item.buildingName} ${item.roomNumber}".trim(),
                )
                DetailLine(
                    icon = Icons.Filled.Schedule,
                    text = "${formatMinutesOfDay(item.startMinutes)} - " +
                        formatMinutesOfDay(item.endMinutes),
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete ${item.name}",
                    tint = GoldMineColors.AccentRed,
                )
            }
        }
    }
}

@Composable
private fun DetailLine(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = GoldMineColors.AccentGray,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = GoldMineColors.AccentGray,
        )
    }
}

@Composable
fun PrimaryActionButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = GoldMineColors.CharlotteGreen,
            contentColor = Color.White,
        ),
        modifier = modifier.height(50.dp),
    ) {
        Text(text = text, style = MaterialTheme.typography.titleSmall)
    }
}
