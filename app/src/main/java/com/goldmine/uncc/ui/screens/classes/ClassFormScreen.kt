package com.goldmine.uncc.ui.screens.classes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.goldmine.uncc.data.model.CampusBuildings
import com.goldmine.uncc.data.model.ClassItem
import com.goldmine.uncc.data.model.Weekday
import com.goldmine.uncc.ui.components.GoldMineHeader
import com.goldmine.uncc.ui.theme.ClassColorSwatches
import com.goldmine.uncc.ui.theme.GoldMineColors
import com.goldmine.uncc.ui.theme.LocalGoldMineColors
import java.util.UUID

/** Add / edit form — the iOS `addClassFormView` and `editClassFormView`. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassFormScreen(
    existing: ClassItem?,
    onCancel: () -> Unit,
    onSave: (ClassItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val extras = LocalGoldMineColors.current

    var name by remember { mutableStateOf(existing?.name.orEmpty()) }
    var building by remember { mutableStateOf(existing?.buildingName.orEmpty()) }
    var room by remember { mutableStateOf(existing?.roomNumber.orEmpty()) }
    var startMinutes by remember { mutableIntStateOf(existing?.startMinutes ?: 9 * 60) }
    var endMinutes by remember { mutableIntStateOf(existing?.endMinutes ?: 10 * 60) }
    var days by remember { mutableStateOf(existing?.days ?: emptySet()) }
    var color by remember { mutableStateOf(existing?.color ?: ClassColorSwatches.first()) }

    var buildingMenuOpen by remember { mutableStateOf(false) }
    var editingStart by remember { mutableStateOf(false) }
    var editingEnd by remember { mutableStateOf(false) }

    val isValid = name.isNotBlank() && building.isNotBlank() && room.isNotBlank() && days.isNotEmpty()

    if (editingStart || editingEnd) {
        val initial = if (editingStart) startMinutes else endMinutes
        val timeState = rememberTimePickerState(
            initialHour = initial / 60,
            initialMinute = initial % 60,
            is24Hour = false,
        )
        AlertDialog(
            onDismissRequest = {
                editingStart = false
                editingEnd = false
            },
            confirmButton = {
                TextButton(onClick = {
                    val value = timeState.hour * 60 + timeState.minute
                    if (editingStart) {
                        startMinutes = value
                        if (endMinutes <= value) endMinutes = (value + 60).coerceAtMost(23 * 60 + 59)
                    } else {
                        endMinutes = value
                    }
                    editingStart = false
                    editingEnd = false
                }) { Text("OK", color = extras.accent) }
            },
            dismissButton = {
                TextButton(onClick = {
                    editingStart = false
                    editingEnd = false
                }) { Text("Cancel") }
            },
            text = { TimePicker(state = timeState) },
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(extras.screenBackground)
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()),
    ) {
        GoldMineHeader(
            title = if (existing == null) "Add Class" else "Edit Class",
            onBack = onCancel,
            backLabel = "Cancel",
            trailing = {
                TextButton(
                    onClick = {
                        onSave(
                            ClassItem(
                                id = existing?.id ?: UUID.randomUUID().toString(),
                                name = name.trim(),
                                buildingName = building.trim(),
                                roomNumber = room.trim(),
                                startMinutes = startMinutes,
                                endMinutes = endMinutes,
                                days = days,
                                color = color,
                            ),
                        )
                    },
                    enabled = isValid,
                ) {
                    Text(
                        text = "Save",
                        color = if (isValid) extras.accent else Color.Gray,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            },
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Class Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                )
            }

            item {
                Box {
                    OutlinedTextField(
                        value = building,
                        onValueChange = { building = it },
                        label = { Text("Building") },
                        singleLine = true,
                        readOnly = false,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Filled.ArrowDropDown,
                                contentDescription = "Pick building",
                                modifier = Modifier.clickable { buildingMenuOpen = true },
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                    )
                    DropdownMenu(
                        expanded = buildingMenuOpen,
                        onDismissRequest = { buildingMenuOpen = false },
                    ) {
                        CampusBuildings.names.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    building = option
                                    buildingMenuOpen = false
                                },
                            )
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = room,
                    onValueChange = { room = it },
                    label = { Text("Room Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                )
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TimeField(
                        label = "Starts",
                        value = formatMinutesOfDay(startMinutes),
                        onClick = { editingStart = true },
                        modifier = Modifier.weight(1f),
                    )
                    TimeField(
                        label = "Ends",
                        value = formatMinutesOfDay(endMinutes),
                        onClick = { editingEnd = true },
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Days",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Weekday.entries.forEach { day ->
                            val selected = day in days
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (selected) {
                                            GoldMineColors.CharlotteGreen
                                        } else {
                                            extras.cardBackground
                                        },
                                    )
                                    .border(
                                        1.dp,
                                        GoldMineColors.CharlotteGreen,
                                        RoundedCornerShape(8.dp),
                                    )
                                    .clickable {
                                        days = if (selected) days - day else days + day
                                    },
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = day.shortName,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (selected) {
                                        Color.White
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    },
                                )
                            }
                        }
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Color",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ClassColorSwatches.forEach { swatch ->
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(colorFromHex(swatch))
                                    .border(
                                        width = if (swatch == color) 3.dp else 0.dp,
                                        color = if (swatch == color) {
                                            MaterialTheme.colorScheme.onBackground
                                        } else {
                                            Color.Transparent
                                        },
                                        shape = CircleShape,
                                    )
                                    .clickable { color = swatch },
                            )
                        }
                    }
                }
            }
        }

        Box(
            Modifier.height(
                WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 8.dp,
            ),
        )
    }
}

@Composable
private fun TimeField(
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val extras = LocalGoldMineColors.current
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = extras.secondaryText,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(extras.cardBackground)
                .border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
