package com.base.androidstartertemplate.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonalRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPR(pr: PersonalRecordEntity)

    @Query("SELECT * FROM personal_records WHERE exerciseId = :exerciseId LIMIT 1")
    suspend fun getPRForExercise(exerciseId: String): PersonalRecordEntity?

    @Query("SELECT * FROM personal_records")
    fun getAllPRs(): Flow<List<PersonalRecordEntity>>
}
