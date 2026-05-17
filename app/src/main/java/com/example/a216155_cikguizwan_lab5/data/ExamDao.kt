package com.example.a216155_cikguizwan_lab5.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExamDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exam: ExamEntity)

    @Query("SELECT * FROM exams ORDER BY examTimestamp ASC")
    fun getAll(): Flow<List<ExamEntity>>

    @Query("DELETE FROM exams WHERE id = :id")
    suspend fun deleteById(id: String)
}