package com.example.a216155_cikguizwan_lab5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // Added correct import for ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import kotlinx.coroutines.delay
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material.icons.filled.Edit
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.a216155_cikguizwan_lab5.ui.them.MidnightBlue
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.filled.MenuBook
import com.example.a216155_cikguizwan_lab5.ui.them.CleanGrayBg
import java.util.Calendar
import java.util.Locale
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.a216155_cikguizwan_lab5.ui.them.SurfaceWhite
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.animation.core.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.foundation.layout.wrapContentHeight
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import androidx.compose.ui.platform.LocalContext
import android.os.Build
// --- THEME COLORS ---
// --- NEW DEEP MIDNIGHT THEME COLORS ---
val MidnightBlue = Color(0xFF1A237E)      // Primary (Replacing PrimaryPurple)
val AcademicGold = Color(0xFFC5A000)      // Secondary (Replacing SoftPink)
val CleanGrayBg = Color(0xFFFFF0F5)       // Background (Replacing BodyPinkBg)
val BoxBorderBlue = Color(0xFFADCFFF)     // Border (You can keep this or use MidnightBlue)
val TitleDark = Color(0xFF212121)    // Pure White for Cards
val SubtextGray = Color(0xFF616161)       // For small text
val AccentBlueBright = Color(0xFF4A90E2)  // Buttons / chips (matches existing cards)
val OverdueOrange = Color(0xFFFF9800)    // Overdue accents

private enum class TaskListTab { Current, Past, Overdue }

private enum class ExamListTab { Current, Past }

private enum class PomodoroMode { Focus, ShortBreak, LongBreak }

private data class PomodoroSettings(
    val focusMinutes: Int = 15,
    val shortBreakMinutes: Int = 5,
    val longBreakMinutes: Int = 15,
    val longBreakInterval: Int = 2,
    val alertSound: Boolean = true,
)

private val pomodoroMinuteOptions: List<Int> = (1..5).toList() + (10..55 step 5).toList()
private val pomodoroIntervalOptions: List<Int> = (1..10).toList()

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                1001
            )
        }
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    val app = application as StudyMateApplication
                    val viewModel: StudyMateApp = viewModel(
                        factory = StudyMateViewModelFactory(application, app.repository)
                    )

                    // THE NAVHOST: Handle high-level screen navigation
                    NavHost(navController = navController, startDestination = "main_app") {
                        composable("main_app") {
                            StudyMateApp(navController, viewModel)
                        }

                        // THE SAVE LOGIC IS HERE
                        composable("profile_edit") {
                            val state = viewModel.uiState.value
                            EditProfileScreen(
                                currentFirst = state.firstName,
                                currentLast = state.lastName,
                                onSave = { first, last ->
                                    // 1. Update the data in the ViewModel
                                    viewModel.updateUserProfile(first, last)
                                    // 2. Return to the previous screen (Profile Tab)
                                    navController.popBackStack()
                                },
                                onCancel = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StudyMatePreview() {
    val navController = rememberNavController()
    val viewModel: StudyMateApp = viewModel(
        factory = StudyMateViewModelFactory(
            LocalContext.current.applicationContext as android.app.Application,
            (LocalContext.current.applicationContext as StudyMateApplication).repository
        )
    )
    StudyMateApp(navController = navController, viewModel = viewModel)
}

@Composable
fun StudyMateApp(navController: NavHostController, viewModel: StudyMateApp) { // Added Parameters
    var currentPage by remember { mutableStateOf("dashboard") }
    var selectedTaskId by remember { mutableStateOf<String?>(null) }
    var selectedExamId by remember { mutableStateOf<String?>(null) }
    var pomodoroSettings by remember { mutableStateOf(PomodoroSettings()) }
    var pomodoroMode by remember { mutableStateOf(PomodoroMode.Focus) }
    var pomodoroRemainingSeconds by remember { mutableIntStateOf(pomodoroSettings.focusMinutes * 60) }
    var completedFocusSessions by remember { mutableIntStateOf(0) }
    var pomodoroBackTarget by remember { mutableStateOf("menu") }
    val uiState by viewModel.uiState // Get the state from ViewModel

    Scaffold(
        bottomBar = {
            BottomAppBar(containerColor = Color.White, tonalElevation = 8.dp) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { currentPage = "dashboard" }) {
                        Icon(Icons.Filled.Home, null, tint = if (currentPage == "dashboard") MidnightBlue else Color.LightGray)
                    }
                    IconButton(onClick = { currentPage = "calendar" }) {
                        Icon(Icons.Outlined.DateRange, null, tint = if (currentPage == "calendar") MidnightBlue else Color.LightGray)
                    }
                    Spacer(modifier = Modifier.width(60.dp))
                    IconButton(onClick = { currentPage = "menu" }) {
                        Icon(Icons.Default.GridView, null, tint = if (currentPage == "menu") MidnightBlue else Color.LightGray)
                    }
                    IconButton(onClick = { currentPage = "profile" }) {
                        Icon(Icons.Outlined.Person, null, tint = if (currentPage == "profile") MidnightBlue else Color.LightGray)
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { currentPage = "add_new" },

                containerColor = MidnightBlue,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(65.dp).offset(y = 55.dp)
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(30.dp))
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (currentPage) {
                "dashboard" -> DashboardScreen(paddingValues, onSearchClick = { currentPage = "search" })
                "calendar" -> MainCalendarApp()
                "menu" -> MenuScreen(
                    paddingValues = paddingValues,
                    onOpenTasks = { currentPage = "tasks_list" },
                    onOpenExams = { currentPage = "exams_list" },
                    onOpenFocusTimer = {
                        pomodoroBackTarget = "menu"
                        currentPage = "pomodoro_timer"
                    },
                )
                "tasks_list" -> TasksListScreen(
                    paddingValues = paddingValues,
                    viewModel = viewModel,
                    onBack = { currentPage = "menu" },
                    onOpenTask = { id ->
                        selectedTaskId = id
                        currentPage = "task_detail"
                    },
                )
                "task_detail" -> {
                    val id = selectedTaskId
                    if (id == null) {
                        LaunchedEffect(Unit) { currentPage = "tasks_list" }
                        Box(Modifier.fillMaxSize().background(CleanGrayBg))
                    } else {
                        TaskDetailScreen(
                            paddingValues = paddingValues,
                            viewModel = viewModel,
                            taskId = id,
                            onBack = { currentPage = "tasks_list" },
                            onEdit = { currentPage = "update_task" },
                            onOpenPomodoro = {
                                pomodoroBackTarget = "task_detail"
                                currentPage = "pomodoro_timer"
                            },
                        )
                    }
                }
                "update_task" -> {
                    val id = selectedTaskId
                    if (id == null) {
                        LaunchedEffect(Unit) { currentPage = "tasks_list" }
                        Box(Modifier.fillMaxSize().background(CleanGrayBg))
                    } else {
                        UpdateTaskScreen(
                            paddingValues = paddingValues,
                            viewModel = viewModel,
                            taskId = id,
                            onBack = { currentPage = "task_detail" },
                            onDeleted = {
                                selectedTaskId = null
                                currentPage = "tasks_list"
                            },
                        )
                    }
                }
                "exams_list" -> ExamsListScreen(
                    paddingValues = paddingValues,
                    viewModel = viewModel,
                    onBack = { currentPage = "menu" },
                    onOpenExam = { eid ->
                        selectedExamId = eid
                        currentPage = "exam_detail"
                    },
                )
                "exam_detail" -> {
                    val eid = selectedExamId
                    if (eid == null) {
                        LaunchedEffect(Unit) { currentPage = "exams_list" }
                        Box(Modifier.fillMaxSize().background(CleanGrayBg))
                    } else {
                        ExamDetailScreen(
                            paddingValues = paddingValues,
                            viewModel = viewModel,
                            examId = eid,
                            onBack = { currentPage = "exams_list" },
                            onEdit = { currentPage = "update_exam" },
                        )
                    }
                }
                "update_exam" -> {
                    val eid = selectedExamId
                    if (eid == null) {
                        LaunchedEffect(Unit) { currentPage = "exams_list" }
                        Box(Modifier.fillMaxSize().background(CleanGrayBg))
                    } else {
                        UpdateExamScreen(
                            paddingValues = paddingValues,
                            viewModel = viewModel,
                            examId = eid,
                            onBack = { currentPage = "exam_detail" },
                            onDeleted = {
                                selectedExamId = null
                                currentPage = "exams_list"
                            },
                        )
                    }
                }
                "profile" -> ProfileScreen(
                    uiState = uiState, // This passes the data from the ViewModel
                    paddingValues = paddingValues,
                    onEditClick = { navController.navigate("profile_edit") }
                )
                "add_new" -> AddNewScreen(
                    paddingValues = paddingValues,
                    viewModel = viewModel,
                    onBack = { currentPage = "dashboard" },
                    onTaskSaved = { currentPage = "tasks_list" },
                    onExamSaved = { currentPage = "exams_list" },
                )
                "search" -> SearchPage(onBack = { currentPage = "dashboard" })
                "pomodoro_timer" -> PomodoroTimerScreen(
                    paddingValues = paddingValues,
                    settings = pomodoroSettings,
                    mode = pomodoroMode,
                    remainingSeconds = pomodoroRemainingSeconds,
                    completedFocusSessions = completedFocusSessions,
                    onBack = { currentPage = pomodoroBackTarget },
                    onOpenSettings = { currentPage = "pomodoro_settings" },
                    onModeChange = { pomodoroMode = it },
                    onRemainingSecondsChange = { pomodoroRemainingSeconds = it },
                    onCompletedFocusSessionsChange = { completedFocusSessions = it },
                )
                "pomodoro_settings" -> PomodoroSettingsScreen(
                    paddingValues = paddingValues,
                    settings = pomodoroSettings,
                    onBack = { currentPage = "pomodoro_timer" },
                    onSave = { updated ->
                        pomodoroSettings = updated
                        pomodoroRemainingSeconds = when (pomodoroMode) {
                            PomodoroMode.Focus -> updated.focusMinutes * 60
                            PomodoroMode.ShortBreak -> updated.shortBreakMinutes * 60
                            PomodoroMode.LongBreak -> updated.longBreakMinutes * 60
                        }
                    },
                )
                else -> DashboardScreen(paddingValues, onSearchClick = { })
            }
        }
    }
}
@Composable
fun MenuScreen(
    paddingValues: PaddingValues,
    onOpenTasks: () -> Unit = {},
    onOpenExams: () -> Unit = {},
    onOpenFocusTimer: () -> Unit = {},
) {
    val menuItems = listOf(
        Pair("Tasks", "📋"), Pair("Classes", "📚"), Pair("Exams", "✍️"),
        Pair("Vacations", "🏝️"), Pair("Xtra", "⚡"), Pair("Focus Timer", "⏳"),
        Pair("Ai Schedule Scan", "📸"), Pair("Calendar Sync", "🔗"),
        Pair("Settings", "🛠️"), Pair("Schedule Set Up", "🗓️")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CleanGrayBg)
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Menu", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(menuItems) { item ->
                val openable = item.first == "Tasks" || item.first == "Exams" || item.first == "Focus Timer"
                val cardContent: @Composable () -> Unit = {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(item.second, fontSize = 30.sp)
                        Text(
                            item.first,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            color = if (openable) Color.Gray else Color.Gray.copy(alpha = 0.5f)
                        )
                    }
                }
                when (item.first) {
                    "Tasks" -> Card(
                        onClick = onOpenTasks,
                        modifier = Modifier.aspectRatio(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp),
                    ) { cardContent() }
                    "Exams" -> Card(
                        onClick = onOpenExams,
                        modifier = Modifier.aspectRatio(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp),
                    ) { cardContent() }
                    "Focus Timer" -> Card(
                        onClick = onOpenFocusTimer,
                        modifier = Modifier.aspectRatio(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp),
                    ) { cardContent() }
                    else -> Card(
                        modifier = Modifier.aspectRatio(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f)),
                        elevation = CardDefaults.cardElevation(1.dp),
                    ) { cardContent() }
                }
            }
        }
    }
}
private val subjectOptions = listOf(
    "English",
    "Mathematics",
    "Science",
    "Biology",
    "Chemistry",
    "Physics",
    "Music",
    "Geography",
    "History",
    "Computer Science",
)

private val taskTypeOptionalOptions = listOf(
    "Assignment",
    "Reminder",
    "Revision",
    "Essay",
    "Group Project",
    "Reading",
    "Meeting",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelectField(
    label: String,
    value: String,
    placeholder: String,
    options: List<String>,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier) {
        Text(label, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 5.dp))
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text(placeholder, color = Color.Gray) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = MidnightBlue,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                ),
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.filter { it != placeholder }.forEach { opt ->
                    DropdownMenuItem(
                        text = { Text(opt) },
                        onClick = {
                            onSelected(opt)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun FormSelectableChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    showPremiumCrown: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.heightIn(min = 40.dp),
        shape = RoundedCornerShape(12.dp),
        color = if (selected) MidnightBlue else Color.White,
        border = if (!selected) BorderStroke(1.dp, BoxBorderBlue) else null,
    ) {
        Row(
            Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text,
                color = if (selected) Color.White else MidnightBlue,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                maxLines = 1,
            )
            if (showPremiumCrown) {
                Spacer(Modifier.width(4.dp))
                Icon(
                    Icons.Default.WorkspacePremium,
                    contentDescription = null,
                    tint = if (selected) AcademicGold else MidnightBlue,
                    modifier = Modifier.size(14.dp),
                )
            }
        }
    }
}

@Composable
fun LabelWithOptionalHelp(label: String, showHelp: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, fontWeight = FontWeight.Bold)
        if (showHelp) {
            Spacer(Modifier.width(6.dp))
            Icon(
                Icons.Outlined.HelpOutline,
                contentDescription = null,
                tint = SubtextGray,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
fun VacationPhotoUploadArea() {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Photo", fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(6.dp))
            Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = MidnightBlue, modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .drawBehind {
                    drawRoundRect(
                        color = BoxBorderBlue,
                        style = Stroke(
                            width = 2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f),
                        ),
                        cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx()),
                    )
                }
                .padding(16.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Box(
                    Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MidnightBlue.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.Image, contentDescription = null, tint = MidnightBlue.copy(alpha = 0.45f), modifier = Modifier.size(36.dp))
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Filled.CloudUpload, contentDescription = null, tint = MidnightBlue)
                    Text("Click to upload image", fontWeight = FontWeight.Bold, color = MidnightBlue, fontSize = 14.sp)
                    Text("Max. File Size: 5MB", fontSize = 12.sp, color = SubtextGray)
                }
            }
        }
    }
}

@Composable
fun VacationScheduleHintCard(onGoScheduleSettings: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BoxBorderBlue),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Want holidays to adjust your rotation?", fontWeight = FontWeight.Bold, color = TitleDark)
            Text(
                "If you're using a rotation schedule and want holidays to shift your classes forward instead of skipping, you can manage this in your Schedule Settings.",
                fontSize = 13.sp,
                color = SubtextGray,
            )
            OutlinedButton(
                onClick = onGoScheduleSettings,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MidnightBlue),
            ) {
                Text("Go to Schedule Settings", color = MidnightBlue)
            }
        }
    }
}

@Composable
fun AddNewTasksForm(form: AddNewFormState, onUpdate: ((AddNewFormState) -> AddNewFormState) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        CustomInputField(
            label = "Title",
            value = form.taskTitle,
            onValueChange = { v -> onUpdate { it.copy(taskTitle = v) } },
            hint = "Task Title",
        )
        CustomInputField(
            label = "Details",
            value = form.taskDescription,
            onValueChange = { v -> onUpdate { it.copy(taskDescription = v) } },
            hint = "Task description",
            singleLine = false,
            minLines = 3,
            maxLines = 6,
        )
        DropdownSelectField(
            label = "Subject",
            value = form.taskSubject,
            placeholder = "Select subject",
            options = subjectOptions,
            onSelected = { v -> onUpdate { it.copy(taskSubject = v) } },
        )
        DropdownSelectField(
            label = "Type (Optional)",
            value = form.taskTypeOptional,
            placeholder = "Task type",
            options = taskTypeOptionalOptions,
            onSelected = { v -> onUpdate { it.copy(taskTypeOptional = v) } },
        )
        Text("Occurs", fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FormSelectableChip(text = "Once", selected = form.taskOccursOnce, onClick = { onUpdate { it.copy(taskOccursOnce = true) } })
            FormSelectableChip(
                text = "Repeating",
                selected = !form.taskOccursOnce,
                onClick = { onUpdate { it.copy(taskOccursOnce = false) } },
                showPremiumCrown = true,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.weight(1f)) {
                StandardDatePickerField(
                    label = "Due Date",
                    value = form.taskDueDate,
                    onValueChange = { v -> onUpdate { it.copy(taskDueDate = v) } },
                    hint = "Fri, 4 Mar 2022",
                )
            }
            Box(Modifier.weight(1f)) {
                StandardTimePickerField(
                    label = "Time",
                    value = form.taskDueTime,
                    onValueChange = { v -> onUpdate { it.copy(taskDueTime = v) } },
                    hint = "10:30 AM",
                )
            }
        }
    }
}

@Composable
fun AddNewClassesForm(form: AddNewFormState, onUpdate: ((AddNewFormState) -> AddNewFormState) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Mode", fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FormSelectableChip(
                text = "In Person",
                selected = form.classModeInPerson,
                onClick = { onUpdate { it.copy(classModeInPerson = true) } },
                modifier = Modifier.weight(1f),
            )
            FormSelectableChip(
                text = "Online",
                selected = !form.classModeInPerson,
                onClick = { onUpdate { it.copy(classModeInPerson = false) } },
                modifier = Modifier.weight(1f),
            )
        }
        CustomInputField(
            label = "Class",
            value = form.className,
            onValueChange = { v -> onUpdate { it.copy(className = v) } },
            hint = "Class Name",
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.weight(1f)) {
                CustomInputField(label = "Room", value = form.classRoom, onValueChange = { v -> onUpdate { it.copy(classRoom = v) } }, hint = "Room")
            }
            Box(Modifier.weight(1f)) {
                CustomInputField(label = "Building", value = form.classBuilding, onValueChange = { v -> onUpdate { it.copy(classBuilding = v) } }, hint = "Building")
            }
        }
        CustomInputField(
            label = "Teacher",
            value = form.classTeacher,
            onValueChange = { v -> onUpdate { it.copy(classTeacher = v) } },
            hint = "Teacher Name",
        )
        DropdownSelectField(
            label = "Subject",
            value = form.classSubject,
            placeholder = "Select subject",
            options = subjectOptions,
            onSelected = { v -> onUpdate { it.copy(classSubject = v) } },
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            LabelWithOptionalHelp(label = "Start/End Dates", showHelp = true)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FormSelectableChip(
                    text = "None",
                    selected = form.classDateRangeType == "None",
                    onClick = { onUpdate { it.copy(classDateRangeType = "None") } },
                    modifier = Modifier.weight(1f),
                )
                FormSelectableChip(
                    text = "Academic year/term",
                    selected = form.classDateRangeType == "Academic",
                    onClick = { onUpdate { it.copy(classDateRangeType = "Academic") } },
                    modifier = Modifier.weight(1f),
                )
                FormSelectableChip(
                    text = "Manual",
                    selected = form.classDateRangeType == "Manual",
                    onClick = { onUpdate { it.copy(classDateRangeType = "Manual") } },
                    modifier = Modifier.weight(1f),
                )
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            LabelWithOptionalHelp(label = "Occurs", showHelp = true)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FormSelectableChip(text = "Once", selected = form.classOccursOnce, onClick = { onUpdate { it.copy(classOccursOnce = true) } })
                FormSelectableChip(
                    text = "Repeating",
                    selected = !form.classOccursOnce,
                    onClick = { onUpdate { it.copy(classOccursOnce = false) } },
                    showPremiumCrown = true,
                )
            }
        }
        StandardDatePickerField(
            label = "Date",
            value = form.classDate,
            onValueChange = { v -> onUpdate { it.copy(classDate = v) } },
            hint = "Fri, 4 Mar 2022",
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.weight(1f)) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Start Time", fontWeight = FontWeight.Bold)
                        Text("*", color = Color(0xFFD32F2F), modifier = Modifier.padding(start = 2.dp))
                    }
                    Spacer(Modifier.height(5.dp))
                    StandardTimePickerField(
                        label = "",
                        value = form.classStartTime,
                        onValueChange = { v -> onUpdate { it.copy(classStartTime = v) } },
                        hint = "4:00 PM",
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            Box(Modifier.weight(1f)) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("End Time", fontWeight = FontWeight.Bold)
                        Text("*", color = Color(0xFFD32F2F), modifier = Modifier.padding(start = 2.dp))
                    }
                    Spacer(Modifier.height(5.dp))
                    StandardTimePickerField(
                        label = "",
                        value = form.classEndTime,
                        onValueChange = { v -> onUpdate { it.copy(classEndTime = v) } },
                        hint = "5:00 PM",
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
fun AddNewExamsForm(form: AddNewFormState, onUpdate: ((AddNewFormState) -> AddNewFormState) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        CustomInputField(
            label = "Exam",
            value = form.examName,
            onValueChange = { v -> onUpdate { it.copy(examName = v) } },
            hint = "Exam Name",
        )
        DropdownSelectField(
            label = "Subject",
            value = form.examSubject,
            placeholder = "Select subject",
            options = subjectOptions,
            onSelected = { v -> onUpdate { it.copy(examSubject = v) } },
        )
        Text("Type", fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FormSelectableChip(
                text = "Exam",
                selected = form.examType == "Exam",
                onClick = { onUpdate { it.copy(examType = "Exam") } },
                modifier = Modifier.weight(1f),
            )
            FormSelectableChip(
                text = "Quiz",
                selected = form.examType == "Quiz",
                onClick = { onUpdate { it.copy(examType = "Quiz") } },
                showPremiumCrown = true,
                modifier = Modifier.weight(1f),
            )
            FormSelectableChip(
                text = "Test",
                selected = form.examType == "Test",
                onClick = { onUpdate { it.copy(examType = "Test") } },
                showPremiumCrown = true,
                modifier = Modifier.weight(1f),
            )
        }
        Text("Mode", fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FormSelectableChip(
                text = "In-person",
                selected = form.examModeInPerson,
                onClick = { onUpdate { it.copy(examModeInPerson = true) } },
                modifier = Modifier.weight(1f),
            )
            FormSelectableChip(
                text = "Online",
                selected = !form.examModeInPerson,
                onClick = { onUpdate { it.copy(examModeInPerson = false) } },
                modifier = Modifier.weight(1f),
            )
        }
        if (form.examModeInPerson) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(Modifier.weight(1f)) {
                    CustomInputField(label = "Seat", value = form.examSeat, onValueChange = { v -> onUpdate { it.copy(examSeat = v) } }, hint = "Seat #")
                }
                Box(Modifier.weight(1f)) {
                    CustomInputField(label = "Room", value = form.examRoom, onValueChange = { v -> onUpdate { it.copy(examRoom = v) } }, hint = "Room")
                }
            }
        } else {
            CustomInputField(
                label = "URL",
                value = form.examOnlineUrl,
                onValueChange = { v -> onUpdate { it.copy(examOnlineUrl = v) } },
                hint = "Paste meeting link",
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.weight(1f)) {
                StandardDatePickerField(
                    label = "Date",
                    value = form.examDate,
                    onValueChange = { v -> onUpdate { it.copy(examDate = v) } },
                    hint = "Fri, 4 Mar 2022",
                )
            }
            Box(Modifier.weight(1f)) {
                StandardTimePickerField(
                    label = "Time",
                    value = form.examTime,
                    onValueChange = { v -> onUpdate { it.copy(examTime = v) } },
                    hint = "10:30 AM",
                )
            }
        }
        CustomInputField(
            label = "Duration (In minutes)",
            value = form.examDurationMinutes,
            onValueChange = { v -> onUpdate { it.copy(examDurationMinutes = v.filter { ch -> ch.isDigit() }) } },
            hint = "Duration (In minutes)",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
    }
}

@Composable
fun AddNewVacationsForm(form: AddNewFormState, onUpdate: ((AddNewFormState) -> AddNewFormState) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        CustomInputField(
            label = "Name",
            value = form.vacationName,
            onValueChange = { v -> onUpdate { it.copy(vacationName = v) } },
            hint = "Vacations Name",
        )
        CustomInputField(
            label = "Details",
            value = form.vacationDetails,
            onValueChange = { v -> onUpdate { it.copy(vacationDetails = v) } },
            hint = "Vacations description",
            singleLine = false,
            minLines = 3,
            maxLines = 6,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.weight(1f)) {
                StandardDatePickerField(
                    label = "Start Date",

                    value = form.vacationStartDate,
                    onValueChange = { v -> onUpdate { it.copy(vacationStartDate = v) } },
                    hint = "Fri, 4 Mar 2022",
                )
            }
            Box(Modifier.weight(1f)) {
                StandardDatePickerField(
                    label = "End Date",
                    value = form.vacationEndDate,
                    onValueChange = { v -> onUpdate { it.copy(vacationEndDate = v) } },
                    hint = "Fri, 4 Mar 2022",
                )
            }
        }
        VacationPhotoUploadArea()
        VacationScheduleHintCard()
    }
}

@Composable
fun AddNewXtraForm(form: AddNewFormState, onUpdate: ((AddNewFormState) -> AddNewFormState) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        CustomInputField(
            label = "Title",
            value = form.xtraTitle,
            onValueChange = { v -> onUpdate { it.copy(xtraTitle = v) } },
            hint = "Xtra title",
        )
        CustomInputField(
            label = "Details",
            value = form.xtraDetails,
            onValueChange = { v -> onUpdate { it.copy(xtraDetails = v) } },
            hint = "Description",
            singleLine = false,
            minLines = 3,
            maxLines = 6,
        )
        CustomInputField(
            label = "Note",
            value = form.xtraNote,
            onValueChange = { v -> onUpdate { it.copy(xtraNote = v) } },
            hint = "Optional note",
            singleLine = false,
            minLines = 2,
            maxLines = 4,
        )
    }
}

@Composable
fun AddNewScreen(
    paddingValues: PaddingValues,
    viewModel: StudyMateApp,
    onBack: () -> Unit,
    onTaskSaved: () -> Unit = onBack,
    onExamSaved: () -> Unit = onBack,
) {
    val uiState by viewModel.uiState
    val form = uiState.addNewForm
    val categories = listOf("Tasks", "Classes", "Exams", "Vacations", "Xtra")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CleanGrayBg)
            .padding(paddingValues)
            .verticalScroll(rememberScrollState()),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = MidnightBlue)
            }
            Text("Add New", modifier = Modifier.align(Alignment.Center), fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            categories.forEach { name ->
                val isSelected = form.selectedCategory == name
                Button(
                    onClick = { viewModel.setAddNewCategory(name) },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isSelected) MidnightBlue else Color.White),
                    border = if (!isSelected) BorderStroke(1.dp, BoxBorderBlue) else null,
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(name, color = if (isSelected) Color.White else MidnightBlue)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            when (form.selectedCategory) {
                "Tasks" -> AddNewTasksForm(form) { viewModel.updateAddNewForm(it) }
                "Classes" -> AddNewClassesForm(form) { viewModel.updateAddNewForm(it) }
                "Exams" -> AddNewExamsForm(form) { viewModel.updateAddNewForm(it) }
                "Vacations" -> AddNewVacationsForm(form) { viewModel.updateAddNewForm(it) }
                "Xtra" -> AddNewXtraForm(form) { viewModel.updateAddNewForm(it) }
            }
        }

        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            when (form.selectedCategory) {
                "Tasks" -> {
                    Button(
                        onClick = {
                            if (viewModel.saveNewTaskFromForm()) onTaskSaved()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        colors = ButtonDefaults.buttonColors(MidnightBlue),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text("Save", fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        border = BorderStroke(1.dp, MidnightBlue),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text("Cancel", color = MidnightBlue)
                    }
                }
                else -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        OutlinedButton(
                            onClick = onBack,
                            modifier = Modifier
                                .weight(1f)
                                .height(55.dp),
                            border = BorderStroke(1.dp, MidnightBlue),
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Text("Cancel", color = MidnightBlue)
                        }
                        Button(
                            onClick = {
                                when (form.selectedCategory) {
                                    "Exams" -> if (viewModel.saveNewExamFromForm()) onExamSaved()
                                    else -> { }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(55.dp),
                            colors = ButtonDefaults.buttonColors(MidnightBlue),
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            val saveLabel = when (form.selectedCategory) {
                                "Classes" -> "Save Class"
                                "Exams" -> "Save Exam"
                                "Vacations" -> "Save Vacations"
                                else -> "Save Xtra"
                            }
                            Text(saveLabel, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    isDropdown: Boolean = false,
    modifier: Modifier = Modifier.fillMaxWidth(),
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Sentences),
) {
    Column(modifier) {
        Text(label, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 5.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(hint, color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = singleLine,
            minLines = minLines,
            maxLines = maxLines,
            keyboardOptions = keyboardOptions,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = MidnightBlue,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
            ),
            trailingIcon = if (isDropdown) {
                { Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.Gray) }
            } else null,
            readOnly = isDropdown,
        )
    }
}

private val WheelItemHeight = 44.dp
private val WheelVisibleHeight = 220.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WheelPickerColumn(
    items: List<String>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (items.isEmpty()) return
    val count = items.size
    val safeIdx = selectedIndex.coerceIn(0, count - 1)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = safeIdx)
    val snap = rememberSnapFlingBehavior(lazyListState = listState)
    val padV = (WheelVisibleHeight - WheelItemHeight) / 2

    LaunchedEffect(count, safeIdx) {
        listState.scrollToItem(safeIdx)
    }

    LaunchedEffect(listState, count) {
        snapshotFlow { listState.isScrollInProgress }
            .distinctUntilChanged()
            .filter { !it }
            .collect {
                delay(20)
                val info = listState.layoutInfo
                if (info.visibleItemsInfo.isEmpty()) return@collect
                val mid = (info.viewportStartOffset + info.viewportEndOffset) / 2f
                val best = info.visibleItemsInfo.minByOrNull { vi ->
                    kotlin.math.abs(vi.offset + vi.size / 2f - mid)
                } ?: return@collect
                val i = best.index.coerceIn(0, count - 1)
                if (i != safeIdx) onSelectedIndexChange(i)
            }
    }

    Box(Modifier.then(modifier).height(WheelVisibleHeight)) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(WheelItemHeight)
                .align(Alignment.Center)
                .background(Color(0xFFDDE3EA).copy(alpha = 0.55f), RoundedCornerShape(10.dp)),
        )
        LazyColumn(
            state = listState,
            flingBehavior = snap,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = padV),
        ) {
            items(count) { i ->
                Text(
                    items[i],
                    modifier = Modifier
                        .height(WheelItemHeight)
                        .fillMaxWidth()
                        .wrapContentHeight(Alignment.CenterVertically),
                    textAlign = TextAlign.Center,
                    fontSize = if (i == safeIdx) 20.sp else 15.sp,
                    color = if (i == safeIdx) TitleDark else SubtextGray.copy(alpha = 0.4f),
                    fontWeight = if (i == safeIdx) FontWeight.SemiBold else FontWeight.Normal,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun WheelDateSheetBody(
    initialDateStr: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    val years = remember { (2020..2035).map { it.toString() } }
    val monthLabels = remember {
        DateFormatSymbols(Locale.ENGLISH).shortMonths.filter { it.isNotEmpty() }
    }
    val initCal = remember(initialDateStr) { StudyMateApp.parseCalendarDateOnly(initialDateStr) }
    var yearIdx by remember(initialDateStr) {
        mutableIntStateOf(years.indexOf(initCal.get(Calendar.YEAR).toString()).coerceIn(0, years.lastIndex))
    }
    var monthIdx by remember(initialDateStr) {
        mutableIntStateOf(initCal.get(Calendar.MONTH).coerceIn(0, 11))
    }
    var dayIdx by remember(initialDateStr) {
        val dim = initCal.getActualMaximum(Calendar.DAY_OF_MONTH)
        mutableIntStateOf((initCal.get(Calendar.DAY_OF_MONTH) - 1).coerceIn(0, dim - 1))
    }

    fun monthDayCount(): Int {
        val y = years[yearIdx].toInt()
        val c = Calendar.getInstance()
        c.set(Calendar.YEAR, y)
        c.set(Calendar.MONTH, monthIdx)
        return c.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    val dayItems = remember(yearIdx, monthIdx) {
        val dim = monthDayCount()
        (1..dim).map { it.toString() }
    }

    LaunchedEffect(yearIdx, monthIdx) {
        val dim = dayItems.size
        if (dayIdx > dim - 1) dayIdx = (dim - 1).coerceAtLeast(0)
    }

    Column(Modifier.fillMaxWidth().padding(bottom = 28.dp)) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = onDismiss) { Text("Cancel", color = MidnightBlue) }
            TextButton(
                onClick = {
                    val y = years[yearIdx].toInt()
                    val c = Calendar.getInstance()
                    c.set(Calendar.YEAR, y)
                    c.set(Calendar.MONTH, monthIdx)
                    c.set(Calendar.DAY_OF_MONTH, dayIdx + 1)
                    c.set(Calendar.HOUR_OF_DAY, 0)
                    c.set(Calendar.MINUTE, 0)
                    c.set(Calendar.SECOND, 0)
                    c.set(Calendar.MILLISECOND, 0)
                    onConfirm(StudyMateApp.calendarToStandardDate(c))
                },
            ) {
                Text("Done", color = MidnightBlue, fontWeight = FontWeight.Bold)
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WheelPickerColumn(years, yearIdx, { yearIdx = it }, Modifier.weight(1f))
            WheelPickerColumn(monthLabels, monthIdx, { monthIdx = it }, Modifier.weight(1f))
            key(yearIdx, monthIdx) {
                WheelPickerColumn(dayItems, dayIdx, { dayIdx = it }, Modifier.weight(1f))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun WheelTimeSheetBody(
    initialTimeStr: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    val hourItems = remember { (1..12).map { it.toString().padStart(2, '0') } }
    val minItems = remember { (0..59).map { it.toString().padStart(2, '0') } }
    val ampmItems = remember { listOf("AM", "PM") }
    val parts = remember(initialTimeStr) { StudyMateApp.parseTimePartsFromString(initialTimeStr) }
    var hourIdx by remember(initialTimeStr) { mutableIntStateOf((parts.first - 1).coerceIn(0, 11)) }
    var minIdx by remember(initialTimeStr) { mutableIntStateOf(parts.second.coerceIn(0, 59)) }
    var ampmIdx by remember(initialTimeStr) { mutableIntStateOf(if (parts.third) 1 else 0) }

    Column(Modifier.fillMaxWidth().padding(bottom = 28.dp)) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = onDismiss) { Text("Cancel", color = MidnightBlue) }
            TextButton(
                onClick = {
                    onConfirm(StudyMateApp.formatStandardTime12h(hourIdx + 1, minIdx, ampmIdx == 1))
                },
            ) {
                Text("Done", color = MidnightBlue, fontWeight = FontWeight.Bold)
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            WheelPickerColumn(hourItems, hourIdx, { hourIdx = it }, Modifier.weight(1f))
            Text(":", fontSize = 22.sp, color = SubtextGray, modifier = Modifier.padding(horizontal = 2.dp))
            WheelPickerColumn(minItems, minIdx, { minIdx = it }, Modifier.weight(1f))
            Text(":", fontSize = 22.sp, color = SubtextGray, modifier = Modifier.padding(horizontal = 2.dp))
            WheelPickerColumn(ampmItems, ampmIdx, { ampmIdx = it }, Modifier.weight(1f))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun StandardDatePickerField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    hint: String = "Fri, 4 Mar 2022",
    required: Boolean = false,
) {
    var open by remember { mutableStateOf(false) }
    Column(modifier) {
        if (label.isNotEmpty() || required) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 5.dp),
            ) {
                if (label.isNotEmpty()) {
                    Text(label, fontWeight = FontWeight.Bold)
                }
                if (required) {
                    Text(
                        "*",
                        color = Color(0xFFD32F2F),
                        modifier = Modifier.padding(start = if (label.isNotEmpty()) 2.dp else 0.dp),
                    )
                }
            }
        }
        Box(Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text(hint, color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                trailingIcon = {
                    Icon(Icons.Outlined.CalendarMonth, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(22.dp))
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = MidnightBlue,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                ),
            )
            Box(
                Modifier
                    .matchParentSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { open = true },
            )
        }
    }
    if (open) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        LaunchedEffect(sheetState) { sheetState.expand() }
        ModalBottomSheet(
            onDismissRequest = { open = false },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp),
            containerColor = Color(0xFFF5F7FA),
        ) {
            WheelDateSheetBody(
                initialDateStr = value,
                onDismiss = { open = false },
                onConfirm = { s ->
                    onValueChange(s)
                    open = false
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun StandardTimePickerField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    hint: String = "10:30 AM",
    required: Boolean = false,
) {
    var open by remember { mutableStateOf(false) }
    Column(modifier) {
        if (label.isNotEmpty() || required) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 5.dp),
            ) {
                if (label.isNotEmpty()) {
                    Text(label, fontWeight = FontWeight.Bold)
                }
                if (required) {
                    Text(
                        "*",
                        color = Color(0xFFD32F2F),
                        modifier = Modifier.padding(start = if (label.isNotEmpty()) 2.dp else 0.dp),
                    )
                }
            }
        }
        Box(Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text(hint, color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                trailingIcon = {
                    Icon(Icons.Outlined.Schedule, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(22.dp))
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = MidnightBlue,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                ),
            )
            Box(
                Modifier
                    .matchParentSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { open = true },
            )
        }
    }
    if (open) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        LaunchedEffect(sheetState) { sheetState.expand() }
        ModalBottomSheet(
            onDismissRequest = { open = false },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp),
            containerColor = Color(0xFFF5F7FA),
        ) {
            WheelTimeSheetBody(
                initialTimeStr = value,
                onDismiss = { open = false },
                onConfirm = { s ->
                    onValueChange(s)
                    open = false
                },
            )
        }
    }
}

@Composable
fun ProfileScreen(
    uiState: UiState,
    paddingValues: PaddingValues,
    onEditClick: () -> Unit
) {
    // 1. Create the scroll state
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CleanGrayBg)
            .padding(paddingValues)
            // 2. Add the verticalScroll modifier here
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- Top Bar ---
        Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text("Profile", modifier = Modifier.align(Alignment.Center), fontSize = 20.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = onEditClick, modifier = Modifier.align(Alignment.CenterEnd)) {
                Icon(Icons.Default.Edit, null, tint = Color.Gray)
            }
        }

        // --- Profile Image ---
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .drawBehind {
                        drawCircle(
                            color = MidnightBlue,
                            style = Stroke(
                                width = 2.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                            )
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("Profile Picture", fontSize = 12.sp, color = Color.Gray)
            }

            Surface(
                modifier = Modifier.size(35.dp).offset(x = (-4).dp, y = (-4).dp),
                shape = CircleShape,
                color = MidnightBlue,
                shadowElevation = 4.dp
            ) {
                Icon(Icons.Default.Edit, "Edit", tint = Color.White, modifier = Modifier.padding(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "${uiState.firstName} ${uiState.lastName}", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("sharmilakjvadivazagan@gmail.com", fontSize = 14.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(24.dp))

        // --- Stat Cards ---
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ProfileStatCard("👀 Pending Tasks", "0", "Next 7 Days", Modifier.weight(1f))
                ProfileStatCard("⚠️ Overdue Tasks", "1", "Total", Modifier.weight(1f), countColor = Color.Red)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ProfileStatCard("👍 Tasks Completed", "0", "Last 7 Days", Modifier.weight(1f))
                ProfileStatCard("🔥 Your Streak", "0", "Total streak", Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Menu Options ---
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            ProfileOptionItem(Icons.Default.WorkspacePremium, "Premium Subscription", MidnightBlue)
            ProfileOptionItem(Icons.Default.ExitToApp, "Log out", MidnightBlue)

            Spacer(modifier = Modifier.height(20.dp))

            // Now you will be able to scroll down to see this!
            Text(
                "Delete Account",
                color = Color(0xFFE91E63),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp), // Added bottom padding so it's not hugging the edge
                textAlign = TextAlign.Center
            )
        }
    }
}
@Composable
fun EditProfileScreen(
    currentFirst: String,
    currentLast: String,
    onSave: (String, String) -> Unit,
    onCancel: () -> Unit
) {
    var firstName by remember { mutableStateOf(currentFirst) }
    var lastName by remember { mutableStateOf(currentLast) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CleanGrayBg)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onCancel) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                "Edit Profile",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(48.dp)) // To balance the back button
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Profile Picture with Dashed Border
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .drawBehind {
                        drawCircle(
                            color = MidnightBlue,
                            style = Stroke(
                                width = 2.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                            )
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("Profile Picture", fontSize = 12.sp, color = Color.Gray)
            }

            // Edit Icon Badge
            Surface(
                modifier = Modifier.size(32.dp),
                shape = CircleShape,
                color = MidnightBlue,
                shadowElevation = 2.dp
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Picture",
                    tint = Color.White,
                    modifier = Modifier.padding(6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Input Fields
        EditField("First Name", firstName) { firstName = it }
        Spacer(modifier = Modifier.height(16.dp))
        EditField("Last Name", lastName) { lastName = it }

        Spacer(modifier = Modifier.weight(1f))

        // Save Button
        Button(
            onClick = { onSave(firstName, lastName) },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MidnightBlue),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Save", fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Cancel Button (Outlined)
        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.5.dp, MidnightBlue)
        ) {
            Text("Cancel", fontWeight = FontWeight.Bold, color = MidnightBlue)
        }
    }
}



// --- HELPER COMPONENTS ---

@Composable
fun ProfileStatCard(title: String, count: String, subtitle: String, modifier: Modifier, countColor: Color = Color(0xFFFFB300)) {
    Surface(modifier = modifier.height(110.dp), shape = RoundedCornerShape(16.dp), color = Color.White, shadowElevation = 2.dp) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Text(count, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = countColor)
            Text(subtitle, fontSize = 11.sp, color = Color.Gray)
        }
    }
}

@Composable
fun ProfileOptionItem(icon: ImageVector, label: String, color: Color) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, modifier = Modifier.weight(1f), fontSize = 16.sp)
        Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
    }
}

@Composable
fun EditField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(value = value, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MidnightBlue))
    }
}
@Composable
fun DashboardScreen(paddingValues: PaddingValues, onSearchClick: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxSize()
        .background(com.example.a216155_cikguizwan_lab5.CleanGrayBg)
        .padding(paddingValues)
        .verticalScroll(rememberScrollState())) {
        HeaderSection(onSearchClick)

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatBox("0 Classes")
            StatBox("0 Exams")
            StatBox("0 Tasks Due")
            StatBox("0 Calendar Sync")
        }

        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            // Fixed the icon error by using a built-in Material icon instead of a potentially missing resource
            Icon(Icons.Default.MenuBook, null, tint = MidnightBlue.copy(alpha = 0.4f), modifier = Modifier.size(130.dp))
            Spacer(modifier = Modifier.height(20.dp))
            Text("No classes, tasks or exams left for today", fontSize = 16.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "You can adjust what is displayed on your homepage in personalised settings.",
                fontSize = 14.sp, color = MidnightBlue, textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 45.dp)
            )
        }
    }
}

@Composable
fun MainCalendarApp() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var currentView by remember { mutableStateOf("week") }
    var calendarInstance by remember { mutableStateOf(Calendar.getInstance()) }

    var selectedFilters by remember { mutableStateOf(setOf("Classes", "Exams", "Tasks", "Holidays", "Extra", "ICal", "Events")) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFFF8F9FD),
                modifier = Modifier.width(300.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("View Mode", fontWeight = FontWeight.Bold, color = MidnightBlue, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(20.dp))

                    val views = listOf(
                        "day" to Icons.Default.DateRange,
                        "week" to Icons.Default.Refresh,
                        "month" to Icons.Default.Menu
                    )

                    views.forEach { (view, icon) ->
                        val isSelected = currentView == view
                        Surface(
                            onClick = {
                                currentView = view
                                scope.launch { drawerState.close() }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            color = if (isSelected) Color(0xFFE8EAF6) else Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(icon, null, tint = MidnightBlue, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    view.replaceFirstChar { it.uppercase() },
                                    fontWeight = FontWeight.SemiBold,
                                    color = MidnightBlue
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Filters", fontWeight = FontWeight.Bold, color = MidnightBlue, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    val filterList = listOf("Classes", "Exams", "Tasks", "Holidays", "Extra", "ICal", "Events")
                    filterList.forEach { filter ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedFilters = if (selectedFilters.contains(filter)) {
                                        selectedFilters - filter
                                    } else {
                                        selectedFilters + filter
                                    }
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(filter, color = MidnightBlue, fontWeight = FontWeight.Medium)
                            if (selectedFilters.contains(filter)) {
                                Icon(Icons.Default.Check, null, tint = MidnightBlue, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }
    ) {
        Scaffold(
            containerColor = CleanGrayBg,
            topBar = {
                CalendarHeader(
                    calendarInstance = calendarInstance,
                    currentView = currentView,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onPrev = {
                        val newCal = calendarInstance.clone() as Calendar
                        if (currentView == "month") newCal.add(Calendar.MONTH, -1)
                        else newCal.add(Calendar.WEEK_OF_YEAR, -1)
                        calendarInstance = newCal
                    },
                    onNext = {
                        val newCal = calendarInstance.clone() as Calendar
                        if (currentView == "month") newCal.add(Calendar.MONTH, 1)
                        else newCal.add(Calendar.WEEK_OF_YEAR, 1)
                        calendarInstance = newCal
                    },
                    onToday = { calendarInstance = Calendar.getInstance() }
                )
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                when (currentView) {
                    "day" -> DayView(calendarInstance)
                    "week" -> WeekView(calendarInstance)
                    "month" -> MonthView(calendarInstance)
                }
            }
        }
    }
}
@Composable
fun CalendarHeader(
    calendarInstance: Calendar,
    currentView: String,
    onMenuClick: () -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onToday: () -> Unit
) {
    val format = if (currentView == "month") "MMMM yyyy" else "MMMM yyyy"
    val label = SimpleDateFormat(format, Locale.ENGLISH).format(calendarInstance.time)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 3-Line Menu Button
        IconButton(onClick = onMenuClick) {
            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.Gray)
        }

        // Month/Year Selector
        Surface(shape = RoundedCornerShape(24.dp), color = Color.White, shadowElevation = 2.dp) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp)) {
                IconButton(onClick = onPrev) { Icon(Icons.Default.KeyboardArrowLeft, null, tint = MidnightBlue) }
                Text(text = label, fontWeight = FontWeight.Bold, color = Color.Black)
                IconButton(onClick = onNext) { Icon(Icons.Default.KeyboardArrowRight, null, tint = MidnightBlue) }
            }
        }

        // Today Button
        OutlinedButton(
            onClick = onToday,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(2.dp, com.example.a216155_cikguizwan_lab5.CleanGrayBg)
        ) {
            Text("Today", color = MidnightBlue, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DayView(calendar: Calendar) {
    val dayName = SimpleDateFormat("EEE", Locale.ENGLISH).format(calendar.time).uppercase()
    val dateNum = SimpleDateFormat("d", Locale.ENGLISH).format(calendar.time)

    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(start = 20.dp, bottom = 8.dp)) {
            Text(dayName, color = MidnightBlue.copy(alpha = 0.6f), fontSize = 12.sp)
            Surface(shape = RoundedCornerShape(50), color = MidnightBlue, modifier = Modifier.size(32.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Text(dateNum, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
        Box(modifier = Modifier.verticalScroll(rememberScrollState())) {
            TimeGrid(columns = 1)
        }
    }
}

@Composable
fun WeekView(calendar: Calendar) {
    val weekDates = remember(calendar) {
        val cal = calendar.clone() as Calendar
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        (0..6).map {
            val dName = SimpleDateFormat("E", Locale.ENGLISH).format(cal.time).first().toString()
            val dNum = cal.get(Calendar.DAY_OF_MONTH).toString()
            cal.add(Calendar.DAY_OF_MONTH, 1)
            dName to dNum
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 60.dp)) {
            weekDates.forEach { (name, num) ->
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(name, fontSize = 12.sp, color = Color.Gray)
                    Text(num, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
        Box(modifier = Modifier.verticalScroll(rememberScrollState())) {
            TimeGrid(columns = 7)
        }
    }
}

@Composable
fun MonthView(calendar: Calendar) {
    val daysHeader = listOf("M", "T", "W", "T", "F", "S", "S")
    val cal = calendar.clone() as Calendar
    cal.set(Calendar.DAY_OF_MONTH, 1)

    val firstDayOffset = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7
    val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 8.dp)) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)) {
            daysHeader.forEach { day ->
                Text(day, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        val totalCells = firstDayOffset + maxDays
        val rows = (totalCells + 6) / 7

        for (i in 0 until rows) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)) {
                for (j in 0..6) {
                    val dateIndex = i * 7 + j
                    val dateNum = dateIndex - firstDayOffset + 1

                    Box(modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .border(0.2.dp, Color.LightGray)) {
                        if (dateNum in 1..maxDays) {
                            Text("$dateNum", modifier = Modifier.padding(4.dp), fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimeGrid(columns: Int) {
    Column {
        (0..23).forEach { hour ->
            val timeLabel = "${if (hour % 12 == 0) 12 else hour % 12} ${if (hour < 12) "AM" else "PM"}"
            Row(modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)) {
                Text(
                    text = timeLabel,
                    modifier = Modifier
                        .width(60.dp)
                        .padding(top = 8.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                repeat(columns) {
                    Box(modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .border(0.3.dp, Color.LightGray.copy(alpha = 0.5f)))
                }
            }
        }
    }
}
@Composable
fun HeaderSection(onSearchClick: () -> Unit) {
    val sdfDay = SimpleDateFormat("EEEE", Locale.ENGLISH).format(Date())
    val sdfDate = SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).format(Date())
    val waveShape = GenericShape { size, _ ->
        lineTo(0f, size.height * 0.82f)
        quadraticBezierTo(size.width * 0.5f, size.height, size.width, size.height * 0.82f)
        lineTo(size.width, 0f)
        close()
    }
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(220.dp)
        .clip(waveShape)
        .background(Brush.verticalGradient(listOf(MidnightBlue, MidnightBlue)))
        .padding(20.dp)) {
        IconButton(onClick = onSearchClick, modifier = Modifier.align(Alignment.TopEnd)) {
            Icon(Icons.Default.Search, null, tint = Color.White, modifier = Modifier.size(32.dp))
        }
        Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = sdfDay, fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = sdfDate, fontSize = 18.sp, color = Color.White.copy(alpha = 0.9f))
        }
    }
}

@Composable
fun SearchPage(onBack: () -> Unit) {
    var query by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showFilterMenu by remember { mutableStateOf(false) }

    // State for multiple filter selection
    var selectedFilters by remember { mutableStateOf(setOf<String>()) }

    LaunchedEffect(query) {
        if (query.isNotEmpty()) {
            isLoading = true
            delay(1200)
            isLoading = false
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(CleanGrayBg)
        .padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = query, onValueChange = { query = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Search") },
                leadingIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.Search, null) } },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MidnightBlue)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box {
                Button(
                    onClick = { showFilterMenu = true },
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MidnightBlue),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.List, null, tint = Color.White)
                }

                DropdownMenu(
                    expanded = showFilterMenu,
                    onDismissRequest = { showFilterMenu = false },
                    modifier = Modifier
                        .width(220.dp)
                        .background(Color.White)
                        .border(2.dp, MidnightBlue, RoundedCornerShape(12.dp))
                ) {
                    listOf("Classes", "Exams", "Tasks", "Holidays", "Extra", "ICal").forEach { label ->
                        val isTicked = selectedFilters.contains(label)
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier
                                        .size(24.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(CleanGrayBg.copy(alpha = 0.3f)))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(label, color = Color.Black, modifier = Modifier.weight(1f))

                                    // Tick only shows if this specific item was clicked
                                    if (isTicked) {
                                        Icon(Icons.Filled.CheckCircle, null, tint = MidnightBlue, modifier = Modifier.size(20.dp))
                                    }
                                }
                            },
                            onClick = {
                                // Toggles selection on/off
                                selectedFilters = if (isTicked) selectedFilters - label else selectedFilters + label
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                repeat(3) {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.LightGray.copy(alpha = 0.3f)))
                }
            }
        } else if (query.isNotEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                // Updates text to show which category was searched
                val filterInfo = if (selectedFilters.isNotEmpty()) " in ${selectedFilters.joinToString(", ")}" else ""
                Text(
                    text = "No results found for \"$query\"$filterInfo",
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun StatBox(label: String) {
    Surface(modifier = Modifier
        .width(140.dp)
        .height(55.dp), shape = RoundedCornerShape(14.dp), color = Color.White, border = BorderStroke(1.dp, BoxBorderBlue)) {
        Box(contentAlignment = Alignment.Center) {
            Text(label, color = MidnightBlue, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

private fun taskListDueLine(task: TaskItem): String {
    val today = StudyMateApp.startOfDayMillis(System.currentTimeMillis())
    val dueDay = StudyMateApp.startOfDayMillis(task.dueTimestamp)
    val diffDays = ((dueDay - today) / (24 * 60 * 60 * 1000L)).toInt()
    val rel = when {
        diffDays < 0 -> "Overdue"
        diffDays == 0 -> "Due Today"
        diffDays == 1 -> "Due Tomorrow"
        else -> "Due in $diffDays days"
    }
    return "$rel ${task.progressPercent}%"
}

@Composable
private fun TaskRowCard(
    task: TaskItem,
    tab: TaskListTab,
    onClick: () -> Unit,
) {
    val badgeColor = if (tab == TaskListTab.Overdue) OverdueOrange else MidnightBlue
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(118.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp),
    ) {
        Row(Modifier.fillMaxSize()) {
            Box(
                Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(MidnightBlue),
            )
            Box(Modifier.weight(1f)) {
                Box(
                    Modifier
                        .fillMaxWidth(0.42f)
                        .fillMaxHeight()
                        .align(Alignment.CenterEnd)
                        .background(Color(0xFFE8EAF6).copy(alpha = 0.5f)),
                )
                Row(
                    Modifier
                        .padding(14.dp)
                        .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Outlined.Description, null, tint = Color.Gray, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(task.title, fontWeight = FontWeight.Bold, color = TitleDark, fontSize = 17.sp)
                        Text(taskListDueLine(task), color = SubtextGray, fontSize = 13.sp)
                    }
                }
                Surface(
                    color = badgeColor,
                    shape = RoundedCornerShape(bottomStart = 12.dp),
                    modifier = Modifier.align(Alignment.TopEnd),
                ) {
                    Text(
                        StudyMateApp.formatCardDayMonth(task.dueTimestamp),
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
fun TasksListScreen(
    paddingValues: PaddingValues,
    viewModel: StudyMateApp,
    onBack: () -> Unit,
    onOpenTask: (String) -> Unit,
) {
    val uiState by viewModel.uiState
    var tab by remember { mutableStateOf(TaskListTab.Current) }
    var subjectFilter by remember { mutableStateOf("All Subjects") }
    var subjectMenuExpanded by remember { mutableStateOf(false) }
    val subjectChoices = remember { listOf("All Subjects") + subjectOptions }
    val today = remember { StudyMateApp.startOfDayMillis(System.currentTimeMillis()) }
    val weekEnd = today + 7L * 24 * 60 * 60 * 1000L

    val filtered = remember(uiState.tasks, tab, subjectFilter, today, weekEnd) {
        val bySubject = if (subjectFilter == "All Subjects") uiState.tasks
        else uiState.tasks.filter { it.subject == subjectFilter }
        when (tab) {
            TaskListTab.Current -> bySubject.filter { !it.isCompleted && it.dueTimestamp >= today }
            TaskListTab.Overdue -> bySubject.filter { !it.isCompleted && it.dueTimestamp < today }
            TaskListTab.Past -> bySubject.filter { it.isCompleted }
        }
    }

    val pastSections = remember(filtered, tab) {
        if (tab != TaskListTab.Past) emptyList()
        else {
            val sdf = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
            filtered
                .groupBy { sdf.format(Date(it.dueTimestamp)) }
                .toList()
                .sortedByDescending { it.second.maxOfOrNull { t -> t.dueTimestamp } ?: 0L }
        }
    }

    var openMonths by remember { mutableStateOf(setOf<String>()) }
    LaunchedEffect(pastSections.map { it.first }.toSet()) {
        openMonths = pastSections.map { it.first }.toSet()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CleanGrayBg)
            .padding(paddingValues),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
        ) {
            Surface(shape = CircleShape, color = Color.White, shadowElevation = 2.dp) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = MidnightBlue)
                }
            }
            Text(
                "Tasks",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.width(48.dp))
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            shape = RoundedCornerShape(14.dp),
            color = MidnightBlue.copy(alpha = 0.12f),
            border = BorderStroke(1.dp, MidnightBlue),
        ) {
            Row(Modifier.padding(4.dp)) {
                TaskListTab.values().forEach { t ->
                    val sel = tab == t
                    val label = t.name.replaceFirstChar { c -> c.titlecase(Locale.ENGLISH) }
                    if (sel) {
                        Button(
                            onClick = { tab = t },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = MidnightBlue),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(vertical = 8.dp),
                        ) { Text(label, color = Color.White, fontSize = 13.sp) }
                    } else {
                        TextButton(
                            onClick = { tab = t },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                        ) { Text(label, color = TitleDark, fontSize = 13.sp) }
                    }
                }
            }
        }

        Box(Modifier.fillMaxWidth().padding(16.dp)) {
            OutlinedTextField(
                value = subjectFilter,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { subjectMenuExpanded = true },
                trailingIcon = {
                    IconButton(onClick = { subjectMenuExpanded = true }) {
                        Icon(Icons.Default.KeyboardArrowDown, null)
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MidnightBlue,
                    focusedBorderColor = MidnightBlue,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                ),
            )
            DropdownMenu(expanded = subjectMenuExpanded, onDismissRequest = { subjectMenuExpanded = false }) {
                subjectChoices.forEach { opt ->
                    DropdownMenuItem(
                        text = { Text(opt) },
                        onClick = {
                            subjectFilter = opt
                            subjectMenuExpanded = false
                        },
                    )
                }
            }
        }

        if (tab == TaskListTab.Overdue) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Overdue", color = OverdueOrange, fontWeight = FontWeight.Bold)
                TextButton(onClick = { viewModel.completeAllOverdueTasks() }) {
                    Text("Mark all as complete", color = AccentBlueBright, fontWeight = FontWeight.Bold)
                }
            }
        }

        when (tab) {
            TaskListTab.Current -> Text(
                "This week",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                fontWeight = FontWeight.Bold,
            )
            TaskListTab.Past -> { }
            TaskListTab.Overdue -> { }
        }

        LazyColumn(Modifier.fillMaxSize()) {
            if (filtered.isEmpty()) {
                item {
                    Text(
                        "No tasks yet. Add one from + or Menu.",
                        modifier = Modifier.padding(24.dp),
                        color = SubtextGray,
                    )
                }
            } else if (tab == TaskListTab.Past) {
                pastSections.forEach { (month, tasksInMonth) ->
                    item(key = month) {
                        val expanded = openMonths.contains(month)
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    openMonths = if (expanded) openMonths - month else openMonths + month
                                },
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, BoxBorderBlue.copy(alpha = 0.4f)),
                        ) {
                            Row(
                                Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(month, fontWeight = FontWeight.Bold)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("${tasksInMonth.size}", color = SubtextGray)
                                    Spacer(Modifier.width(8.dp))
                                    Icon(
                                        if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        null,
                                        tint = SubtextGray,
                                    )
                                }
                            }
                        }
                    }
                    if (openMonths.contains(month)) {
                        items(tasksInMonth, key = { it.id }) { task ->
                            TaskRowCard(task, tab) { onOpenTask(task.id) }
                        }
                    }
                }
            } else {
                items(filtered, key = { it.id }) { task ->
                    TaskRowCard(task, tab) { onOpenTask(task.id) }
                }
            }
        }
    }
}

@Composable
fun TaskDetailScreen(
    paddingValues: PaddingValues,
    viewModel: StudyMateApp,
    taskId: String,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onOpenPomodoro: () -> Unit = {},
) {
    val uiState by viewModel.uiState
    val task = uiState.tasks.find { it.id == taskId }
    val scrollState = rememberScrollState()
    if (task == null) {
        LaunchedEffect(Unit) { onBack() }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(CleanGrayBg)
            .verticalScroll(scrollState),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(Brush.verticalGradient(listOf(Color(0xFF37474F), Color(0xFF78909C)))),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.35f), CircleShape),
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                }
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.35f), CircleShape),
                ) {
                    Icon(Icons.Default.Edit, null, tint = Color.White)
                }
            }

            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 56.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(10.dp),
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text(
                        task.title.uppercase(Locale.ENGLISH),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = TitleDark,
                    )
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                        Text(
                            task.subject.uppercase(Locale.ENGLISH),
                            color = AccentBlueBright,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                        )
                        Spacer(Modifier.width(8.dp))
                        Surface(color = Color(0xFFE3F2FD), shape = RoundedCornerShape(8.dp)) {
                            Text(
                                "Task",
                                color = AccentBlueBright,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                    Text(
                        task.typeOptional.ifBlank { "Task" },
                        color = SubtextGray,
                        fontSize = 13.sp,
                    )
                    Row(Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        DetailChip(Icons.Default.CalendarToday, StudyMateApp.formatDetailDate(task.dueTimestamp))
                        DetailChip(Icons.Default.Schedule, task.dueTimeDisplay.ifBlank { StudyMateApp.formatDisplayTime(task.dueTimestamp) })
                    }
                }
            }
        }

        Spacer(Modifier.height(72.dp))

        Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Column {
                Text("Progress rate", fontWeight = FontWeight.Bold, color = MidnightBlue)
                val p = (task.progressPercent.coerceIn(0, 100)) / 100f
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .height(45.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE3F2FD)),
                ) {
                    Box(
                        Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(p)
                            .background(AccentBlueBright),
                    )
                    Text(
                        "${task.progressPercent}%",
                        modifier = Modifier.align(Alignment.CenterStart).padding(start = 12.dp),
                        color = AccentBlueBright,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Button(
                onClick = { viewModel.completeTask(task.id); onBack() },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlueBright),
                shape = RoundedCornerShape(14.dp),
            ) {
                Text("Mark as Completed", fontWeight = FontWeight.Bold, color = Color.White)
            }

            Column {
                Text("Description", fontWeight = FontWeight.Bold, color = MidnightBlue)
                Text(task.details.ifBlank { "—" }, color = SubtextGray, fontSize = 15.sp)
            }

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, BoxBorderBlue),
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.WorkspacePremium, null, tint = AcademicGold, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Subtasks - this occurrence",
                        color = AccentBlueBright,
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.Medium,
                    )
                    Icon(Icons.Default.KeyboardArrowDown, null, tint = AccentBlueBright)
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(modifier = Modifier.size(45.dp), color = Color(0xFFE3F2FD), shape = CircleShape) {
                            Icon(Icons.Default.Timer, null, tint = AccentBlueBright, modifier = Modifier.padding(10.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Pomodoro Timer", fontWeight = FontWeight.Bold, color = MidnightBlue)
                            Text("Boost your productivity by alternating study sessions with regular breaks.", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                    Button(
                        onClick = onOpenPomodoro,
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp).height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlueBright),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(18.dp), tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text("Start study session", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun ExamRowCard(exam: ExamItem, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp),
    ) {
        Row(Modifier.fillMaxWidth()) {
            Box(
                Modifier
                    .width(4.dp)
                    .height(130.dp)
                    .background(MidnightBlue),
            )
            Box(Modifier.weight(1f).height(130.dp)) {
                Box(
                    Modifier
                        .fillMaxWidth(0.38f)
                        .fillMaxHeight()
                        .align(Alignment.CenterEnd)
                        .background(Color(0xFFE8EAF6).copy(alpha = 0.55f)),
                )
                Column(Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("✍️", fontSize = 18.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(exam.subject, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TitleDark)
                    }
                    Text(exam.name, color = SubtextGray, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))
                    val today = StudyMateApp.startOfDayMillis(System.currentTimeMillis())
                    val dayLabel = if (StudyMateApp.startOfDayMillis(exam.examTimestamp) == today) {
                        "${exam.timeDisplay.ifBlank { StudyMateApp.formatDisplayTime(exam.examTimestamp) }} Today"
                    } else {
                        "${exam.timeDisplay.ifBlank { StudyMateApp.formatDisplayTime(exam.examTimestamp) }}, ${
                            SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH).format(Date(exam.examTimestamp))
                        }"
                    }
                    Text(dayLabel, color = TitleDark, fontSize = 13.sp, modifier = Modifier.padding(top = 6.dp))
                    Text(
                        "${exam.durationMinutes.ifBlank { "—" }} min - ${exam.examType}",
                        color = SubtextGray,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun ExamsListScreen(
    paddingValues: PaddingValues,
    viewModel: StudyMateApp,
    onBack: () -> Unit,
    onOpenExam: (String) -> Unit,
) {
    val uiState by viewModel.uiState
    var tab by remember { mutableStateOf(ExamListTab.Current) }
    var subjectFilter by remember { mutableStateOf("All Subjects") }
    var subjectMenuExpanded by remember { mutableStateOf(false) }
    val subjectChoices = remember { listOf("All Subjects") + subjectOptions }
    val today = remember { StudyMateApp.startOfDayMillis(System.currentTimeMillis()) }

    val filtered = remember(uiState.exams, tab, subjectFilter, today) {
        val bySubject = if (subjectFilter == "All Subjects") uiState.exams
        else uiState.exams.filter { it.subject == subjectFilter }
        when (tab) {
            ExamListTab.Current -> bySubject.filter { it.examTimestamp >= today }
            ExamListTab.Past -> bySubject.filter { it.examTimestamp < today }
        }
    }

    val todayExams = remember(filtered, tab, today) {
        if (tab != ExamListTab.Current) emptyList()
        else filtered.filter { StudyMateApp.startOfDayMillis(it.examTimestamp) == today }
    }
    val otherCurrent = remember(filtered, tab, today) {
        if (tab != ExamListTab.Current) filtered
        else filtered.filter { StudyMateApp.startOfDayMillis(it.examTimestamp) != today }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CleanGrayBg)
            .padding(paddingValues),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
        ) {
            Surface(shape = CircleShape, color = Color.White, shadowElevation = 2.dp) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = MidnightBlue)
                }
            }
            Text(
                "Exams",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.width(48.dp))
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            shape = RoundedCornerShape(14.dp),
            color = MidnightBlue.copy(alpha = 0.12f),
            border = BorderStroke(1.dp, MidnightBlue),
        ) {
            Row(Modifier.padding(4.dp)) {
                ExamListTab.values().forEach { t ->
                    val sel = tab == t
                    val label = t.name.replaceFirstChar { c -> c.titlecase(Locale.ENGLISH) }
                    if (sel) {
                        Button(
                            onClick = { tab = t },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = MidnightBlue),
                            shape = RoundedCornerShape(10.dp),
                        ) { Text(label, color = Color.White) }
                    } else {
                        TextButton(onClick = { tab = t }, modifier = Modifier.weight(1f)) {
                            Text(label, color = TitleDark)
                        }
                    }
                }
            }
        }

        Box(Modifier.fillMaxWidth().padding(16.dp)) {
            OutlinedTextField(
                value = subjectFilter,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { subjectMenuExpanded = true },
                trailingIcon = {
                    IconButton(onClick = { subjectMenuExpanded = true }) {
                        Icon(Icons.Default.KeyboardArrowDown, null)
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MidnightBlue,
                    focusedBorderColor = MidnightBlue,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                ),
            )
            DropdownMenu(expanded = subjectMenuExpanded, onDismissRequest = { subjectMenuExpanded = false }) {
                subjectChoices.forEach { opt ->
                    DropdownMenuItem(
                        text = { Text(opt) },
                        onClick = {
                            subjectFilter = opt
                            subjectMenuExpanded = false
                        },
                    )
                }
            }
        }

        LazyColumn(Modifier.fillMaxSize()) {
            if (tab == ExamListTab.Current && todayExams.isNotEmpty()) {
                item {
                    Text("Today", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                }
                items(todayExams, key = { it.id }) { ex -> ExamRowCard(ex) { onOpenExam(ex.id) } }
            }
            if (tab == ExamListTab.Current && otherCurrent.isNotEmpty()) {
                item {
                    Text("Upcoming", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                }
                items(otherCurrent, key = { it.id }) { ex -> ExamRowCard(ex) { onOpenExam(ex.id) } }
            }
            if (tab == ExamListTab.Past) {
                if (filtered.isEmpty()) {
                    item {
                        Text("No past exams.", modifier = Modifier.padding(24.dp), color = SubtextGray)
                    }
                } else {
                    items(filtered, key = { it.id }) { ex -> ExamRowCard(ex) { onOpenExam(ex.id) } }
                }
            }
            if (tab == ExamListTab.Current && todayExams.isEmpty() && otherCurrent.isEmpty()) {
                item {
                    Text(
                        "No upcoming exams. Add one with +.",
                        modifier = Modifier.padding(24.dp),
                        color = SubtextGray,
                    )
                }
            }
        }
    }
}

@Composable
fun ExamDetailScreen(
    paddingValues: PaddingValues,
    viewModel: StudyMateApp,
    examId: String,
    onBack: () -> Unit,
    onEdit: () -> Unit,
) {
    val uiState by viewModel.uiState
    val exam = uiState.exams.find { it.id == examId }
    val scrollState = rememberScrollState()
    if (exam == null) {
        LaunchedEffect(Unit) { onBack() }
        return
    }
    val relatedTask = uiState.tasks
        .filter { it.subject.equals(exam.subject, ignoreCase = true) }
        .minByOrNull { it.dueTimestamp }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(CleanGrayBg)
            .verticalScroll(scrollState),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Brush.verticalGradient(listOf(Color(0xFF5D4037), Color(0xFF8D6E63)))),
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.35f), CircleShape),
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                }
                Row {
                    IconButton(
                        onClick = { },
                        modifier = Modifier.background(Color.Black.copy(alpha = 0.35f), CircleShape),
                    ) {
                        Icon(Icons.Outlined.FavoriteBorder, null, tint = Color.White)
                    }
                    Spacer(Modifier.width(8.dp))
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.background(Color.Black.copy(alpha = 0.35f), CircleShape),
                    ) {
                        Icon(Icons.Default.Edit, null, tint = Color.White)
                    }
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-48).dp)
                .padding(horizontal = 18.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp),
        ) {
            Column(Modifier.padding(18.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            exam.subject.uppercase(Locale.ENGLISH),
                            color = MidnightBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                        )
                        Text(exam.name, color = SubtextGray, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                    Surface(color = Color(0xFFE3F2FD), shape = RoundedCornerShape(20.dp)) {
                        Text(
                            exam.examType.uppercase(Locale.ENGLISH),
                            color = MidnightBlue,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                Text(
                    "${SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH).format(Date(exam.examTimestamp))} | ${exam.timeDisplay.ifBlank { StudyMateApp.formatDisplayTime(exam.examTimestamp) }}",
                    fontWeight = FontWeight.Bold,
                    color = TitleDark,
                    modifier = Modifier.padding(top = 12.dp),
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ExamInfoCell(
                        Icons.Default.Schedule,
                        "Duration",
                        "${exam.durationMinutes.ifBlank { "—" }} minutes",
                        Modifier.weight(1f),
                    )
                    ExamInfoCell(Icons.Filled.EventSeat, "Seat", exam.seat.ifBlank { "—" }, Modifier.weight(1f))
                    ExamInfoCell(Icons.Filled.Place, "Location", exam.room.ifBlank { "—" }, Modifier.weight(1f))
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MidnightBlue),
            shape = RoundedCornerShape(14.dp),
        ) {
            Icon(Icons.Default.Add, null, tint = Color.White)
            Spacer(Modifier.width(8.dp))
            Text("Add Score", color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(6.dp))
            Icon(Icons.Default.WorkspacePremium, null, tint = AcademicGold, modifier = Modifier.size(18.dp))
        }

        Text(
            "Revision Tasks",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 24.dp, bottom = 8.dp),
        )
        if (relatedTask != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
            ) {
                Row(Modifier.fillMaxWidth()) {
                    Box(
                        Modifier
                            .width(4.dp)
                            .height(90.dp)
                            .background(MidnightBlue),
                    )
                    Column(Modifier.padding(14.dp)) {
                        Text(relatedTask.title, fontWeight = FontWeight.Bold)
                        Text(
                            "Due ${StudyMateApp.formatDetailDate(relatedTask.dueTimestamp)}",
                            color = SubtextGray,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 6.dp),
                        )
                        Text(
                            "${relatedTask.subject} ${relatedTask.typeOptional.ifBlank { "task" }.lowercase(Locale.ENGLISH)}",
                            color = AccentBlueBright,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                }
            }
        } else {
            Text("No linked tasks for this subject.", color = SubtextGray, modifier = Modifier.padding(horizontal = 18.dp))
        }
        Spacer(Modifier.height(80.dp))
    }
}

@Composable
private fun ExamInfoCell(icon: ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F5F5))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(icon, null, tint = AccentBlueBright, modifier = Modifier.size(22.dp))
        Text(label, color = SubtextGray, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
        Text(value, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TitleDark)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UpdateTaskScreen(
    paddingValues: PaddingValues,
    viewModel: StudyMateApp,
    taskId: String,
    onBack: () -> Unit,
    onDeleted: () -> Unit,
) {
    val task = viewModel.getTask(taskId)
    if (task == null) {
        LaunchedEffect(Unit) { onBack() }
        return
    }
    var title by remember(taskId) { mutableStateOf(task.title) }
    var details by remember(taskId) { mutableStateOf(task.details) }
    var typeOptional by remember(taskId) { mutableStateOf(task.typeOptional) }
    var occursOnce by remember(taskId) { mutableStateOf(task.occursOnce) }
    var dueDate by remember(taskId) { mutableStateOf(task.dueDateDisplay) }
    var dueTime by remember(taskId) { mutableStateOf(task.dueTimeDisplay) }

    val premiumTypes = setOf("Essay", "Group Project", "Reading", "Meeting")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CleanGrayBg)
            .padding(paddingValues)
            .verticalScroll(rememberScrollState()),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(shape = CircleShape, color = Color.White, shadowElevation = 2.dp) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = MidnightBlue)
                }
            }
            Text(
                "Update Task",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.width(48.dp))
        }

        Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Column {
                Row {
                    Text("Title", fontWeight = FontWeight.Bold)
                    Text("*", color = Color.Red, modifier = Modifier.padding(start = 2.dp))
                }
                Spacer(Modifier.height(5.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = MidnightBlue,
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                    ),
                )
            }
            CustomInputField(
                label = "Details",
                value = details,
                onValueChange = { details = it },
                hint = "Details",
                singleLine = false,
                minLines = 3,
                maxLines = 8,
            )
            Text("Type (optional)", fontWeight = FontWeight.Bold)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                taskTypeOptionalOptions.forEach { opt ->
                    FormSelectableChip(
                        text = opt,
                        selected = typeOptional == opt,
                        onClick = { typeOptional = opt },
                        showPremiumCrown = opt in premiumTypes,
                    )
                }
            }
            Text("Occurs", fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FormSelectableChip(text = "Once", selected = occursOnce, onClick = { occursOnce = true })
                FormSelectableChip(
                    text = "Repeating",
                    selected = !occursOnce,
                    onClick = { occursOnce = false },
                    showPremiumCrown = true,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(Modifier.weight(1f)) {
                    StandardDatePickerField(
                        label = "Due Date",
                        value = dueDate,
                        onValueChange = { dueDate = it },
                        hint = "Fri, 4 Mar 2022",
                        required = true,
                    )
                }
                Box(Modifier.weight(1f)) {
                    StandardTimePickerField(
                        label = "Time",
                        value = dueTime,
                        onValueChange = { dueTime = it },
                        hint = "10:30 AM",
                    )
                }
            }
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f).height(52.dp),
                border = BorderStroke(1.dp, MidnightBlue),
                shape = RoundedCornerShape(12.dp),
            ) { Text("Cancel", color = MidnightBlue) }
            Button(
                onClick = {
                    viewModel.updateTask(taskId, title, details, typeOptional, occursOnce, dueDate, dueTime)
                    onBack()
                },
                modifier = Modifier.weight(1f).height(52.dp),
                colors = ButtonDefaults.buttonColors(MidnightBlue),
                shape = RoundedCornerShape(12.dp),
            ) { Text("Update Task", color = Color.White, fontWeight = FontWeight.Bold) }
        }
        TextButton(
            onClick = {
                viewModel.deleteTask(taskId)
                onDeleted()
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
        ) {
            Text("Delete", color = Color.Red, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun UpdateExamScreen(
    paddingValues: PaddingValues,
    viewModel: StudyMateApp,
    examId: String,
    onBack: () -> Unit,
    onDeleted: () -> Unit,
) {
    val exam = viewModel.getExam(examId)
    if (exam == null) {
        LaunchedEffect(Unit) { onBack() }
        return
    }
    var isResit by remember(examId) { mutableStateOf(exam.isResit) }
    var examType by remember(examId) { mutableStateOf(exam.examType) }
    var inPerson by remember(examId) { mutableStateOf(exam.inPerson) }
    var module by remember(examId) { mutableStateOf(exam.module.ifBlank { exam.subject }) }
    var seat by remember(examId) { mutableStateOf(exam.seat) }
    var room by remember(examId) { mutableStateOf(exam.room) }
    var onlineUrl by remember(examId) { mutableStateOf(exam.onlineUrl) }
    var dateStr by remember(examId) { mutableStateOf(exam.dateDisplay) }
    var timeStr by remember(examId) { mutableStateOf(exam.timeDisplay) }
    var duration by remember(examId) { mutableStateOf(exam.durationMinutes) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CleanGrayBg)
            .padding(paddingValues)
            .verticalScroll(rememberScrollState()),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(shape = CircleShape, color = Color.White, shadowElevation = 2.dp) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = MidnightBlue)
                }
            }
            Text(
                "Update Exam",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.width(48.dp))
        }

        Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Resit", fontWeight = FontWeight.Bold)
                Switch(checked = isResit, onCheckedChange = { isResit = it })
            }
            Text("Type", fontWeight = FontWeight.Bold)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Exam", "Quiz", "Test").forEach { t ->
                    FormSelectableChip(
                        text = t,
                        selected = examType == t,
                        onClick = { examType = t },
                        showPremiumCrown = t != "Exam",
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            Text("Mode", fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FormSelectableChip(text = "In-person", selected = inPerson, onClick = { inPerson = true }, modifier = Modifier.weight(1f))
                FormSelectableChip(text = "Online", selected = !inPerson, onClick = { inPerson = false }, modifier = Modifier.weight(1f))
            }
            Column {
                Row {
                    Text("Module", fontWeight = FontWeight.Bold)
                    Text("*", color = Color.Red, modifier = Modifier.padding(start = 2.dp))
                }
                Spacer(Modifier.height(5.dp))
                OutlinedTextField(
                    value = module,
                    onValueChange = { module = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = MidnightBlue,
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                    ),
                )
            }
            if (inPerson) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(Modifier.weight(1f)) { CustomInputField(label = "Seat", value = seat, onValueChange = { seat = it }, hint = "Seat") }
                    Box(Modifier.weight(1f)) { CustomInputField(label = "Room", value = room, onValueChange = { room = it }, hint = "Room") }
                }
            } else {
                CustomInputField(label = "URL", value = onlineUrl, onValueChange = { onlineUrl = it }, hint = "Paste meeting link")
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(Modifier.weight(1f)) {
                    StandardDatePickerField(
                        label = "Date",
                        value = dateStr,
                        onValueChange = { dateStr = it },
                        hint = "Fri, 4 Mar 2022",
                        required = true,
                    )
                }
                Box(Modifier.weight(1f)) {
                    StandardTimePickerField(
                        label = "Time",
                        value = timeStr,
                        onValueChange = { timeStr = it },
                        hint = "10:30 AM",
                        required = true,
                    )
                }
            }
            Column {
                Row {
                    Text("Duration (In minutes)", fontWeight = FontWeight.Bold)
                    Text("*", color = Color.Red, modifier = Modifier.padding(start = 2.dp))
                }
                Spacer(Modifier.height(5.dp))
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it.filter { ch -> ch.isDigit() } },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = MidnightBlue,
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                    ),
                )
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f).height(52.dp),
                border = BorderStroke(1.dp, MidnightBlue),
                shape = RoundedCornerShape(12.dp),
            ) { Text("Cancel", color = MidnightBlue) }
            Button(
                onClick = {
                    viewModel.updateExam(
                        examId,
                        isResit,
                        examType,
                        inPerson,
                        module,
                        seat,
                        room,
                        onlineUrl,
                        dateStr,
                        timeStr,
                        duration,
                    )
                    onBack()
                },
                modifier = Modifier.weight(1f).height(52.dp),
                colors = ButtonDefaults.buttonColors(MidnightBlue),
                shape = RoundedCornerShape(12.dp),
            ) { Text("Update Exam", color = Color.White, fontWeight = FontWeight.Bold) }
        }
        TextButton(
            onClick = {
                viewModel.deleteExam(examId)
                onDeleted()
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
        ) {
            Text("Delete", color = Color.Red, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun PomodoroTimerScreen(
    paddingValues: PaddingValues,
    settings: PomodoroSettings,
    mode: PomodoroMode,
    remainingSeconds: Int,
    completedFocusSessions: Int,
    onBack: () -> Unit,
    onOpenSettings: () -> Unit,
    onModeChange: (PomodoroMode) -> Unit,
    onRemainingSecondsChange: (Int) -> Unit,
    onCompletedFocusSessionsChange: (Int) -> Unit,
) {
    var isRunning by remember { mutableStateOf(false) }
    val totalSecondsForMode = pomodoroModeTotalSeconds(mode, settings).coerceAtLeast(1)
    val progress = (remainingSeconds.toFloat() / totalSecondsForMode.toFloat()).coerceIn(0f, 1f)
    val clockwiseProgress = (1f - progress).coerceIn(0f, 1f)
    val knobRadiusPx = 123f
    val angleRad = Math.toRadians((360f * clockwiseProgress - 90f).toDouble())
    val knobX = (cos(angleRad) * knobRadiusPx).toInt()
    val knobY = (sin(angleRad) * knobRadiusPx).toInt()

    LaunchedEffect(isRunning, remainingSeconds, mode, settings, completedFocusSessions) {
        if (!isRunning) return@LaunchedEffect
        if (remainingSeconds > 0) {
            delay(1000)
            onRemainingSecondsChange((remainingSeconds - 1).coerceAtLeast(0))
        } else {
            isRunning = false
            when (mode) {
                PomodoroMode.Focus -> {
                    val nextCompleted = completedFocusSessions + 1
                    onCompletedFocusSessionsChange(nextCompleted)
                    val shouldTakeLongBreak = nextCompleted % settings.longBreakInterval == 0
                    val nextMode = if (shouldTakeLongBreak) PomodoroMode.LongBreak else PomodoroMode.ShortBreak
                    onModeChange(nextMode)
                    onRemainingSecondsChange(
                        if (shouldTakeLongBreak) settings.longBreakMinutes * 60 else settings.shortBreakMinutes * 60,
                    )
                }
                PomodoroMode.ShortBreak, PomodoroMode.LongBreak -> {
                    onModeChange(PomodoroMode.Focus)
                    onRemainingSecondsChange(settings.focusMinutes * 60)
                }
            }
        }
    }

    fun resetCurrentMode() {
        val seconds = when (mode) {
            PomodoroMode.Focus -> settings.focusMinutes * 60
            PomodoroMode.ShortBreak -> settings.shortBreakMinutes * 60
            PomodoroMode.LongBreak -> settings.longBreakMinutes * 60
        }
        onRemainingSecondsChange(seconds)
        isRunning = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(CleanGrayBg)
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = Color.White, shadowElevation = 2.dp) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = MidnightBlue)
                }
            }
            Text(
                text = "Timer",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.width(48.dp))
        }

        Spacer(Modifier.height(28.dp))
        Text("Make it happen", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = TitleDark)
        Text(
            "Focused work makes you up to five times more productive.",
            color = SubtextGray,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 8.dp),
        )

        Spacer(Modifier.height(28.dp))
        Box(
            modifier = Modifier
                .size(260.dp)
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(
                progress = { 1f },
                modifier = Modifier.fillMaxSize(),
                color = MidnightBlue.copy(alpha = 0.28f),
                strokeWidth = 14.dp,
                trackColor = Color.Transparent,
            )
            CircularProgressIndicator(
                progress = { clockwiseProgress },
                modifier = Modifier.fillMaxSize(),
                color = MidnightBlue,
                strokeWidth = 14.dp,
                trackColor = Color.Transparent,
                strokeCap = StrokeCap.Round,
            )
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .offset { IntOffset(knobX, knobY) }
                    .background(Color.White, CircleShape)
                    .border(1.dp, MidnightBlue.copy(alpha = 0.45f), CircleShape),
            )
            Surface(
                modifier = Modifier.size(222.dp),
                shape = CircleShape,
                color = Color.White,
                border = BorderStroke(2.dp, Color(0xFFE9EEF4)),
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    val modeLabel = when (mode) {
                        PomodoroMode.Focus -> "Focus"
                        PomodoroMode.ShortBreak -> "Short Break"
                        PomodoroMode.LongBreak -> "Long Break"
                    }
                    Text(modeLabel, color = SubtextGray, fontWeight = FontWeight.Bold, fontSize = 30.sp)
                    Text(
                        formatPomodoroTime(remainingSeconds),
                        fontSize = 58.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MidnightBlue,
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FilledIconButton(
                onClick = { resetCurrentMode() },
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFFDCEBFA)),
            ) { Icon(Icons.Default.RestartAlt, null, tint = MidnightBlue) }
            Spacer(Modifier.width(18.dp))
            FilledIconButton(
                onClick = { isRunning = !isRunning },
                modifier = Modifier.size(72.dp),
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = MidnightBlue),
            ) { Icon(if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow, null, tint = Color.White) }
            Spacer(Modifier.width(18.dp))
            FilledIconButton(
                onClick = onOpenSettings,
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFFDCEBFA)),
            ) { Icon(Icons.Default.Settings, null, tint = MidnightBlue) }
        }

        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = "Checklist",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, null, tint = MidnightBlue) },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MidnightBlue.copy(alpha = 0.7f),
                focusedBorderColor = MidnightBlue,
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
            ),
        )
    }
}

@Composable
private fun PomodoroSettingsScreen(
    paddingValues: PaddingValues,
    settings: PomodoroSettings,
    onBack: () -> Unit,
    onSave: (PomodoroSettings) -> Unit,
) {
    var focusMinutes by remember(settings) { mutableIntStateOf(settings.focusMinutes) }
    var shortBreakMinutes by remember(settings) { mutableIntStateOf(settings.shortBreakMinutes) }
    var longBreakMinutes by remember(settings) { mutableIntStateOf(settings.longBreakMinutes) }
    var longBreakInterval by remember(settings) { mutableIntStateOf(settings.longBreakInterval) }
    var alertSound by remember(settings) { mutableStateOf(settings.alertSound) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(CleanGrayBg)
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = Color.White, shadowElevation = 2.dp) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = MidnightBlue)
                }
            }
            Text(
                text = "Pomodoro Settings",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.width(48.dp))
        }

        Spacer(Modifier.height(20.dp))
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                PomodoroDropdownField(
                    label = "Focus Time",
                    selectedValue = focusMinutes,
                    options = pomodoroMinuteOptions,
                    optionLabel = { "$it Minutes" },
                    onSelect = { focusMinutes = it },
                )
                PomodoroDropdownField(
                    label = "Short Break",
                    selectedValue = shortBreakMinutes,
                    options = pomodoroMinuteOptions,
                    optionLabel = { "$it Minutes" },
                    onSelect = { shortBreakMinutes = it },
                )
                PomodoroDropdownField(
                    label = "Long Break",
                    selectedValue = longBreakMinutes,
                    options = pomodoroMinuteOptions,
                    optionLabel = { "$it Minutes" },
                    onSelect = { longBreakMinutes = it },
                )
                PomodoroDropdownField(
                    label = "Long Break Interval",
                    selectedValue = longBreakInterval,
                    options = pomodoroIntervalOptions,
                    optionLabel = { "$it intervals" },
                    onSelect = { longBreakInterval = it },
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Alert Sound", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Switch(checked = alertSound, onCheckedChange = { alertSound = it })
            }
        }

        Spacer(Modifier.weight(1f))
        Button(
            onClick = {
                onSave(
                    PomodoroSettings(
                        focusMinutes = focusMinutes,
                        shortBreakMinutes = shortBreakMinutes,
                        longBreakMinutes = longBreakMinutes,
                        longBreakInterval = longBreakInterval,
                        alertSound = alertSound,
                    ),
                )
                onBack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MidnightBlue),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text("Save", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun PomodoroDropdownField(
    label: String,
    selectedValue: Int,
    options: List<Int>,
    optionLabel: (Int) -> String,
    onSelect: (Int) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(label, fontWeight = FontWeight.Bold, color = TitleDark, fontSize = 17.sp)
        Spacer(Modifier.height(6.dp))
        Box {
            OutlinedTextField(
                value = optionLabel(selectedValue),
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true },
                trailingIcon = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.KeyboardArrowDown, null)
                    }
                },
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = MidnightBlue,
                    unfocusedContainerColor = Color(0xFFF2F6F9),
                    focusedContainerColor = Color(0xFFF2F6F9),
                ),
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.92f),
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(optionLabel(option)) },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

private fun formatPomodoroTime(totalSeconds: Int): String {
    val minutes = (totalSeconds / 60).coerceAtLeast(0)
    val seconds = (totalSeconds % 60).coerceAtLeast(0)
    return String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds)
}

private fun pomodoroModeTotalSeconds(mode: PomodoroMode, settings: PomodoroSettings): Int {
    return when (mode) {
        PomodoroMode.Focus -> settings.focusMinutes * 60
        PomodoroMode.ShortBreak -> settings.shortBreakMinutes * 60
        PomodoroMode.LongBreak -> settings.longBreakMinutes * 60
    }
}

// --- HELPERS ---
@Composable
fun DetailChip(icon: ImageVector, text: String) {
    Surface(color = Color(0xFFF5F5F5), shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
            Spacer(modifier = Modifier.width(6.dp))
            Text(text, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}