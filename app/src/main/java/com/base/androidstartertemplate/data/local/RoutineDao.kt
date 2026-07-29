package com.base.androidstartertemplate.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: RoutineEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutineExerciseCrossRefs(crossRefs: List<RoutineExerciseCrossRef>)

    @Transaction
    suspend fun insertRoutineWithExercises(routine: RoutineEntity, exerciseIds: List<String>) {
        val routineId = insertRoutine(routine)
        val refs = exerciseIds.map { RoutineExerciseCrossRef(routineId = routineId, exerciseId = it) }
        insertRoutineExerciseCrossRefs(refs)
    }

    @Query("UPDATE routines SET name = :name WHERE id = :routineId")
    suspend fun updateRoutineName(routineId: Long, name: String)

    @Query("DELETE FROM routine_exercise_cross_ref WHERE routineId = :routineId")
    suspend fun deleteCrossRefsForRoutine(routineId: Long)

    @Transaction
    suspend fun updateRoutineWithExercises(routineId: Long, name: String, exerciseIds: List<String>) {
        updateRoutineName(routineId, name)
        deleteCrossRefsForRoutine(routineId)
        val refs = exerciseIds.map { RoutineExerciseCrossRef(routineId = routineId, exerciseId = it) }
        insertRoutineExerciseCrossRefs(refs)
    }

    @Query("SELECT * FROM routines WHERE id = :routineId LIMIT 1")
    suspend fun getRoutineById(routineId: Long): RoutineEntity?

    @Query("SELECT * FROM routines ORDER BY createdAt DESC")
    fun getAllRoutines(): Flow<List<RoutineEntity>>

    @Query("SELECT exerciseId FROM routine_exercise_cross_ref WHERE routineId = :routineId")
    suspend fun getExerciseIdsForRoutine(routineId: Long): List<String>

    @Query("DELETE FROM routines WHERE id = :routineId")
    suspend fun deleteRoutine(routineId: Long)
}
