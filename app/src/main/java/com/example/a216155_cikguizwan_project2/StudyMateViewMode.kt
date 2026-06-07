package com.example.a216155_cikguizwan_project2

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.a216155_cikguizwan_project2.data.StudyMateRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
class StudyMateApp(
    application: Application,
    private val repository: StudyMateRepository
) : AndroidViewModel(application) {

    // --- SHARED PREFERENCES FOR PROFILE NAME ---
    private val prefs = application.getSharedPreferences("StudyMatePrefs", Context.MODE_PRIVATE)

    // --- FIREBASE ---
    private val firebaseDb = Firebase.database("https://studymate-3d213-default-rtdb.firebaseio.com").reference

    private val _uiState = mutableStateOf(
        UiState(
            // Load saved name from SharedPreferences
            firstName = prefs.getString("firstName", "") ?: "",
            lastName = prefs.getString("lastName", "") ?: ""
        )
    )
    val uiState: State<UiState> = _uiState

    init {
        repository.allTasks
            .onEach { tasks ->
                _uiState.value = _uiState.value.copy(tasks = tasks)
            }
            .launchIn(viewModelScope)

        repository.allExams
            .onEach { exams ->
                _uiState.value = _uiState.value.copy(exams = exams)
            }
            .launchIn(viewModelScope)

        // Load profile from Room
        repository.userProfile
            .onEach { profile ->
                profile?.let {
                    _uiState.value = _uiState.value.copy(
                        firstName = it.firstName,
                        lastName = it.lastName,
                        profilePicturePath = it.profilePicturePath
                    )
                    android.util.Log.d("Room", "✅ Profile loaded from Room: ${it.firstName} ${it.lastName}")
                }
            }
            .launchIn(viewModelScope)
    }

    // --- SAVE PROFILE NAME (SharedPreferences + Firebase) ---
    fun updateUserProfile(newFirst: String, newLast: String) {
        viewModelScope.launch {

            // ✅ Save to Room (local permanent storage)
            val currentPath = _uiState.value.profilePicturePath
            val profile = com.example.a216155_cikguizwan_project2.data.UserProfileEntity(
                id = 1,
                firstName = newFirst,
                lastName = newLast,
                profilePicturePath = currentPath,
                updatedAt = System.currentTimeMillis()
            )
            repository.saveUserProfile(profile)
            android.util.Log.d("Room", "✅ Profile name saved to Room: $newFirst $newLast")

            // ✅ Update UI
            _uiState.value = _uiState.value.copy(
                firstName = newFirst,
                lastName = newLast,
            )
        }
    }
    fun saveProfilePicturePath(picturePath: String) {
        viewModelScope.launch {
            val current = repository.getUserProfile()
            val profile = com.example.a216155_cikguizwan_project2.data.UserProfileEntity(
                id = 1,
                firstName = current?.firstName ?: _uiState.value.firstName,
                lastName = current?.lastName ?: _uiState.value.lastName,
                profilePicturePath = picturePath,
                updatedAt = System.currentTimeMillis()
            )
            repository.saveUserProfile(profile)
            android.util.Log.d("Room", "✅ Picture path saved to Room: $picturePath")

            _uiState.value = _uiState.value.copy(
                profilePicturePath = picturePath
            )
        }
    }

    fun updateAddNewForm(transform: (AddNewFormState) -> AddNewFormState) {
        val current = _uiState.value.addNewForm
        _uiState.value = _uiState.value.copy(addNewForm = transform(current))
    }

    fun setAddNewCategory(category: String) {
        updateAddNewForm { it.copy(selectedCategory = category) }
    }

    // --- SAVE TASK (Room + Firebase) ---
    fun saveNewTaskFromForm(): Boolean {
        val f = _uiState.value.addNewForm
        if (f.taskTitle.isBlank()) return false
        val ts = parseCombinedDateTime(f.taskDueDate, f.taskDueTime)
        val task = TaskItem(
            title = f.taskTitle.trim(),
            details = f.taskDescription.trim(),
            subject = f.taskSubject.trim(),
            typeOptional = f.taskTypeOptional.trim(),
            occursOnce = f.taskOccursOnce,
            dueDateDisplay = f.taskDueDate.trim().ifBlank { formatDisplayDate(ts) },
            dueTimeDisplay = f.taskDueTime.trim().ifBlank { formatDisplayTime(ts) },
            dueTimestamp = ts,
        )
        viewModelScope.launch {
            // Save to Room (local)
            repository.insertTask(task)

            // Save to Firebase (cloud backup)
            val taskData = mapOf(
                "id" to task.id,
                "title" to task.title,
                "details" to task.details,
                "subject" to task.subject,
                "typeOptional" to task.typeOptional,
                "occursOnce" to task.occursOnce,
                "dueDateDisplay" to task.dueDateDisplay,
                "dueTimeDisplay" to task.dueTimeDisplay,
                "dueTimestamp" to task.dueTimestamp,
                "progressPercent" to task.progressPercent,
                "isCompleted" to task.isCompleted,
                "savedAt" to System.currentTimeMillis()
            )
            firebaseDb.child("tasks").child(task.id).setValue(taskData)
                .addOnSuccessListener {
                    android.util.Log.d("Firebase", "✅ Task saved to Firebase: ${task.title}")
                }
                .addOnFailureListener { e ->
                    android.util.Log.e("Firebase", "❌ Task save failed: ${e.message}")
                }

            NotificationScheduler.scheduleTaskNotifications(
                context = getApplication(),
                taskId = task.id,
                taskTitle = task.title,
                taskType = task.typeOptional,
                dueTimestamp = task.dueTimestamp,
            )
        }
        updateAddNewForm {
            it.copy(
                taskTitle = "",
                taskDescription = "",
                taskSubject = "",
                taskTypeOptional = "",
                taskOccursOnce = true,
                taskDueDate = "",
                taskDueTime = "",
                freeTasksRemaining = (it.freeTasksRemaining - 1).coerceAtLeast(0),
            )
        }
        return true
    }

    fun updateTask(
        id: String,
        title: String,
        details: String,
        typeOptional: String,
        occursOnce: Boolean,
        dueDateDisplay: String,
        dueTimeDisplay: String,
    ) {
        val ts = parseCombinedDateTime(dueDateDisplay, dueTimeDisplay)
        val existing = _uiState.value.tasks.find { it.id == id } ?: return
        val updated = existing.copy(
            title = title.trim(),
            details = details.trim(),
            typeOptional = typeOptional.trim(),
            occursOnce = occursOnce,
            dueDateDisplay = dueDateDisplay.trim().ifBlank { formatDisplayDate(ts) },
            dueTimeDisplay = dueTimeDisplay.trim().ifBlank { formatDisplayTime(ts) },
            dueTimestamp = ts,
        )
        viewModelScope.launch {
            repository.insertTask(updated)

            // Update Firebase
            val taskData = mapOf(
                "id" to updated.id,
                "title" to updated.title,
                "details" to updated.details,
                "subject" to updated.subject,
                "typeOptional" to updated.typeOptional,
                "occursOnce" to updated.occursOnce,
                "dueDateDisplay" to updated.dueDateDisplay,
                "dueTimeDisplay" to updated.dueTimeDisplay,
                "dueTimestamp" to updated.dueTimestamp,
                "progressPercent" to updated.progressPercent,
                "isCompleted" to updated.isCompleted,
                "updatedAt" to System.currentTimeMillis()
            )
            firebaseDb.child("tasks").child(id).setValue(taskData)
                .addOnSuccessListener {
                    android.util.Log.d("Firebase", "✅ Task updated in Firebase: ${updated.title}")
                }
                .addOnFailureListener { e ->
                    android.util.Log.e("Firebase", "❌ Task update failed: ${e.message}")
                }
        }
    }

    fun deleteTask(id: String) {
        viewModelScope.launch {
            // Only delete from Room locally
            // Firebase keeps the data for backup/restore!
            repository.deleteTask(id)
            android.util.Log.d("Room", "✅ Task deleted from Room only — Firebase backup kept!")
        }
    }

    fun completeTask(id: String) {
        viewModelScope.launch {
            repository.completeTask(id)

            // Update Firebase
            firebaseDb.child("tasks").child(id).child("isCompleted").setValue(true)
                .addOnSuccessListener {
                    android.util.Log.d("Firebase", "✅ Task completed in Firebase: $id")
                }
        }
    }

    fun completeAllOverdueTasks() {
        val today = startOfDayMillis(System.currentTimeMillis())
        viewModelScope.launch {
            repository.completeAllOverdueTasks(today)
            android.util.Log.d("Firebase", "✅ All overdue tasks completed")
        }
    }

    fun getTask(id: String): TaskItem? = _uiState.value.tasks.find { it.id == id }

    fun postStudyTip(
        authorName: String,
        tip: String,
        subject: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val tipId = java.util.UUID.randomUUID().toString()
        val tipData = mapOf(
            "id" to tipId,
            "author" to authorName,
            "tip" to tip,
            "subject" to subject,
            "timestamp" to System.currentTimeMillis()
        )
        firebaseDb.child("study_tips").child(tipId)
            .setValue(tipData)
            .addOnSuccessListener {
                android.util.Log.d("Firebase", "✅ Study tip posted!")
                onSuccess()
            }
            .addOnFailureListener { error ->
                android.util.Log.e("Firebase", "❌ Failed: ${error.message}")
                onFailure(error.message ?: "Unknown error")
            }
    }
    // --- SAVE EXAM (Room + Firebase) ---
    fun saveNewExamFromForm(): Boolean {
        val f = _uiState.value.addNewForm
        if (f.examName.isBlank() || f.examSubject.isBlank()) return false
        val ts = parseCombinedDateTime(f.examDate, f.examTime)
        val exam = ExamItem(
            name = f.examName.trim(),
            subject = f.examSubject.trim(),
            examType = f.examType,
            inPerson = f.examModeInPerson,
            seat = if (f.examModeInPerson) f.examSeat.trim() else "",
            room = if (f.examModeInPerson) f.examRoom.trim() else "",
            onlineUrl = if (f.examModeInPerson) "" else f.examOnlineUrl.trim(),
            dateDisplay = f.examDate.trim().ifBlank { formatDisplayDate(ts) },
            timeDisplay = f.examTime.trim().ifBlank { formatDisplayTime(ts) },
            durationMinutes = f.examDurationMinutes.trim(),
            examTimestamp = ts,
            module = f.examSubject.trim(),
        )
        viewModelScope.launch {
            repository.insertExam(exam)

            // Save to Firebase
            val examData = mapOf(
                "id" to exam.id,
                "name" to exam.name,
                "subject" to exam.subject,
                "examType" to exam.examType,
                "inPerson" to exam.inPerson,
                "seat" to exam.seat,
                "room" to exam.room,
                "onlineUrl" to exam.onlineUrl,
                "dateDisplay" to exam.dateDisplay,
                "timeDisplay" to exam.timeDisplay,
                "durationMinutes" to exam.durationMinutes,
                "examTimestamp" to exam.examTimestamp,
                "savedAt" to System.currentTimeMillis()
            )
            firebaseDb.child("exams").child(exam.id).setValue(examData)
                .addOnSuccessListener {
                    android.util.Log.d("Firebase", "✅ Exam saved to Firebase: ${exam.name}")
                }
                .addOnFailureListener { e ->
                    android.util.Log.e("Firebase", "❌ Exam save failed: ${e.message}")
                }
        }
        updateAddNewForm {
            it.copy(
                examName = "",
                examSubject = "",
                examType = "Exam",
                examModeInPerson = true,
                examSeat = "",
                examRoom = "",
                examOnlineUrl = "",
                examDate = "",
                examTime = "",
                examDurationMinutes = "",
            )
        }
        return true
    }

    fun updateExam(
        id: String,
        isResit: Boolean,
        examType: String,
        inPerson: Boolean,
        module: String,
        seat: String,
        room: String,
        onlineUrl: String,
        dateDisplay: String,
        timeDisplay: String,
        durationMinutes: String,
    ) {
        val ts = parseCombinedDateTime(dateDisplay, timeDisplay)
        val existing = _uiState.value.exams.find { it.id == id } ?: return
        val updated = existing.copy(
            isResit = isResit,
            examType = examType,
            inPerson = inPerson,
            module = module.trim(),
            subject = module.trim().ifBlank { existing.subject },
            seat = if (inPerson) seat.trim() else "",
            room = if (inPerson) room.trim() else "",
            onlineUrl = if (inPerson) "" else onlineUrl.trim(),
            dateDisplay = dateDisplay.trim().ifBlank { formatDisplayDate(ts) },
            timeDisplay = timeDisplay.trim().ifBlank { formatDisplayTime(ts) },
            durationMinutes = durationMinutes.trim(),
            examTimestamp = ts,
        )
        viewModelScope.launch {
            repository.insertExam(updated)

            // Update Firebase
            val examData = mapOf(
                "id" to updated.id,
                "name" to updated.name,
                "subject" to updated.subject,
                "examType" to updated.examType,
                "inPerson" to updated.inPerson,
                "seat" to updated.seat,
                "room" to updated.room,
                "onlineUrl" to updated.onlineUrl,
                "dateDisplay" to updated.dateDisplay,
                "timeDisplay" to updated.timeDisplay,
                "durationMinutes" to updated.durationMinutes,
                "examTimestamp" to updated.examTimestamp,
                "updatedAt" to System.currentTimeMillis()
            )
            firebaseDb.child("exams").child(id).setValue(examData)
                .addOnSuccessListener {
                    android.util.Log.d("Firebase", "✅ Exam updated in Firebase: ${updated.name}")
                }
                .addOnFailureListener { e ->
                    android.util.Log.e("Firebase", "❌ Exam update failed: ${e.message}")
                }
        }
    }

    fun deleteExam(id: String) {
        viewModelScope.launch {
            // Only delete from Room locally
            // Firebase keeps the data for backup/restore!
            repository.deleteExam(id)
            android.util.Log.d("Room", "✅ Exam deleted from Room only — Firebase backup kept!")
        }
    }

    fun getExam(id: String): ExamItem? = _uiState.value.exams.find { it.id == id }

    fun restoreFromFirebase() {
        // Restore Tasks
        firebaseDb.child("tasks").get()
            .addOnSuccessListener { snapshot ->
                viewModelScope.launch {
                    for (child in snapshot.children) {
                        try {
                            val task = TaskItem(
                                id = child.child("id").getValue(String::class.java) ?: continue,
                                title = child.child("title").getValue(String::class.java) ?: "",
                                details = child.child("details").getValue(String::class.java) ?: "",
                                subject = child.child("subject").getValue(String::class.java) ?: "",
                                typeOptional = child.child("typeOptional").getValue(String::class.java) ?: "",
                                occursOnce = child.child("occursOnce").getValue(Boolean::class.java) ?: true,
                                dueDateDisplay = child.child("dueDateDisplay").getValue(String::class.java) ?: "",
                                dueTimeDisplay = child.child("dueTimeDisplay").getValue(String::class.java) ?: "",
                                dueTimestamp = child.child("dueTimestamp").getValue(Long::class.java) ?: 0L,
                                progressPercent = child.child("progressPercent").getValue(Int::class.java) ?: 0,
                                isCompleted = child.child("isCompleted").getValue(Boolean::class.java) ?: false,
                            )
                            repository.insertTask(task)
                        } catch (e: Exception) {
                            android.util.Log.e("Firebase", "Task restore error: ${e.message}")
                        }
                    }
                    android.util.Log.d("Firebase", "✅ Tasks restored from Firebase")
                }
            }
            .addOnFailureListener { e ->
                android.util.Log.e("Firebase", "❌ Task restore failed: ${e.message}")
            }

        // Restore Exams
        firebaseDb.child("exams").get()
            .addOnSuccessListener { snapshot ->
                viewModelScope.launch {
                    for (child in snapshot.children) {
                        try {
                            val exam = ExamItem(
                                id = child.child("id").getValue(String::class.java) ?: continue,
                                name = child.child("name").getValue(String::class.java) ?: "",
                                subject = child.child("subject").getValue(String::class.java) ?: "",
                                examType = child.child("examType").getValue(String::class.java) ?: "Exam",
                                inPerson = child.child("inPerson").getValue(Boolean::class.java) ?: true,
                                seat = child.child("seat").getValue(String::class.java) ?: "",
                                room = child.child("room").getValue(String::class.java) ?: "",
                                onlineUrl = child.child("onlineUrl").getValue(String::class.java) ?: "",
                                dateDisplay = child.child("dateDisplay").getValue(String::class.java) ?: "",
                                timeDisplay = child.child("timeDisplay").getValue(String::class.java) ?: "",
                                durationMinutes = child.child("durationMinutes").getValue(String::class.java) ?: "",
                                examTimestamp = child.child("examTimestamp").getValue(Long::class.java) ?: 0L,
                            )
                            repository.insertExam(exam)
                        } catch (e: Exception) {
                            android.util.Log.e("Firebase", "Exam restore error: ${e.message}")
                        }
                    }
                    android.util.Log.d("Firebase", "✅ Exams restored from Firebase")
                }
            }
            .addOnFailureListener { e ->
                android.util.Log.e("Firebase", "❌ Exam restore failed: ${e.message}")
            }
    }
    companion object {
        fun startOfDayMillis(millis: Long): Long {
            val c = Calendar.getInstance()
            c.timeInMillis = millis
            c.set(Calendar.HOUR_OF_DAY, 0)
            c.set(Calendar.MINUTE, 0)
            c.set(Calendar.SECOND, 0)
            c.set(Calendar.MILLISECOND, 0)
            return c.timeInMillis
        }

        fun formatDisplayDate(millis: Long): String =
            SimpleDateFormat("EEE, d MMM yyyy", Locale.ENGLISH).format(Date(millis))

        fun formatDisplayTime(millis: Long): String =
            SimpleDateFormat("h:mm a", Locale.ENGLISH).format(Date(millis))

        fun formatDetailDate(millis: Long): String =
            SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH).format(Date(millis))

        fun formatCardDayMonth(millis: Long): String =
            SimpleDateFormat("dd MMM", Locale.ENGLISH).format(Date(millis))

        fun parseCalendarDateOnly(dateStr: String): Calendar {
            val c = Calendar.getInstance()
            val millis = if (dateStr.isBlank()) {
                startOfDayMillis(System.currentTimeMillis())
            } else {
                startOfDayMillis(parseCombinedDateTime(dateStr, "12:00 AM"))
            }
            c.timeInMillis = millis
            return c
        }

        fun calendarToStandardDate(cal: Calendar): String = formatDisplayDate(cal.timeInMillis)

        fun parseTimePartsFromString(timeStr: String): Triple<Int, Int, Boolean> {
            val refDate = formatDisplayDate(System.currentTimeMillis())
            val millis = parseCombinedDateTime(refDate, timeStr.ifBlank { "12:00 PM" })
            val c = Calendar.getInstance()
            c.timeInMillis = millis
            val hour24 = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)
            val isPm = hour24 >= 12
            val hour12 = when {
                hour24 == 0 -> 12
                hour24 > 12 -> hour24 - 12
                else -> hour24
            }
            return Triple(hour12, minute, isPm)
        }

        fun formatStandardTime12h(hour12: Int, minute: Int, isPm: Boolean): String {
            val cal = Calendar.getInstance()
            val h = hour12.coerceIn(1, 12)
            val hour24 = when {
                !isPm && h == 12 -> 0
                !isPm -> h
                isPm && h == 12 -> 12
                else -> h + 12
            }
            cal.set(Calendar.HOUR_OF_DAY, hour24)
            cal.set(Calendar.MINUTE, minute.coerceIn(0, 59))
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            return formatDisplayTime(cal.timeInMillis)
        }

        fun parseCombinedDateTime(dateStr: String, timeStr: String): Long {
            val datePart = dateStr.trim()
            val timePart = timeStr.trim().ifBlank { "12:00 AM" }
            val dateFormats = listOf(
                SimpleDateFormat("EEE, d MMM yyyy", Locale.ENGLISH),
                SimpleDateFormat("EEE, d MMMM yyyy", Locale.ENGLISH),
                SimpleDateFormat("d MMM yyyy", Locale.ENGLISH),
            )
            var dayCal = Calendar.getInstance()
            var parsed = false
            for (fmt in dateFormats) {
                try {
                    val d = fmt.parse(datePart) ?: continue
                    dayCal.time = d
                    parsed = true
                    break
                } catch (_: Exception) {}
            }
            if (!parsed) return System.currentTimeMillis()
            val timeFormats = listOf(
                SimpleDateFormat("h:mm a", Locale.ENGLISH),
                SimpleDateFormat("h.mm a", Locale.ENGLISH),
                SimpleDateFormat("K:mm a", Locale.ENGLISH),
            )
            var timeCal: Calendar? = null
            for (tf in timeFormats) {
                try {
                    val t = tf.parse(timePart) ?: continue
                    val tc = Calendar.getInstance()
                    tc.time = t
                    timeCal = tc
                    break
                } catch (_: Exception) {}
            }
            if (timeCal != null) {
                dayCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY))
                dayCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE))
                dayCal.set(Calendar.SECOND, 0)
                dayCal.set(Calendar.MILLISECOND, 0)
            }
            return dayCal.timeInMillis
        }
    }
}


class StudyMateViewModelFactory(
    private val application: Application,
    private val repository: StudyMateRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudyMateApp::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StudyMateApp(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}