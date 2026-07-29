package com.base.androidstartertemplate.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.base.androidstartertemplate.data.model.EquipmentType
import com.base.androidstartertemplate.data.model.Exercise
import com.base.androidstartertemplate.ui.viewmodel.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRoutineScreen(
    viewModel: WorkoutViewModel,
    routineIdToEdit: Long? = null,
    onNavigateBack: () -> Unit
) {
    val exercises by viewModel.exercises.collectAsState()
    var routineName by remember { mutableStateOf("") }
    val selectedExerciseIds = remember { mutableStateListOf<String>() }

    var searchQuery by remember { mutableStateOf("") }
    var selectedEquipmentSet by remember { mutableStateOf<Set<EquipmentType>>(emptySet()) }
    var selectedMuscleGroups by remember { mutableStateOf<Set<String>>(emptySet()) }

    LaunchedEffect(routineIdToEdit) {
        if (routineIdToEdit != null) {
            viewModel.getRoutineWithExerciseIds(routineIdToEdit) { routine, ids ->
                if (routine != null) {
                    routineName = routine.name
                    selectedExerciseIds.clear()
                    selectedExerciseIds.addAll(ids)
                }
            }
        }
    }

    val muscleGroups = listOf("Chest", "Back", "Legs", "Arms", "Shoulders", "Core")

    val filteredList = remember(exercises, searchQuery, selectedEquipmentSet, selectedMuscleGroups) {
        exercises.filter { exercise ->
            val matchesEquip = selectedEquipmentSet.isEmpty() || selectedEquipmentSet.contains(exercise.equipment)
            val matchesMuscle = selectedMuscleGroups.isEmpty() || selectedMuscleGroups.any { m -> exercise.targetMuscle.contains(m, ignoreCase = true) }
            val matchesQuery = (searchQuery.isBlank() || exercise.name.contains(searchQuery, ignoreCase = true) || exercise.targetMuscle.contains(searchQuery, ignoreCase = true))
            matchesEquip && matchesMuscle && matchesQuery
        }
    }

    val selectedExercisesList = remember(selectedExerciseIds.toList(), exercises) {
        selectedExerciseIds.mapNotNull { id -> exercises.firstOrNull { it.id == id } }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (routineIdToEdit != null) "Edit Routine" else "Routine Builder", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Routine Name Input
                OutlinedTextField(
                    value = routineName,
                    onValueChange = { routineName = it },
                    label = { Text("Routine Name (e.g. Upper Body Blast)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Selected Exercises Summary Card & Reorder Controls
                if (selectedExerciseIds.isNotEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Selected & Ordered Exercises (${selectedExerciseIds.size})",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "Use ▲▼ to reorder",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                itemsIndexed(selectedExercisesList) { index, exercise ->
                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            if (index > 0) {
                                                IconButton(
                                                    onClick = {
                                                        val temp = selectedExerciseIds[index]
                                                        selectedExerciseIds[index] = selectedExerciseIds[index - 1]
                                                        selectedExerciseIds[index - 1] = temp
                                                    },
                                                    modifier = Modifier.size(20.dp)
                                                ) {
                                                    Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = "Move Up", tint = Color.Black)
                                                }
                                            }
                                            if (index < selectedExerciseIds.size - 1) {
                                                IconButton(
                                                    onClick = {
                                                        val temp = selectedExerciseIds[index]
                                                        selectedExerciseIds[index] = selectedExerciseIds[index + 1]
                                                        selectedExerciseIds[index + 1] = temp
                                                    },
                                                    modifier = Modifier.size(20.dp)
                                                ) {
                                                    Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "Move Down", tint = Color.Black)
                                                }
                                            }

                                            Text(
                                                text = "${index + 1}. ${exercise.name}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black
                                            )

                                            Spacer(modifier = Modifier.width(4.dp))
                                            IconButton(
                                                onClick = { selectedExerciseIds.remove(exercise.id) },
                                                modifier = Modifier.size(18.dp)
                                            ) {
                                                Icon(imageVector = Icons.Default.Close, contentDescription = "Remove", tint = Color.Black)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search exercise to add...") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear search")
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Muscle Group Filter Chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    item {
                        FilterChip(
                            selected = selectedMuscleGroups.isEmpty(),
                            onClick = { selectedMuscleGroups = emptySet() },
                            label = { Text("⚡ All Muscles", fontSize = 12.sp) },
                            leadingIcon = if (selectedMuscleGroups.isEmpty()) {
                                { Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondary,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                    items(muscleGroups) { muscle ->
                        val isSelected = selectedMuscleGroups.contains(muscle)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedMuscleGroups = if (isSelected) selectedMuscleGroups - muscle else selectedMuscleGroups + muscle
                            },
                            label = { Text(muscle, fontSize = 12.sp) },
                            leadingIcon = if (isSelected) {
                                { Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondary,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }

                // Equipment Filter Chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    item {
                        FilterChip(
                            selected = selectedEquipmentSet.isEmpty(),
                            onClick = { selectedEquipmentSet = emptySet() },
                            label = { Text("⚡ All Equipment", fontSize = 12.sp) },
                            leadingIcon = if (selectedEquipmentSet.isEmpty()) {
                                { Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                    items(EquipmentType.values()) { equipment ->
                        val isSelected = selectedEquipmentSet.contains(equipment)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedEquipmentSet = if (isSelected) selectedEquipmentSet - equipment else selectedEquipmentSet + equipment
                            },
                            label = { Text(equipment.chipLabel, fontSize = 12.sp) },
                            leadingIcon = if (isSelected) {
                                { Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }

                // Exercise Selection List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredList) { exercise ->
                        val isChecked = selectedExerciseIds.contains(exercise.id)
                        ExerciseSelectionRow(
                            exercise = exercise,
                            isSelected = isChecked,
                            onToggle = {
                                if (isChecked) {
                                    selectedExerciseIds.remove(exercise.id)
                                } else {
                                    selectedExerciseIds.add(exercise.id)
                                }
                            }
                        )
                    }
                }

                // Save Routine Button
                Button(
                    onClick = {
                        if (routineIdToEdit != null) {
                            viewModel.updateCustomRoutine(routineIdToEdit, routineName, selectedExerciseIds) {
                                onNavigateBack()
                            }
                        } else {
                            viewModel.saveCustomRoutine(routineName, selectedExerciseIds) {
                                onNavigateBack()
                            }
                        }
                    },
                    enabled = routineName.isNotBlank() && selectedExerciseIds.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.Black
                    )
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (routineIdToEdit != null) "Update Routine (${selectedExerciseIds.size})" else "Save Routine (${selectedExerciseIds.size})",
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
fun ExerciseSelectionRow(
    exercise: Exercise,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${exercise.equipment.chipLabel} · ${exercise.targetMuscle}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    checkmarkColor = Color.Black
                )
            )
        }
    }
}
