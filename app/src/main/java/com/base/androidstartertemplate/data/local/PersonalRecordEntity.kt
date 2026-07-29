package com.base.androidstartertemplate.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "personal_records")
data class PersonalRecordEntity(
    @PrimaryKey
    val exerciseId: String,
    val maxWeightKg: Double = 0.0,
    val maxReps: Int = 0,
    val maxDurationSeconds: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
)
