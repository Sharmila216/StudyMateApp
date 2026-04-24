package com.example.a216155_cikguizwan_lab03

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material.icons.filled.Edit
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.a216155_cikguizwan_lab03.ui.them.MidnightBlue
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.filled.MenuBook
import com.example.a216155_cikguizwan_lab03.ui.them.CleanGrayBg
import java.util.Calendar
import java.util.Locale
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

// --- THEME COLORS ---
// --- NEW DEEP MIDNIGHT THEME COLORS ---
val MidnightBlue = Color(0xFF1A237E)      // Primary (Replacing PrimaryPurple)
val AcademicGold = Color(0xFFC5A000)      // Secondary (Replacing SoftPink)
val CleanGrayBg = Color(0xFFFFF0F5)       // Background (Replacing BodyPinkBg)
val BoxBorderBlue = Color(0xFFADCFFF)     // Border (You can keep this or use MidnightBlue)
val TitleDark = Color(0xFF212121)    // Pure White for Cards
val SubtextGray = Color(0xFF616161)       // For small text

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { StudyMateApp() }
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StudyMatePreview() {
    StudyMateApp()
}

@Composable
fun StudyMateApp() {
    var currentPage by remember { mutableStateOf("dashboard") }

    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Navigation Buttons
                    IconButton(onClick = { currentPage = "dashboard" }) {
                        Icon(Icons.Filled.Home, null, tint = if (currentPage == "dashboard") MidnightBlue else Color.LightGray)
                    }
                    IconButton(onClick = { currentPage = "calendar" }) {
                        Icon(Icons.Outlined.DateRange, null, tint = if (currentPage == "calendar") MidnightBlue else Color.LightGray)
                    }

                    Spacer(modifier = Modifier.width(60.dp)) // Space for FAB

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
                modifier = Modifier
                    .size(65.dp)
                    .offset(y = 55.dp)
            ) {
                Icon(Icons.Default.Edit, null, modifier = Modifier.size(30.dp))
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (currentPage) {
                // All screens now receive the required paddingValues to fix the build errors
                "dashboard" -> DashboardScreen(paddingValues, onSearchClick = { currentPage = "search" })
                "calendar" -> MainCalendarApp()
                "menu" -> MenuScreen(onTasksClick = { currentPage = "tasks_list" })
                "tasks_list" -> TasksListScreen(onTaskClick = { currentPage = "task_detail" }, onBack = { currentPage = "menu" })
                "task_detail" -> TaskDetailScreen(onBack = { currentPage = "tasks_list" })
                "profile" -> ProfileScreen(paddingValues, onEditClick = { /* logic */ })
                "add_new" -> AddNewScreen(paddingValues, onBack = { currentPage = "dashboard" })
                "search" -> SearchPage(onBack = { currentPage = "dashboard" })
                else -> DashboardScreen(paddingValues, onSearchClick = { })
            }
        }
    }
}
@Composable
fun MenuScreen(paddingValues: PaddingValues) {
    // FIXED: Using Pair explicitly to prevent 'unresolved reference' errors
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
                Card(
                    modifier = Modifier.aspectRatio(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(item.second, fontSize = 30.sp)
                        Text(item.first, fontSize = 11.sp, textAlign = TextAlign.Center, color = Color.Gray)
                    }
                }
            }
        }
    }
}
@Composable
fun AddNewScreen(paddingValues: PaddingValues, onBack: () -> Unit) {
    var selectedTab by remember { mutableStateOf("Tasks") }
    val categories = listOf("Tasks", "Classes", "Exams", "Vacations", "Xtra")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CleanGrayBg)
            .padding(paddingValues) // Prevents UI from being hidden under bars
            .verticalScroll(rememberScrollState())
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = MidnightBlue)
            }
            Text("Add New", modifier = Modifier.align(Alignment.Center), fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            categories.forEach { name ->
                val isSelected = selectedTab == name
                Button(
                    onClick = { selectedTab = name },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isSelected) MidnightBlue else Color.White),
                    border = if (!isSelected) BorderStroke(1.dp, BoxBorderBlue) else null,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(name, color = if (isSelected) Color.White else MidnightBlue)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(modifier = Modifier.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            CustomInputField(label = "Title", hint = "Task Title")
            CustomInputField(label = "Details", hint = "Task description")
            CustomInputField(label = "Subject", hint = "Select subject", isDropdown = true)

            Text("Occurs", fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = {}, colors = ButtonDefaults.buttonColors(MidnightBlue)) { Text("Once") }
                OutlinedButton(onClick = {}, border = BorderStroke(1.dp, MidnightBlue)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Repeating", color = MidnightBlue)
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.Default.WorkspacePremium, null, tint = MidnightBlue, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(Modifier.weight(1f)) { CustomInputField(label = "Due Date", hint = "Fri, 4 Mar 2022") }
                Box(Modifier.weight(1f)) { CustomInputField(label = "Time", hint = "10:30 AM") }
            }
        }

        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { /* Save action */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                colors = ButtonDefaults.buttonColors(MidnightBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save (4 free tasks remaining)", fontWeight = FontWeight.Bold)
            }
            OutlinedButton(onClick = onBack, modifier = Modifier
                .fillMaxWidth()
                .height(55.dp), border = BorderStroke(1.dp, MidnightBlue), shape = RoundedCornerShape(12.dp)) {
                Text("Cancel", color = MidnightBlue)
            }
        }
    }
}

@Composable
fun CustomInputField(label: String, hint: String, isDropdown: Boolean = false) {
    Column {
        Text(label, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 5.dp))
        OutlinedTextField(
            value = "", onValueChange = {},
            placeholder = { Text(hint, color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = MidnightBlue,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            ),
            trailingIcon = if (isDropdown) { { Icon(Icons.Default.KeyboardArrowDown, null, tint = Color.Gray) } } else null
        )
    }
}
@Composable
fun ProfileScreen(paddingValues: PaddingValues, onEditClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CleanGrayBg)
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
            Text("Profile", modifier = Modifier.align(Alignment.Center), fontSize = 20.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = onEditClick, modifier = Modifier.align(Alignment.CenterEnd)) {
                Icon(Icons.Default.Edit, null, tint = Color.Gray)
            }
        }
        Box(modifier = Modifier
            .size(120.dp)
            .border(1.dp, MidnightBlue, CircleShape), contentAlignment = Alignment.Center) {
            Text("Profile Picture", fontSize = 12.sp, color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Sharmila", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("sharmila@gmail.com", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(24.dp))
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
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)) {
            ProfileOptionItem(Icons.Default.WorkspacePremium, "Premium Subscription", MidnightBlue)
            ProfileOptionItem(Icons.Default.ExitToApp, "Log out", com.example.a216155_cikguizwan_lab03.MidnightBlue)
            Spacer(modifier = Modifier.height(20.dp))
            Text("Delete Account", color = Color(0xFFE91E63), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun EditProfileScreen(paddingValues: PaddingValues, onBack: () -> Unit) {
    var firstName by remember { mutableStateOf("Sharmila") }
    var lastName by remember { mutableStateOf("K.J.") }
    Column(modifier = Modifier
        .fillMaxSize()
        .background(CleanGrayBg)
        .padding(paddingValues)
        .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
            Text("Edit Profile", modifier = Modifier.align(Alignment.Center), fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(30.dp))
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(modifier = Modifier
                .size(120.dp)
                .border(1.dp, MidnightBlue, CircleShape), contentAlignment = Alignment.Center) {
                Text("Profile Picture", fontSize = 12.sp, color = Color.Gray)
            }
            Surface(modifier = Modifier.size(32.dp), shape = CircleShape, color = MidnightBlue) {
                Icon(Icons.Default.Edit, null, tint = Color.White, modifier = Modifier.padding(6.dp))
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
        EditField("First Name", firstName) { firstName = it }
        Spacer(modifier = Modifier.height(16.dp))
        EditField("Last Name", lastName) { lastName = it }
        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = onBack, modifier = Modifier
            .fillMaxWidth()
            .height(55.dp), colors = ButtonDefaults.buttonColors(containerColor = MidnightBlue)) {
            Text("Save", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(onClick = onBack, modifier = Modifier
            .fillMaxWidth()
            .height(55.dp), border = BorderStroke(1.dp, MidnightBlue)) {
            Text("Cancel", color = MidnightBlue)
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
        .background(com.example.a216155_cikguizwan_lab03.CleanGrayBg)
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
            border = BorderStroke(2.dp, com.example.a216155_cikguizwan_lab03.CleanGrayBg)
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
        quadraticTo(size.width * 0.5f, size.height, size.width, size.height * 0.82f)
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

// 2. Updated MenuScreen
@Composable
fun MenuScreen(onTasksClick: () -> Unit) {
    val menuItems = listOf(
        Pair("Tasks", "📋"), Pair("Classes", "📚"), Pair("Exams", "✍️"),
        Pair("Vacations", "🏝️"), Pair("Xtra", "⚡"), Pair("Focus Timer", "⏳"),
        Pair("Ai Schedule Scan", "📸"), Pair("Calendar Sync", "🔗"),
        Pair("Settings", "🛠️"), Pair("Schedule Set Up", "🗓️")
    )

    Column(modifier = Modifier
        .fillMaxSize()
        .background(CleanGrayBg), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Menu", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(menuItems) { item ->
                Card(
                    onClick = { if (item.first == "Tasks") onTasksClick() },
                    modifier = Modifier.aspectRatio(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(item.second, fontSize = 30.sp)
                        Text(item.first, fontSize = 10.sp, textAlign = TextAlign.Center, color = Color.Gray, lineHeight = 12.sp)
                    }
                }
            }
        }
    }
}


// --- [CARDS PART: Task List Screen] ---
@Composable
fun TasksListScreen(onTaskClick: () -> Unit, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(CleanGrayBg)) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp).fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = MidnightBlue)
            }
            Text("Tasks", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.width(48.dp))
        }

        // Tabs
        Row(modifier = Modifier.padding(horizontal = 14.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2)), modifier = Modifier.weight(1f)) { Text("Current") }
            OutlinedButton(onClick = {}, modifier = Modifier.weight(1f)) { Text("Past", color = Color.Black) }
            OutlinedButton(onClick = {}, modifier = Modifier.weight(1f)) { Text("Overdue", color = Color.Black) }
        }

        OutlinedTextField(
            value = "All Subjects", onValueChange = {},
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, null) },
            shape = RoundedCornerShape(12.dp)
        )

        Text("This year", modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), fontWeight = FontWeight.Bold)

        // --- [ANIMATION PART: Assignment Card Animation] ---
        var isPressed by remember { mutableStateOf(false) }
        val scale by animateFloatAsState(
            targetValue = if (isPressed) 1.05f else 1f,
            animationSpec = tween(400),
            label = "ExpandAnimation"
        )

        // --- [CARDS PART: Assignment 1 Card] ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(115.dp)
                .graphicsLayer(scaleX = scale, scaleY = scale) // Animation applied here
                .clickable { isPressed = true },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Background Pattern (Placeholder for the chalkboard image)
                Box(modifier = Modifier.fillMaxWidth(0.45f).fillMaxHeight().align(Alignment.CenterEnd).background(Color.LightGray.copy(alpha = 0.15f)))

                Row(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckBoxOutlineBlank, null, tint = Color.Gray, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Assingment 1", fontWeight = FontWeight.ExtraBold, color = MidnightBlue, fontSize = 18.sp)
                        Text("Due Tue, 05 May  0%", color = Color.Gray, fontSize = 13.sp)
                    }
                }

                // Date Badge on Top-Right
                Surface(color = MidnightBlue, shape = RoundedCornerShape(bottomStart = 12.dp), modifier = Modifier.align(Alignment.TopEnd)) {
                    Text("05 May", color = Color.White, modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        LaunchedEffect(isPressed) {
            if (isPressed) {
                delay(300)
                isPressed = false
                onTaskClick()
            }
        }
    }
}

// --- [CARDS PART: Scrollable Task Detail Screen] ---
@Composable
fun TaskDetailScreen(onBack: () -> Unit) {
    val scrollState = rememberScrollState() // --- [SCROLLER PART] ---

    Column(modifier = Modifier.fillMaxSize().background(CleanGrayBg).verticalScroll(scrollState)) {
        // Header with Math Image area
        Box(modifier = Modifier.fillMaxWidth().height(220.dp).background(Color.DarkGray)) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                IconButton(onClick = onBack, modifier = Modifier.background(Color.Black.copy(alpha = 0.4f), CircleShape)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                }
                IconButton(onClick = {}, modifier = Modifier.background(Color.Black.copy(alpha = 0.4f), CircleShape)) {
                    Icon(Icons.Default.Edit, null, tint = Color.White)
                }
            }

            // --- [CARDS PART: Floating Info Card] ---
            Card(
                modifier = Modifier.align(Alignment.BottomCenter).offset(y = 60.dp).fillMaxWidth().padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(10.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("ASSIGNMENT 1", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = MidnightBlue)
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                        Text("MATHEMATICS", color = Color(0xFF4A90E2), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(color = Color(0xFFE3F2FD), shape = RoundedCornerShape(8.dp)) {
                            Text("Task", color = Color(0xFF4A90E2), modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text("Reminder", color = Color.Gray, fontSize = 13.sp)
                    Row(modifier = Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        DetailChip(Icons.Default.CalendarToday, "May 5, 2026")
                        DetailChip(Icons.Default.Schedule, "12.00 AM")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))

        // Details Body
        Column(modifier = Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Column {
                Text("Progress rate", fontWeight = FontWeight.Bold, color = MidnightBlue)
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).height(45.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFE3F2FD))) {
                    Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(0.08f).background(Color(0xFF4A90E2)))
                    Text("0%", modifier = Modifier.align(Alignment.CenterStart).padding(start = 12.dp), color = Color(0xFF4A90E2), fontWeight = FontWeight.Bold)
                }
            }

            // Button
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth().height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Mark as Completed", fontWeight = FontWeight.Bold)
            }

            Column {
                Text("Description", fontWeight = FontWeight.Bold, color = MidnightBlue)
                Text("Calculation", color = Color.Gray, fontSize = 15.sp)
            }

            // --- [CARDS PART: Subtasks Card] ---
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("👑", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Subtasks - this occurrence", color = Color(0xFF4A90E2), modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
                    Icon(Icons.Default.KeyboardArrowDown, null, tint = Color(0xFF4A90E2))
                }
            }

            // --- [CARDS PART: Pomodoro Timer Card] ---
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(modifier = Modifier.size(45.dp), color = Color(0xFFE3F2FD), shape = CircleShape) {
                            Icon(Icons.Default.Timer, null, tint = Color(0xFF4A90E2), modifier = Modifier.padding(10.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Pomodoro Timer", fontWeight = FontWeight.Bold, color = MidnightBlue)
                            Text("Boost your productivity by alternating...", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                    Button(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp).height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Start study session")
                    }
                }
            }
            Spacer(modifier = Modifier.height(100.dp))
        }
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