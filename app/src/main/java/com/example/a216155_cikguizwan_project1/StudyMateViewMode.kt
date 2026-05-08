package com.example.a216155_cikguizwan_project1

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
class StudyMateApp : ViewModel() {

    private val _uiState = mutableStateOf(UiState(firstName = "", lastName = ""))
    val uiState: State<UiState> = _uiState

    fun updateUserProfile(newFirst: String, newLast: String) {
        _uiState.value = _uiState.value.copy(
            firstName = newFirst,
            lastName = newLast,
        )
    }

    fun updateAddNewForm(transform: (AddNewFormState) -> AddNewFormState) {
        val current = _uiState.value.addNewForm
        _uiState.value = _uiState.value.copy(addNewForm = transform(current))
    }

    fun setAddNewCategory(category: String) {
        updateAddNewForm { it.copy(selectedCategory = category) }
    }

    /** Returns false if title is blank. */
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
        _uiState.value = _uiState.value.copy(
            tasks = _uiState.value.tasks + task,
            addNewForm = f.copy(
                taskTitle = "",
                taskDescription = "",
                taskSubject = "",
                taskTypeOptional = "",
                taskOccursOnce = true,
                taskDueDate = "",
                taskDueTime = "",
                freeTasksRemaining = (f.freeTasksRemaining - 1).coerceAtLeast(0),
            ),
        )
        return true
    }

    /** Returns false if exam name or subject is blank. */
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
        _uiState.value = _uiState.value.copy(
            exams = _uiState.value.exams + exam,
            addNewForm = f.copy(
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
            ),
        )
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
        _uiState.value = _uiState.value.copy(
            tasks = _uiState.value.tasks.map { t ->
                if (t.id != id) t
                else t.copy(
                    title = title.trim(),
                    details = details.trim(),
                    typeOptional = typeOptional.trim(),
                    occursOnce = occursOnce,
                    dueDateDisplay = dueDateDisplay.trim().ifBlank { formatDisplayDate(ts) },
                    dueTimeDisplay = dueTimeDisplay.trim().ifBlank { formatDisplayTime(ts) },
                    dueTimestamp = ts,
                )
            },
        )
    }

    fun deleteTask(id: String) {
        _uiState.value = _uiState.value.copy(tasks = _uiState.value.tasks.filter { it.id != id })
    }

    fun completeTask(id: String) {
        _uiState.value = _uiState.value.copy(
            tasks = _uiState.value.tasks.map { t ->
                if (t.id != id) t else t.copy(isCompleted = true, progressPercent = 100)
            },
        )
    }

    fun completeAllOverdueTasks() {
        val today = startOfDayMillis(System.currentTimeMillis())
        _uiState.value = _uiState.value.copy(
            tasks = _uiState.value.tasks.map { t ->
                if (!t.isCompleted && t.dueTimestamp < today) {
                    t.copy(isCompleted = true, progressPercent = 100)
                } else t
            },
        )
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
        _uiState.value = _uiState.value.copy(
            exams = _uiState.value.exams.map { e ->
                if (e.id != id) e
                else e.copy(
                    isResit = isResit,
                    examType = examType,
                    inPerson = inPerson,
                    module = module.trim(),
                    subject = module.trim().ifBlank { e.subject },
                    seat = if (inPerson) seat.trim() else "",
                    room = if (inPerson) room.trim() else "",
                    onlineUrl = if (inPerson) "" else onlineUrl.trim(),
                    dateDisplay = dateDisplay.trim().ifBlank { formatDisplayDate(ts) },
                    timeDisplay = timeDisplay.trim().ifBlank { formatDisplayTime(ts) },
                    durationMinutes = durationMinutes.trim(),
                    examTimestamp = ts,
                )
            },
        )
    }

    fun deleteExam(id: String) {
        _uiState.value = _uiState.value.copy(exams = _uiState.value.exams.filter { it.id != id })
    }

    fun getTask(id: String): TaskItem? = _uiState.value.tasks.find { it.id == id }

    fun getExam(id: String): ExamItem? = _uiState.value.exams.find { it.id == id }

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

        /** Date at local midnight from stored display string (for date-only pickers). */
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

        /** Hour 1-12, minute 0-59, isPm */
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
                } catch (_: Exception) {
                }
            }
            if (!parsed) {
                return System.currentTimeMillis()
            }
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
                } catch (_: Exception) {
                }
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
