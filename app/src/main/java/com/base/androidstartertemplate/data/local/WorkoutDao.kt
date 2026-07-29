package com.base.androidstartertemplate.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: CompletedWorkoutEntity)

    @Query("SELECT * FROM completed_workouts ORDER BY timestamp DESC")
    fun getAllCompletedWorkouts(): Flow<List<CompletedWorkoutEntity>>
}
