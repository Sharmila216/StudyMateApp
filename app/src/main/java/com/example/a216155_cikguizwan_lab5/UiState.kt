package com.example.a216155_cikguizwan_lab5

import java.util.UUID

/** Saved task (Add New + list + detail + update). */
data class TaskItem(
    val id: String = UUID.randomUUID().toString(),
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

/** Saved exam (Add New + list + detail + update). */
data class ExamItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val subject: String,
    val examType: String,
    val inPerson: Boolean,
    val seat: String,
    val room: String,
    val onlineUrl: String = "",
    val dateDisplay: String,
    val timeDisplay: String,
    val durationMinutes: String,
    val examTimestamp: Long,
    val isResit: Boolean = false,
    val module: String = "",
)

/** Form state for the Add New screen (all categories share one ViewModel). */
data class AddNewFormState(
    val selectedCategory: String = "Tasks",
    // Tasks
    val taskTitle: String = "",
    val taskDescription: String = "",
    val taskSubject: String = "",
    val taskTypeOptional: String = "",
    val taskOccursOnce: Boolean = true,
    val taskDueDate: String = "",
    val taskDueTime: String = "",
    val freeTasksRemaining: Int = 10,
    // Classes
    val classModeInPerson: Boolean = true,
    val className: String = "",
    val classRoom: String = "",
    val classBuilding: String = "",
    val classTeacher: String = "",
    val classSubject: String = "",
    /** None | Academic | Manual */
    val classDateRangeType: String = "None",
    val classOccursOnce: Boolean = true,
    val classDate: String = "",
    val classStartTime: String = "",
    val classEndTime: String = "",
    // Exams
    val examName: String = "",
    val examSubject: String = "",
    /** Exam | Quiz | Test */
    val examType: String = "Exam",
    val examModeInPerson: Boolean = true,
    val examSeat: String = "",
    val examRoom: String = "",
    val examOnlineUrl: String = "",
    val examDate: String = "",
    val examTime: String = "",
    val examDurationMinutes: String = "",
    // Vacations
    val vacationName: String = "",
    val vacationDetails: String = "",
    val vacationStartDate: String = "",
    val vacationEndDate: String = "",
    // Xtra
    val xtraTitle: String = "",
    val xtraDetails: String = "",
    val xtraNote: String = "",
)

data class UiState(
    val firstName: String = "",
    val lastName: String = "",
    val addNewForm: AddNewFormState = AddNewFormState(),
    val tasks: List<TaskItem> = emptyList(),
    val exams: List<ExamItem> = emptyList(),
)
