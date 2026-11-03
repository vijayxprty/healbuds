package com.steptracker.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StepDao {

    @Query("SELECT * FROM daily_steps WHERE date = :date LIMIT 1")
    fun getRecordForDate(date: String): Flow<DailyStepRecord?>

    @Query("SELECT * FROM daily_steps WHERE date = :date LIMIT 1")
    suspend fun getRecordForDateSync(date: String): DailyStepRecord?

    @Query("SELECT * FROM daily_steps ORDER BY date DESC LIMIT :limit")
    fun getRecentRecords(limit: Int = 30): Flow<List<DailyStepRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(record: DailyStepRecord)

    @Query("SELECT SUM(steps) FROM daily_steps")
    fun getTotalLifetimeSteps(): Flow<Int?>
}
