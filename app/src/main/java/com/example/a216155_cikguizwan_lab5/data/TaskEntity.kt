package com.example.a216155_cikguizwan_lab5.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.a216155_cikguizwan_lab5.TaskItem

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val details: String,
    val subject: String,
    val typeOptional: String,
    val occursOnce: Boolean,
    val dueDateDisplay: String,
    val dueTimeDisplay: String,
    val dueTimestamp: Long,
    val progressPercent: Int = 0,
    val isCompleted: Boolean = false,
)

fun TaskEntity.toTaskItem() = TaskItem(
    id = id, title = title, details = details,
    subject = subject, typeOptional = typeOptional,
    occursOnce = occursOnce,
    dueDateDisplay = dueDateDisplay,
    dueTimeDisplay = dueTimeDisplay,
    dueTimestamp = dueTimestamp,
    progressPercent = progressPercent,
    isCompleted = isCompleted,
)

fun TaskItem.toEntity() = TaskEntity(
    id = id, title = title, details = details,
    subject = subject, typeOptional = typeOptional,
    occursOnce = occursOnce,
    dueDateDisplay = dueDateDisplay,
    dueTimeDisplay = dueTimeDisplay,
    dueTimestamp = dueTimestamp,
    progressPercent = progressPercent,
    isCompleted = isCompleted,
)