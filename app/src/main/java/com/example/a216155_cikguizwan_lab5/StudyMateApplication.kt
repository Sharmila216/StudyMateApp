package com.example.a216155_cikguizwan_lab5

import android.app.Application
import com.example.a216155_cikguizwan_lab5.data.StudyMateDatabase
import com.example.a216155_cikguizwan_lab5.data.StudyMateRepository

class StudyMateApplication : Application() {

    val database by lazy { StudyMateDatabase.getDatabase(this) }

    val repository by lazy {
        StudyMateRepository(
            taskDao = database.taskDao(),
            examDao = database.examDao(),
        )
    }

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)
    }
}