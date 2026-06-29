package com.soundscope.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DecibelDao {

    @Query("SELECT * FROM decibel_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<DecibelRecord>>

    @Query("SELECT * FROM decibel_records WHERE id = :id")
    suspend fun getRecordById(id: Long): DecibelRecord?

    @Query("SELECT * FROM decibel_records WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    fun getRecordsByTimeRange(startTime: Long, endTime: Long): Flow<List<DecibelRecord>>

    @Query("SELECT COUNT(*) FROM decibel_records")
    suspend fun getRecordCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: DecibelRecord): Long

    @Delete
    suspend fun deleteRecord(record: DecibelRecord)

    @Query("DELETE FROM decibel_records WHERE id = :id")
    suspend fun deleteRecordById(id: Long)

    @Query("DELETE FROM decibel_records")
    suspend fun deleteAllRecords()
}
