package com.example.a216155_cikguizwan_project2.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.a216155_cikguizwan_project2.ExamItem

@Entity(tableName = "exams")
data class ExamEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val subject: String,
    val examType: String,
    val inPerson: Boolean,
    val seat: String,
    val room: String,
    val onlineUrl: String,
    val dateDisplay: String,
    val timeDisplay: String,
    val durationMinutes: String,
    val examTimestamp: Long,
    val isResit: Boolean = false,
    val module: String = "",
)

fun ExamEntity.toExamItem() = ExamItem(
    id = id, name = name, subject = subject,
    examType = examType, inPerson = inPerson,
    seat = seat, room = room, onlineUrl = onlineUrl,
    dateDisplay = dateDisplay, timeDisplay = timeDisplay,
    durationMinutes = durationMinutes,
    examTimestamp = examTimestamp,
    isResit = isResit, module = module,
)

fun ExamItem.toEntity() = ExamEntity(
    id = id, name = name, subject = subject,
    examType = examType, inPerson = inPerson,
    seat = seat, room = room, onlineUrl = onlineUrl,
    dateDisplay = dateDisplay, timeDisplay = timeDisplay,
    durationMinutes = durationMinutes,
    examTimestamp = examTimestamp,
    isResit = isResit, module = module,
)

