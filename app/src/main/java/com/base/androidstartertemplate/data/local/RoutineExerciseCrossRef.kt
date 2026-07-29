package com.base.androidstartertemplate.data.local

import androidx.room.Entity

@Entity(
    tableName = "routine_exercise_cross_ref",
    primaryKeys = ["routineId", "exerciseId"]
)
data class RoutineExerciseCrossRef(
    val routineId: Long,
    val exerciseId: String
)
