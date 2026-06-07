package com.example.a216155_cikguizwan_project2

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object NotificationScheduler {

    fun scheduleTaskNotifications(
        context: Context,
        taskId: String,
        taskTitle: String,
        taskType: String,
        dueTimestamp: Long,
    ) {
        val now = System.currentTimeMillis()

        when (taskType) {
            "Assignment" -> {
                scheduleNotification(
                    context = context,
                    notificationId = (taskId + "60").hashCode(),
                    title = "📚 Assignment Reminder",
                    message = "$taskTitle — Assignment due in 1 hour",
                    delayMillis = dueTimestamp - now - 60 * 60 * 1000L,
                )
                scheduleNotification(
                    context = context,
                    notificationId = (taskId + "30").hashCode(),
                    title = "📚 Assignment Reminder",
                    message = "$taskTitle — Assignment due in 30 minutes",
                    delayMillis = dueTimestamp - now - 30 * 60 * 1000L,
                )
                scheduleNotification(
                    context = context,
                    notificationId = (taskId + "10").hashCode(),
                    title = "📚 Assignment Reminder",
                    message = "$taskTitle — Assignment due in 10 minutes",
                    delayMillis = dueTimestamp - now - 10 * 60 * 1000L,
                )
            }
            "Reminder" -> {
                scheduleNotification(
                    context = context,
                    notificationId = taskId.hashCode(),
                    title = "🔔 Task Reminder",
                    message = "Task reminder, please check the task: $taskTitle",
                    delayMillis = dueTimestamp - now,
                )
            }
            "Revision" -> {
                scheduleNotification(
                    context = context,
                    notificationId = taskId.hashCode(),
                    title = "📖 Revision Time",
                    message = "Time arrived to do revision: $taskTitle",
                    delayMillis = dueTimestamp - now,
                )
            }
        }
    }

    private fun scheduleNotification(
        context: Context,
        notificationId: Int,
        title: String,
        message: String,
        delayMillis: Long,
    ) {
        if (delayMillis <= 0) return

        val data = Data.Builder()
            .putString("title", title)
            .putString("message", message)
            .putInt("notificationId", notificationId)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<TaskNotificationWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag(notificationId.toString())
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }

    fun cancelTaskNotifications(context: Context, taskId: String) {
        val wm = WorkManager.getInstance(context)
        listOf("60", "30", "10", "").forEach { suffix ->
            wm.cancelAllWorkByTag((taskId + suffix).hashCode().toString())
        }
    }
}