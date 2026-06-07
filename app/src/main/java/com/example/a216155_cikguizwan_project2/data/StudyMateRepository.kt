package com.example.a216155_cikguizwan_project2.data

import com.example.a216155_cikguizwan_project2.ExamItem
import com.example.a216155_cikguizwan_project2.TaskItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StudyMateRepository(
    private val taskDao: TaskDao,
    private val examDao: ExamDao,
    private val userProfileDao: UserProfileDao
) {
    // --- TASKS ---
    val allTasks: Flow<List<TaskItem>> = taskDao.getAll().map { list ->
        list.map { it.toTaskItem() }
    }

    suspend fun insertTask(task: TaskItem) {
        taskDao.insert(task.toEntity())
    }

    suspend fun deleteTask(id: String) {
        taskDao.deleteById(id)
    }

    suspend fun completeTask(id: String) {
        taskDao.updateCompletion(id, isCompleted = true, progress = 100)
    }

    suspend fun completeAllOverdueTasks(todayMillis: Long) {
        taskDao.completeAllOverdue(todayMillis)
    }

    // --- EXAMS ---
    val allExams: Flow<List<ExamItem>> = examDao.getAll().map { list ->
        list.map { it.toExamItem() }
    }

    suspend fun insertExam(exam: ExamItem) {
        examDao.insert(exam.toEntity())
    }

    suspend fun deleteExam(id: String) {
        examDao.deleteById(id)
    }

    // --- USER PROFILE ---
    val userProfile: Flow<UserProfileEntity?> = userProfileDao.getProfile()

    suspend fun saveUserProfile(profile: UserProfileEntity) {
        userProfileDao.saveProfile(profile)
    }

    suspend fun getUserProfile(): UserProfileEntity? {
        return userProfileDao.getProfileOnce()
    }
}