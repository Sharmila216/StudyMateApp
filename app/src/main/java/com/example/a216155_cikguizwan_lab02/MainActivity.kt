package com.example.a216155_cikguizwan_lab02

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material.icons.filled.Edit
import androidx.compose.foundation.lazy.grid.*


// --- THEME COLORS ---
val PrimaryPurple = Color(0xFF7C83FD)
val SoftPink = Color(0xFFFFB3C6)
val BodyPinkBg = Color(0xFFFFF0F3)
val BoxBorderBlue = Color(0xFFADCFFF)

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
                modifier = Modifier
                    .height(70.dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                tonalElevation = 8.dp
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    IconButton(onClick = { currentPage = "dashboard" }) {
                        Icon(Icons.Filled.Home, null, tint = if (currentPage == "dashboard") PrimaryPurple else Color.LightGray)
                    }
                    IconButton(onClick = { currentPage = "calendar" }) {
                        Icon(Icons.Outlined.DateRange, null, tint = if (currentPage == "calendar") PrimaryPurple else Color.LightGray)
                    }
                    Spacer(modifier = Modifier.width(60.dp))
                    IconButton(onClick = { currentPage = "menu" }) {
                        Icon(Icons.Default.GridView, null, tint = if (currentPage == "menu") PrimaryPurple else Color.LightGray)
                    }
                    IconButton(onClick = { currentPage = "profile" }) {
                        Icon(Icons.Outlined.Person, null, tint = if (currentPage == "profile") PrimaryPurple else Color.LightGray)
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { currentPage = "add_new" },
                containerColor = PrimaryPurple,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(65.dp).offset(y = 55.dp)
            ) {
                Icon(Icons.Default.Edit, null, modifier = Modifier.size(30.dp))
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (currentPage) {
                // FIXED: Explicitly passing all required parameters for each screen
               "dashboard" -> DashboardScreen(paddingValues, onSearchClick = { currentPage = "search" })
                "calendar" -> CalendarScreen(paddingValues)
                "menu" -> MenuScreen(paddingValues)
                "profile" -> ProfileScreen(paddingValues, onEditClick = { /* Add logic */ })
                "add_new" -> AddNewScreen(paddingValues = paddingValues, onBack = { currentPage = "dashboard" })
                "search" -> SearchPage(onBack = { currentPage = "dashboard" })
                "edit_profile" -> EditProfileScreen(paddingValues, onBack = { currentPage = "profile" })
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
            .background(BodyPinkBg)
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
            .background(BodyPinkBg)
            .padding(paddingValues) // Prevents UI from being hidden under bars
            .verticalScroll(rememberScrollState())
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = PrimaryPurple)
            }
            Text("Add New", modifier = Modifier.align(Alignment.Center), fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            categories.forEach { name ->
                val isSelected = selectedTab == name
                Button(
                    onClick = { selectedTab = name },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isSelected) PrimaryPurple else Color.White),
                    border = if (!isSelected) BorderStroke(1.dp, BoxBorderBlue) else null,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(name, color = if (isSelected) Color.White else PrimaryPurple)
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
                Button(onClick = {}, colors = ButtonDefaults.buttonColors(PrimaryPurple)) { Text("Once") }
                OutlinedButton(onClick = {}, border = BorderStroke(1.dp, PrimaryPurple)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Repeating", color = PrimaryPurple)
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.Default.WorkspacePremium, null, tint = PrimaryPurple, modifier = Modifier.size(16.dp))
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
                modifier = Modifier.fillMaxWidth().height(55.dp),
                colors = ButtonDefaults.buttonColors(PrimaryPurple),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save (4 free tasks remaining)", fontWeight = FontWeight.Bold)
            }
            OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth().height(55.dp), border = BorderStroke(1.dp, PrimaryPurple), shape = RoundedCornerShape(12.dp)) {
                Text("Cancel", color = PrimaryPurple)
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
                focusedBorderColor = PrimaryPurple,
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
        modifier = Modifier.fillMaxSize().background(BodyPinkBg).padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text("Profile", modifier = Modifier.align(Alignment.Center), fontSize = 20.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = onEditClick, modifier = Modifier.align(Alignment.CenterEnd)) {
                Icon(Icons.Default.Edit, null, tint = Color.Gray)
            }
        }
        Box(modifier = Modifier.size(120.dp).border(1.dp, PrimaryPurple, CircleShape), contentAlignment = Alignment.Center) {
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
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            ProfileOptionItem(Icons.Default.WorkspacePremium, "Premium Subscription", PrimaryPurple)
            ProfileOptionItem(Icons.Default.ExitToApp, "Log out", PrimaryPurple)
            Spacer(modifier = Modifier.height(20.dp))
            Text("Delete Account", color = Color(0xFFE91E63), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun EditProfileScreen(paddingValues: PaddingValues, onBack: () -> Unit) {
    var firstName by remember { mutableStateOf("Sharmila") }
    var lastName by remember { mutableStateOf("K.J.") }
    Column(modifier = Modifier.fillMaxSize().background(BodyPinkBg).padding(paddingValues).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
            Text("Edit Profile", modifier = Modifier.align(Alignment.Center), fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(30.dp))
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(modifier = Modifier.size(120.dp).border(1.dp, PrimaryPurple, CircleShape), contentAlignment = Alignment.Center) {
                Text("Profile Picture", fontSize = 12.sp, color = Color.Gray)
            }
            Surface(modifier = Modifier.size(32.dp), shape = CircleShape, color = PrimaryPurple) {
                Icon(Icons.Default.Edit, null, tint = Color.White, modifier = Modifier.padding(6.dp))
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
        EditField("First Name", firstName) { firstName = it }
        Spacer(modifier = Modifier.height(16.dp))
        EditField("Last Name", lastName) { lastName = it }
        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth().height(55.dp), colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)) {
            Text("Save", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth().height(55.dp), border = BorderStroke(1.dp, PrimaryPurple)) {
            Text("Cancel", color = PrimaryPurple)
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
fun ProfileOptionItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, color: Color) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
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
        OutlinedTextField(value = value, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryPurple))
    }
}
@Composable
fun DashboardScreen(paddingValues: PaddingValues, onSearchClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(BodyPinkBg).padding(paddingValues).verticalScroll(rememberScrollState())) {
        HeaderSection(onSearchClick)

        Row(modifier = Modifier.fillMaxWidth().padding(16.dp).horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatBox("0 Classes")
            StatBox("0 Exams")
            StatBox("0 Tasks Due")
            StatBox("0 Calendar Sync")
        }

        Column(modifier = Modifier.fillMaxWidth().padding(top = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            // Fixed the icon error by using a built-in Material icon instead of a potentially missing resource
            Icon(Icons.Default.MenuBook, null, tint = SoftPink.copy(alpha = 0.4f), modifier = Modifier.size(130.dp))
            Spacer(modifier = Modifier.height(20.dp))
            Text("No classes, tasks or exams left for today", fontSize = 16.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "You can adjust what is displayed on your homepage in personalised settings.",
                fontSize = 14.sp, color = PrimaryPurple, textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 45.dp)
            )
        }
    }
}

@Composable
fun CalendarScreen(paddingValues: PaddingValues) {
    var calendarViewMode by remember { mutableStateOf("Day") }
    var selectedMonthIndex by remember { mutableStateOf(3) }
    val months = listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")

    val categoryTicks = remember {
        mutableStateMapOf(
            "Classes" to true, "Exams" to true, "Tasks" to true,
            "Holidays" to true, "Xtra" to true, "ICal" to true
        )
    }

    var currentTime by remember { mutableStateOf(SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(Date())) }
    var currentDateDisplay by remember { mutableStateOf(SimpleDateFormat("EEEE, MMMM d", Locale.ENGLISH).format(Date())) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(Date())
            currentDateDisplay = SimpleDateFormat("EEEE, MMMM d", Locale.ENGLISH).format(Date())
            delay(1000)
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(paddingValues)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            var showMenu by remember { mutableStateOf(false) }
            Box {
                IconButton(onClick = { showMenu = true }) { Icon(Icons.Default.Menu, null, tint = PrimaryPurple) }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.width(260.dp).background(Color.White)
                ) {
                    DropdownMenuItem(
                        leadingIcon = { Icon(Icons.Default.List, null) },
                        text = { Text("Day") },
                        onClick = { calendarViewMode = "Day"; showMenu = false }
                    )
                    DropdownMenuItem(
                        leadingIcon = { Icon(Icons.Default.DateRange, null) },
                        text = { Text("Week") },
                        onClick = { calendarViewMode = "Week"; showMenu = false }
                    )
                    DropdownMenuItem(
                        leadingIcon = { Icon(Icons.Default.Event, null) },
                        text = { Text("Month") },
                        onClick = { calendarViewMode = "Month"; showMenu = false }
                    )

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    val categories = listOf(
                        "Classes" to Color(0xFF81C784),
                        "Exams" to Color(0xFFBA68C8),
                        "Tasks" to Color(0xFF4FC3F7),
                        "Holidays" to Color(0xFFFFB74D),
                        "Xtra" to Color(0xFFFFF176),
                        "ICal" to Color(0xFFFFAB91)
                    )

                    categories.forEach { (label, color) ->
                        val isTicked = categoryTicks[label] ?: false
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(16.dp).background(color, RoundedCornerShape(4.dp)))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(label, modifier = Modifier.weight(1f))
                                    if (isTicked) {
                                        Icon(Icons.Filled.CheckCircle, null, tint = PrimaryPurple, modifier = Modifier.size(20.dp))
                                    }
                                }
                            },
                            onClick = {
                                categoryTicks[label] = !isTicked
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = currentDateDisplay, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryPurple)
                Text(text = "Time: $currentTime", fontSize = 14.sp, color = SoftPink)
            }

            Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = SoftPink.copy(alpha = 0.2f)), shape = RoundedCornerShape(8.dp)) {
                Text("Today", color = PrimaryPurple)
            }
        }

        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
            months.forEachIndexed { index, month ->
                Text(
                    text = month,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp).clickable { selectedMonthIndex = index },
                    color = if (selectedMonthIndex == index) PrimaryPurple else Color.Gray,
                    fontWeight = if (selectedMonthIndex == index) FontWeight.Bold else FontWeight.Normal
                )
            }
        }

        Box(modifier = Modifier.weight(1f).padding(16.dp)) {
            when (calendarViewMode) {
                "Day" -> DayView()
                "Week" -> WeekView()
                "Month" -> MonthView()
            }
        }
    }
}

@Composable
fun DayView() {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        (8..20).forEach { hour ->
            Row(modifier = Modifier.fillMaxWidth().height(60.dp)) {
                Text("${if (hour > 12) hour - 12 else hour} ${if (hour >= 12) "PM" else "AM"}",
                    modifier = Modifier.width(60.dp), fontSize = 12.sp, color = Color.Gray)
                Divider(modifier = Modifier.padding(top = 8.dp), color = SoftPink.copy(alpha = 0.2f))
            }
        }
    }
}

@Composable
fun WeekView() {
    Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceBetween) {
        listOf("M", "T", "W", "T", "F", "S", "S").forEach { day ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(day, fontWeight = FontWeight.Bold, color = PrimaryPurple)
                repeat(10) {
                    Box(modifier = Modifier.size(30.dp).padding(2.dp).background(SoftPink.copy(alpha = 0.1f), RoundedCornerShape(4.dp)))
                }
            }
        }
    }
}

@Composable
fun MonthView() {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            listOf("M", "T", "W", "T", "F", "S", "S").forEach { day ->
                Text(day, fontWeight = FontWeight.Bold, color = PrimaryPurple)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val daysInMonth = 31
        var dayCounter = 1

        repeat(5) {
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceAround) {
                repeat(7) {
                    if (dayCounter <= daysInMonth) {
                        Box(
                            modifier = Modifier.size(38.dp).background(SoftPink.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = dayCounter.toString(), fontSize = 14.sp, color = Color.DarkGray)
                        }
                        dayCounter++
                    } else {
                        Spacer(modifier = Modifier.size(38.dp))
                    }
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
    Box(modifier = Modifier.fillMaxWidth().height(220.dp).clip(waveShape).background(Brush.verticalGradient(listOf(PrimaryPurple, SoftPink))).padding(20.dp)) {
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

    Column(modifier = Modifier.fillMaxSize().background(BodyPinkBg).padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = query, onValueChange = { query = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Search") },
                leadingIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.Search, null) } },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryPurple)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box {
                Button(
                    onClick = { showFilterMenu = true },
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.List, null, tint = Color.White)
                }

                DropdownMenu(
                    expanded = showFilterMenu,
                    onDismissRequest = { showFilterMenu = false },
                    modifier = Modifier.width(220.dp).background(Color.White).border(2.dp, PrimaryPurple, RoundedCornerShape(12.dp))
                ) {
                    listOf("Classes", "Exams", "Tasks", "Holidays", "Extra", "ICal").forEach { label ->
                        val isTicked = selectedFilters.contains(label)
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(24.dp).clip(RoundedCornerShape(6.dp)).background(SoftPink.copy(alpha = 0.3f)))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(label, color = Color.Black, modifier = Modifier.weight(1f))

                                    // Tick only shows if this specific item was clicked
                                    if (isTicked) {
                                        Icon(Icons.Filled.CheckCircle, null, tint = PrimaryPurple, modifier = Modifier.size(20.dp))
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
                    Box(modifier = Modifier.fillMaxWidth().height(80.dp).clip(RoundedCornerShape(16.dp)).background(Color.LightGray.copy(alpha = 0.3f)))
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
    Surface(modifier = Modifier.width(140.dp).height(55.dp), shape = RoundedCornerShape(14.dp), color = Color.White, border = BorderStroke(1.dp, BoxBorderBlue)) {
        Box(contentAlignment = Alignment.Center) {
            Text(label, color = PrimaryPurple, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}
