package com.example.a216155_cikguizwan_lab5.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity)

    @Query("SELECT * FROM tasks ORDER BY dueTimestamp ASC")
    fun getAll(): Flow<List<TaskEntity>>

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE tasks SET isCompleted = :isCompleted, progressPercent = :progress WHERE id = :id")
    suspend fun updateCompletion(id: String, isCompleted: Boolean, progress: Int)

    @Query("UPDATE tasks SET isCompleted = 1, progressPercent = 100 WHERE isCompleted = 0 AND dueTimestamp < :todayMillis")
    suspend fun completeAllOverdue(todayMillis: Long)
}