package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusSessionDao {
    @Query("SELECT * FROM focus_sessions ORDER BY timestampMillis DESC")
    fun getAllSessions(): Flow<List<FocusSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: FocusSessionEntity): Long

    @Query("SELECT SUM(durationMinutes) FROM focus_sessions WHERE sessionType = 'Focus'")
    fun getTotalFocusMinutes(): Flow<Int?>

    @Query("SELECT COUNT(*) FROM focus_sessions WHERE sessionType = 'Focus'")
    fun getTotalCompletedSessions(): Flow<Int>
}
