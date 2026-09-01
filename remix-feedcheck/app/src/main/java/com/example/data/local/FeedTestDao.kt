package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedTestDao {
    @Query("SELECT * FROM feed_tests ORDER BY id DESC")
    fun getAllTests(): Flow<List<FeedTestEntity>>

    @Query("SELECT * FROM feed_tests WHERE id = :testId LIMIT 1")
    suspend fun getTestById(testId: Long): FeedTestEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTest(test: FeedTestEntity): Long

    @Query("DELETE FROM feed_tests WHERE id = :testId")
    suspend fun deleteTest(testId: Long)

    @Query("SELECT COUNT(*) FROM feed_tests")
    suspend fun getCount(): Int
}
