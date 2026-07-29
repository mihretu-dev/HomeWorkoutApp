package com.base.androidstartertemplate.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "completed_workouts")
data class CompletedWorkoutEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val routineName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val durationMinutes: Int,
    val totalReps: Int,
    val totalVolumeKg: Double,
    val caloriesBurned: Int
)
