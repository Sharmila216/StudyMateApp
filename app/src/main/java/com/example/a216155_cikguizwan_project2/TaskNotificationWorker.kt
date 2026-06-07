package com.example.a216155_cikguizwan_project2

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class TaskNotificationWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        val title = inputData.getString("title") ?: "StudyMate"
        val message = inputData.getString("message") ?: ""
        val notificationId = inputData.getInt("notificationId", 0)

        NotificationHelper.showNotification(
            context = applicationContext,
            notificationId = notificationId,
            title = title,
            message = message,
        )
        return Result.success()
    }
}