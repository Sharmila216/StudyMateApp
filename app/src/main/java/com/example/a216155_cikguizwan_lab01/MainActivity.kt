package com.example.a216155_cikguizwan_lab01

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewStudyMateApp() {
    StudyMateApp()
}
val PrimaryPurple = Color(0xFF7C83FD)
val SoftPink = Color(0xFFFFB3C6)
val BodyPinkBg = Color(0xFFFFF0F3)
val BoxBorderBlue = Color(0xFFADCFFF)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudyMateApp()
        }
    }
}

@Composable
fun StudyMateApp() {
    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .height(70.dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BottomNavItem(Icons.Filled.Home, Icons.Outlined.Home, true)
                    BottomNavItem(Icons.Filled.DateRange, Icons.Outlined.DateRange, false)

                    Spacer(modifier = Modifier.width(60.dp))

                    BottomNavItem(Icons.Filled.Menu, Icons.Outlined.Menu, false)
                    BottomNavItem(Icons.Filled.Person, Icons.Outlined.Person, false)
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
                containerColor = PrimaryPurple,
                contentColor = Color.White,
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .size(65.dp)
                    .offset(y = 55.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(30.dp))
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BodyPinkBg)
                .padding(paddingValues)
        ) {
            HeaderWithWave()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 25.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatBox("0 Classes")
                StatBox("0 Exams")
                StatBox("0 Tasks Due")
                StatBox("0 Credits")
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_book),
                    contentDescription = "Book",
                    tint = SoftPink.copy(alpha = 0.4f),
                    modifier = Modifier.size(130.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "No classes, tasks or exams left for today",
                    fontSize = 17.sp,
                    fontFamily = FontFamily.Serif,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 40.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "You can adjust what is displayed on your homepage in personalised settings.",
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Serif,
                    color = PrimaryPurple.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 40.dp)
                )
            }
        }
    }
}

@Composable
fun HeaderWithWave() {
    val waveShape = GenericShape { size, _ ->
        lineTo(0f, size.height * 0.82f)
        quadraticTo(size.width * 0.5f, size.height, size.width, size.height * 0.82f)
        lineTo(size.width, 0f)
        close()
    }

    val headerGradient = Brush.verticalGradient(colors = listOf(PrimaryPurple, SoftPink))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(waveShape)
            .background(headerGradient)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = { },
            modifier = Modifier.align(Alignment.TopEnd).size(36.dp)
        ) {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Tuesday",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = Color.White
            )
            Text(
                text = "March 24, 2026",
                fontSize = 16.sp,
                fontFamily = FontFamily.Serif,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
fun StatBox(label: String) {
    Surface(
        modifier = Modifier.width(120.dp).height(55.dp),
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, BoxBorderBlue)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = PrimaryPurple,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Serif
            )
        }
    }
}

@Composable
fun BottomNavItem(filledIcon: ImageVector, outlinedIcon: ImageVector, isSelected: Boolean) {
    IconButton(onClick = { }, modifier = Modifier.size(48.dp)) {
        Icon(
            imageVector = if (isSelected) filledIcon else outlinedIcon,
            contentDescription = null,
            tint = if (isSelected) PrimaryPurple else Color.LightGray
        )
    }
}

