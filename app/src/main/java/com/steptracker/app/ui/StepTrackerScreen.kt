package com.steptracker.app.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.steptracker.app.R
import com.steptracker.app.data.DailyStepRecord
import com.steptracker.app.ui.theme.*
import java.util.Locale

/* =====================================================================
   DATA MODELS FOR THALI & DIET PLANNER
   ===================================================================== */

data class KatoriItem(
    val slotId: String,       // "center", "katori1", "katori2", "katori3", "katori4", "katori5"
    val bowlLabel: String,    // "Grains", "Dal", "Sabzi", "Curd", "Salad", "Roti"
    val name: String,
    val emoji: String,
    val calories: Int,
    val description: String,
    val proteinG: Int,
    val carbsG: Int,
    val fatG: Int
)

data class DietMeal(
    val id: String,
    val name: String,
    val emoji: String,
    val calories: Int,
    val slot: String,         // "breakfast", "lunch", "snacks", "dinner", "latenight"
    val description: String,
    val proteinG: Int,
    val carbsG: Int,
    val fatG: Int,
    val isWeightGainRecommended: Boolean = false,
    val isWeightLossRecommended: Boolean = false
)

/* =====================================================================
   MAIN SCREEN SCAFFOLDING
   ===================================================================== */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepTrackerScreen(
    currentSteps: Int,
    dailyGoal: Int,
    distanceKm: Double,
    calories: Double,
    activeMinutes: Int,
    recentHistory: List<DailyStepRecord>,
    waterConsumedMl: Int,
    waterTargetMl: Int,
    streakDays: Int,
    restDaysUsed: Int,
    onUpdateGoal: (Int) -> Unit,
    onLogWater: (Int) -> Unit,
    onAddTestSteps: (Int) -> Unit,
    onDonateClick: () -> Unit,
    onCopyUpiClick: () -> Unit,
    onInstagramClick: () -> Unit,
    showFallbackSheet: Boolean,
    onDismissFallbackSheet: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showGoalDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            HealbudsHeader(
                streakDays = streakDays,
                onHeartClick = onDonateClick,
                onEditGoalClick = { showGoalDialog = true }
            )
        },
        bottomBar = {
            HealbudsBottomNav(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                0 -> TodayStepsTab(
                    currentSteps = currentSteps,
                    dailyGoal = dailyGoal,
                    distanceKm = distanceKm,
                    calories = calories,
                    activeMinutes = activeMinutes,
                    onAddTestSteps = onAddTestSteps,
                    onDonateClick = onDonateClick,
                    onInstagramClick = onInstagramClick
                )
                1 -> StreaksCalendarTab(
                    streakDays = streakDays,
                    restDaysUsed = restDaysUsed,
                    onDonateClick = onDonateClick,
                    onInstagramClick = onInstagramClick
                )
                2 -> IndianThaliDietTab(
                    onDonateClick = onDonateClick,
                    onInstagramClick = onInstagramClick
                )
                3 -> HydrationTab(
                    waterConsumedMl = waterConsumedMl,
                    waterTargetMl = waterTargetMl,
                    onAddGlass = { onLogWater(250) },
                    onAddBottle = { onLogWater(500) },
                    onDonateClick = onDonateClick,
                    onInstagramClick = onInstagramClick
                )
                4 -> SettingsTab(
                    dailyGoal = dailyGoal,
                    onEditGoalClick = { showGoalDialog = true },
                    onDonateClick = onDonateClick,
                    onCopyUpiClick = onCopyUpiClick,
                    onInstagramClick = onInstagramClick
                )
            }

            if (showGoalDialog) {
                GoalDialog(
                    currentGoal = dailyGoal,
                    onDismiss = { showGoalDialog = false },
                    onSelect = {
                        onUpdateGoal(it)
                        showGoalDialog = false
                    }
                )
            }

            if (showFallbackSheet) {
                DonateFallbackModalSheet(
                    onDismiss = onDismissFallbackSheet,
                    onCopyUpiClick = onCopyUpiClick
                )
            }
        }
    }
}

/* =====================================================================
   HEADER & BOTTOM NAVIGATION
   ===================================================================== */

@Composable
fun HealbudsHeader(
    streakDays: Int,
    onHeartClick: () -> Unit,
    onEditGoalClick: () -> Unit
) {
    Surface(
        color = BackgroundDark,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(EmeraldGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Healbuds",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = SurfaceDark,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "On your device",
                            color = EmeraldGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = "Wednesday, Nov 4",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Header Heart Button (Brief 2a)
                IconButton(
                    onClick = onHeartClick,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(SurfaceDark)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Donate to the developer",
                        tint = RosePink,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Goal Edit button
                IconButton(
                    onClick = onEditGoalClick,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(SurfaceDark)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Daily Goal",
                        tint = EmeraldGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun HealbudsBottomNav(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = SurfaceDark,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            Triple(0, "Steps", Icons.Default.DirectionsWalk),
            Triple(1, "Streaks", Icons.Default.LocalFireDepartment),
            Triple(2, "Thali", Icons.Default.Restaurant),
            Triple(3, "Water", Icons.Default.WaterDrop),
            Triple(4, "More", Icons.Default.Settings)
        )

        items.forEach { (index, label, icon) ->
            NavigationBarItem(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = EmeraldGreen,
                    selectedTextColor = EmeraldGreen,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = EmeraldGreen.copy(alpha = 0.2f)
                )
            )
        }
    }
}

/* =====================================================================
   UNIVERSAL COMPONENTS (DONATE ROW & FOOTER CREDIT)
   ===================================================================== */

@Composable
fun UniversalDonateRow(onClick: () -> Unit) {
    Surface(
        color = SurfaceDark,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, RosePink.copy(alpha = 0.35f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.mascot_heart),
                contentDescription = "Support Developer",
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Donate to the developer",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "Pay any amount · helps keep Healbuds running and ad-free",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 14.sp
                )
            }

            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = RosePink,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun UniversalFooterCredit(onInstagramClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Private, on this device · records reflect what the user reports",
            color = TextMuted,
            fontSize = 11.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onInstagramClick() }
        ) {
            Text(
                text = "Made with love ♥ by Vijay Mamta Sahani (",
                color = TextSecondary,
                fontSize = 11.sp
            )
            Text(
                text = "@momentswithvms",
                color = RosePink,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = ")",
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
    }
}

/* =====================================================================
   TAB 0: STEPS & ACTIVITY (SPACED PROGRESS RING & MASCOT)
   ===================================================================== */

@Composable
fun TodayStepsTab(
    currentSteps: Int,
    dailyGoal: Int,
    distanceKm: Double,
    calories: Double,
    activeMinutes: Int,
    onAddTestSteps: (Int) -> Unit,
    onDonateClick: () -> Unit,
    onInstagramClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val progress = remember(currentSteps, dailyGoal) {
        if (dailyGoal > 0) (currentSteps.toFloat() / dailyGoal).coerceIn(0f, 1f) else 0f
    }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000),
        label = "progress_anim"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Large Circular Step Progress Ring (265dp with generous clearance)
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(265.dp)
                .padding(6.dp)
        ) {
            Canvas(modifier = Modifier.size(245.dp)) {
                drawCircle(
                    color = TrackBackground,
                    style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                )
                drawArc(
                    brush = Brush.sweepGradient(listOf(EmeraldGreen, EmeraldLight, EmeraldGreen)),
                    startAngle = -90f,
                    sweepAngle = animatedProgress * 360f,
                    useCenter = false,
                    style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // Interior elements spaced generously to avoid collision
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 28.dp)
            ) {
                Text(text = "👟", fontSize = 24.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = String.format(Locale.getDefault(), "%,d", currentSteps),
                    color = TextPrimary,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 40.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = String.format(Locale.getDefault(), "Daily Goal: %,d steps", dailyGoal),
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(10.dp))
                // Percentage completed pill with ample breathing room
                val percentage = (progress * 100).toInt()
                Surface(
                    color = EmeraldGreen.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "$percentage% Completed",
                        color = EmeraldGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Hardware Cadence Active Badge
        Surface(
            color = SurfaceDark,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = EmeraldGreen,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "10-Step Cadence Filter Active",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Vehicle vibration rejection · On-device pedometer coprocessor",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Mascot Motivation Card
        Surface(
            color = SurfaceDark,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(
                        id = if (progress >= 1f) R.drawable.mascot_victory else R.drawable.mascot_walking
                    ),
                    contentDescription = "Mascot",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (progress >= 1f) "Goal Crushed! 🎉" else "VMS Buddy 👟",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = if (progress >= 1f)
                            "You crushed your daily goal! Celebrate and keep your streak going!"
                        else
                            "\"${String.format(Locale.getDefault(), "%,d", currentSteps)} steps crushed! Only ${String.format(Locale.getDefault(), "%,d", (dailyGoal - currentSteps).coerceAtLeast(0))} more to hit our daily goal. Let's walk together!\"",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3 Key Health Metrics
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.PinDrop,
                iconTint = OceanBlue,
                value = String.format(Locale.getDefault(), "%.2f", distanceKm),
                unit = "km",
                label = "Distance"
            )
            MetricCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.LocalFireDepartment,
                iconTint = AmberFire,
                value = String.format(Locale.getDefault(), "%.0f", calories),
                unit = "kcal",
                label = "Calories"
            )
            MetricCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Schedule,
                iconTint = RoyalPurple,
                value = activeMinutes.toString(),
                unit = "min",
                label = "Active"
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Test Walk Buttons (For instant testing without walking outdoors)
        Surface(
            color = SurfaceDark,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quick Test Walk:",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { onAddTestSteps(500) },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("+500", color = EmeraldGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { onAddTestSteps(1000) },
                        colors = ButtonDefaults.buttonColors(containerColor = OceanBlue.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("+1,000", color = OceanBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        UniversalDonateRow(onClick = onDonateClick)
        Spacer(modifier = Modifier.height(10.dp))
        UniversalFooterCredit(onInstagramClick = onInstagramClick)
    }
}

/* =====================================================================
   TAB 1: STREAKS & COMPLETE 30-DAY CALENDAR
   ===================================================================== */

@Composable
fun StreaksCalendarTab(
    streakDays: Int,
    restDaysUsed: Int,
    onDonateClick: () -> Unit,
    onInstagramClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Streak Banner with Relaxing Mascot
        Surface(
            color = SurfaceDark,
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.mascot_relaxing),
                        contentDescription = "Rest Day Mascot",
                        modifier = Modifier
                            .size(68.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "$streakDays Day Streak",
                                color = TextPrimary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "🔥", fontSize = 20.sp)
                        }
                        Text(
                            text = "Streak Protected by Smart Rest Days",
                            color = EmeraldGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = SurfaceLightDark)
                Spacer(modifier = Modifier.height(12.dp))

                // Rest Day Rule
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "2 Rest Days Allowed / Week",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Monday to Sunday · Guilt-free recovery",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    Surface(
                        color = OceanBlue.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "$restDaysUsed / 2 Used",
                            color = OceanBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3-Day Inactivity Warning Card
        Surface(
            color = SurfaceDark,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.mascot_sad),
                    contentDescription = "Sad Mascot",
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "3-Day Gap Rule",
                        color = AmberFire,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "If inactive for 3 consecutive days without rest credit, streak resets to Day 1 and archives to history.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Active Streak Celebration Card with Dancing Mascot
        Surface(
            color = SurfaceDark,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.mascot_dancing),
                    contentDescription = "Dancing Mascot",
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Consistency is Key! 🎉",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "$streakDays active walking days! Taking planned rest days keeps your joints injury-free and metabolism high.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // COMPLETE 30-DAY MONTHLY CALENDAR GRID (November 2026)
        Surface(
            color = SurfaceDark,
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "November 2026",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "12 Active Days",
                        color = EmeraldGreen,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Day headers (Mon - Sun)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("M", "T", "W", "T", "F", "S", "S").forEach { day ->
                        Text(
                            text = day,
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = SurfaceLightDark)
                Spacer(modifier = Modifier.height(8.dp))

                // All 30 Days of November 2026 across 6 weeks
                val weeks = listOf(
                    listOf(Pair(26, false), Pair(27, false), Pair(28, false), Pair(29, false), Pair(30, false), Pair(31, false), Pair(1, true)),
                    listOf(Pair(2, true), Pair(3, true), Pair(4, false), Pair(5, false), Pair(6, false), Pair(7, false), Pair(8, false)),
                    listOf(Pair(9, false), Pair(10, false), Pair(11, false), Pair(12, false), Pair(13, false), Pair(14, false), Pair(15, false)),
                    listOf(Pair(16, false), Pair(17, false), Pair(18, false), Pair(19, false), Pair(20, false), Pair(21, false), Pair(22, false)),
                    listOf(Pair(23, false), Pair(24, false), Pair(25, false), Pair(26, false), Pair(27, false), Pair(28, false), Pair(29, false)),
                    listOf(Pair(30, false), Pair(1, false), Pair(2, false), Pair(3, false), Pair(4, false), Pair(5, false), Pair(6, false))
                )

                weeks.forEach { week ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        week.forEach { (dayNum, isCompleted) ->
                            val isToday = (dayNum == 4)
                            val isRestDay = (dayNum == 3)
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .weight(1f)
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isToday -> EmeraldGreen
                                            isRestDay -> OceanBlue.copy(alpha = 0.25f)
                                            isCompleted -> EmeraldGreen.copy(alpha = 0.2f)
                                            else -> Color.Transparent
                                        }
                                    )
                                    .border(
                                        width = if (isToday) 2.dp else 0.dp,
                                        color = if (isToday) EmeraldLight else Color.Transparent,
                                        shape = CircleShape
                                    )
                            ) {
                                Text(
                                    text = dayNum.toString(),
                                    color = when {
                                        isToday -> BackgroundDark
                                        isCompleted -> EmeraldGreen
                                        isRestDay -> OceanBlue
                                        else -> TextSecondary
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = if (isToday || isCompleted || isRestDay) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        UniversalDonateRow(onClick = onDonateClick)
        Spacer(modifier = Modifier.height(10.dp))
        UniversalFooterCredit(onInstagramClick = onInstagramClick)
    }
}

/* =====================================================================
   TAB 2: AUTHENTIC CIRCULAR INDIAN THALI & MY DIET PLANNER
   ===================================================================== */

@Composable
fun IndianThaliDietTab(
    onDonateClick: () -> Unit,
    onInstagramClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    // Weight Goal Mode: "lose", "gain", "maintain"
    var weightGoal by remember { mutableStateOf("lose") }
    // Veg / Non-Veg Toggle
    var isVeg by remember { mutableStateOf(true) }
    // Selected Meal Slot: "breakfast", "lunch", "snacks", "dinner", "latenight"
    var selectedMealSlot by remember { mutableStateOf("lunch") }

    // Dialogs
    var showDishPickerSlot by remember { mutableStateOf<String?>(null) }
    var showCustomDishDialog by remember { mutableStateOf(false) }

    // Interactive Thali Katoris State
    var centerItem by remember {
        mutableStateOf(
            KatoriItem("center", "Grains", "Basmati Rice", "🍚", 210, "Steamed fragrant white rice", 4, 45, 1)
        )
    }
    var katori1Item by remember {
        mutableStateOf(
            KatoriItem("katori1", "Dal", "Dal Tadka", "🥣", 180, "Yellow lentils with cumin & garlic", 11, 24, 5)
        )
    }
    var katori2Item by remember {
        mutableStateOf(
            KatoriItem("katori2", "Sabzi", "Rajma Masala", "🍛", 240, "Red kidney beans in spiced gravy", 13, 38, 4)
        )
    }
    var katori3Item by remember {
        mutableStateOf(
            KatoriItem("katori3", "Curd", "Curd / Dahi", "🥛", 100, "Fresh probiotic curd", 6, 8, 5)
        )
    }
    var katori4Item by remember {
        mutableStateOf(
            KatoriItem("katori4", "Salad", "Salad & Achar", "🥗", 35, "Fresh cucumber, tomato & lemon pickle", 1, 7, 0)
        )
    }
    var katori5Item by remember {
        mutableStateOf(
            KatoriItem("katori5", "Roti", "2x Roti", "🫓", 160, "Whole wheat phulka", 6, 32, 1)
        )
    }

    // Dynamic total calculations
    val totalThaliCalories = centerItem.calories + katori1Item.calories + katori2Item.calories +
            katori3Item.calories + katori4Item.calories + katori5Item.calories
    val totalProtein = centerItem.proteinG + katori1Item.proteinG + katori2Item.proteinG +
            katori3Item.proteinG + katori4Item.proteinG + katori5Item.proteinG
    val totalCarbs = centerItem.carbsG + katori1Item.carbsG + katori2Item.carbsG +
            katori3Item.carbsG + katori4Item.carbsG + katori5Item.carbsG

    // Custom Meals List
    var customMeals by remember {
        mutableStateOf(
            listOf(
                DietMeal("c1", "Poha & Peanuts", "🥣", 210, "breakfast", "Light breakfast staple", 5, 38, 5),
                DietMeal("c2", "Roasted Makhana", "🍿", 110, "snacks", "Light crispy foxnuts", 3, 20, 2),
                DietMeal("c3", "Moong Khichdi", "🍲", 260, "dinner", "Comforting digestible dinner", 9, 44, 4),
                DietMeal("c4", "Warm Haldi Doodh", "🥛", 120, "latenight", "Promotes deep sleep and joint repair", 4, 12, 5)
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // 1. Weight Goal Switcher (Lose Weight vs Gain Weight vs Maintain)
        Surface(
            color = SurfaceDark,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(
                    Triple("lose", "🔻 Lose Weight", EmeraldGreen),
                    Triple("gain", "🔺 Gain Weight", AmberFire),
                    Triple("maintain", "⚖️ Maintain", OceanBlue)
                ).forEach { (goalKey, label, accentColor) ->
                    val isSelected = weightGoal == goalKey
                    Button(
                        onClick = { weightGoal = goalKey },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) accentColor else SurfaceDark,
                            contentColor = if (isSelected) BackgroundDark else TextSecondary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = label,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 2. Pure Veg vs Non-Veg Toggle
        Surface(
            color = SurfaceDark,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Button(
                    onClick = {
                        isVeg = true
                        katori2Item = KatoriItem("katori2", "Sabzi", "Rajma Masala", "🍛", 240, "Red kidney beans in gravy", 13, 38, 4)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isVeg) EmeraldGreen.copy(alpha = 0.25f) else SurfaceDark,
                        contentColor = if (isVeg) EmeraldGreen else TextSecondary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("🌱 Pure Veg Thali", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Button(
                    onClick = {
                        isVeg = false
                        katori2Item = KatoriItem("katori2", "Sabzi", "Chicken Curry", "🍗", 280, "Lean chicken in aromatic gravy", 28, 8, 14)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isVeg) AmberFire.copy(alpha = 0.25f) else SurfaceDark,
                        contentColor = if (!isVeg) AmberFire else TextSecondary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("🍗 Non-Veg Thali", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 3. Mascot Gym Fuel Banner with Live Calories
        Surface(
            color = SurfaceDark,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.mascot_gym),
                    contentDescription = "Gym Mascot",
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(14.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = when (weightGoal) {
                            "lose" -> "Fuel Your Deficit: High Protein & Fiber"
                            "gain" -> "Fuel Your Surplus: Nutrient Dense Energy"
                            else -> "Fuel Your Health & Steps: Balanced Macros"
                        },
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Current Meal Total: $totalThaliCalories kcal · $totalProtein g Protein · $totalCarbs g Carbs. Daal for protein, Roti/Rice for energy, Salad for fiber.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 4. Meal Category Selector Tabs (Breakfast, Lunch, Snacks, Dinner, Late Night)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                Pair("breakfast", "🌅 Breakfast"),
                Pair("lunch", "☀️ Lunch (Thali)"),
                Pair("snacks", "☕ Snacks"),
                Pair("dinner", "🌙 Dinner"),
                Pair("latenight", "🌃 Late Night")
            ).forEach { (slotKey, label) ->
                val isSelected = selectedMealSlot == slotKey
                Surface(
                    color = if (isSelected) EmeraldGreen else SurfaceDark,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, if (isSelected) EmeraldGreen else SurfaceLightDark),
                    modifier = Modifier.clickable { selectedMealSlot = slotKey }
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) BackgroundDark else TextSecondary,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 5. Dynamic Content Depending on Selected Meal Slot
        if (selectedMealSlot == "lunch") {
            // ==========================================
            // LUNCH: INTERACTIVE VISUAL THALI PLATE
            // ==========================================
            Surface(
                color = SurfaceDark,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Interactive Thali Plate",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Tap any bowl to swap dish",
                            color = EmeraldGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // THE CIRCULAR THALI PLATE (280dp diameter metallic steel circle)
                    Box(
                        modifier = Modifier
                            .size(280.dp)
                            .shadow(16.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(Color(0xFF475569), Color(0xFF1E293B), Color(0xFF0F172A))
                                )
                            )
                            .border(4.dp, Color(0xFF64748B), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        // 1. Center Katori (Rice / Khichdi / Grains)
                        KatoriBowlView(
                            item = centerItem,
                            sizeDp = 78,
                            onClick = { showDishPickerSlot = "center" }
                        )

                        // 2. Katori 1: Top (Dal)
                        Box(modifier = Modifier.offset(x = 0.dp, y = (-92).dp)) {
                            KatoriBowlView(
                                item = katori1Item,
                                sizeDp = 64,
                                onClick = { showDishPickerSlot = "katori1" }
                            )
                        }

                        // 3. Katori 2: Top-Right (Sabzi / Curry)
                        Box(modifier = Modifier.offset(x = 88.dp, y = (-28).dp)) {
                            KatoriBowlView(
                                item = katori2Item,
                                sizeDp = 64,
                                onClick = { showDishPickerSlot = "katori2" }
                            )
                        }

                        // 4. Katori 3: Bottom-Right (Curd / Dahi)
                        Box(modifier = Modifier.offset(x = 54.dp, y = 74.dp)) {
                            KatoriBowlView(
                                item = katori3Item,
                                sizeDp = 64,
                                onClick = { showDishPickerSlot = "katori3" }
                            )
                        }

                        // 5. Katori 4: Bottom-Left (Salad & Pickle)
                        Box(modifier = Modifier.offset(x = (-54).dp, y = 74.dp)) {
                            KatoriBowlView(
                                item = katori4Item,
                                sizeDp = 64,
                                onClick = { showDishPickerSlot = "katori4" }
                            )
                        }

                        // 6. Katori 5: Top-Left (Roti / Chapati)
                        Box(modifier = Modifier.offset(x = (-88).dp, y = (-28).dp)) {
                            KatoriBowlView(
                                item = katori5Item,
                                sizeDp = 64,
                                onClick = { showDishPickerSlot = "katori5" }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Total Thali Nutrition Breakdown
                    Surface(
                        color = SurfaceLightDark,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "Total Thali Nutrition", color = TextSecondary, fontSize = 11.sp)
                                Text(
                                    text = "$totalThaliCalories kcal",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "${totalProtein}g", color = EmeraldGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(text = "Protein", color = TextSecondary, fontSize = 10.sp)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "${totalCarbs}g", color = AmberFire, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(text = "Carbs", color = TextSecondary, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // ==========================================
            // OTHER MEAL SLOTS: (Breakfast, Snacks, Dinner, Late Night)
            // ==========================================
            val slotMeals = getCuratedMealsForSlot(selectedMealSlot, isVeg, weightGoal)

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                slotMeals.forEach { meal ->
                    DietMealCard(meal = meal)
                }

                // If Late Night Cravings, display Sleep & Recovery suggestion card with Sleeping Mascot!
                if (selectedMealSlot == "latenight") {
                    SleepRecoveryBanner()
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 6. Custom Food Item Creator Button
        Surface(
            color = SurfaceDark,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Customize Your Indian Diet",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Add custom Daal, Chawal, Roti, Pickle, etc.",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
                Button(
                    onClick = { showCustomDishDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("+ Add Food", color = BackgroundDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        UniversalDonateRow(onClick = onDonateClick)
        Spacer(modifier = Modifier.height(10.dp))
        UniversalFooterCredit(onInstagramClick = onInstagramClick)
    }

    // Dish Picker Dialog
    if (showDishPickerSlot != null) {
        val slot = showDishPickerSlot!!
        DishPickerDialog(
            slot = slot,
            isVeg = isVeg,
            onDismiss = { showDishPickerSlot = null },
            onSelect = { newItem ->
                when (slot) {
                    "center" -> centerItem = newItem
                    "katori1" -> katori1Item = newItem
                    "katori2" -> katori2Item = newItem
                    "katori3" -> katori3Item = newItem
                    "katori4" -> katori4Item = newItem
                    "katori5" -> katori5Item = newItem
                }
                showDishPickerSlot = null
            }
        )
    }

    // Custom Dish Creator Dialog
    if (showCustomDishDialog) {
        CustomDishDialog(
            defaultSlot = selectedMealSlot,
            onDismiss = { showCustomDishDialog = false },
            onAdd = { newDish ->
                customMeals = customMeals + newDish
                showCustomDishDialog = false
            }
        )
    }
}

/* =====================================================================
   CIRCULAR KATORI (BOWL) COMPOSABLE
   ===================================================================== */

@Composable
fun KatoriBowlView(
    item: KatoriItem,
    sizeDp: Int,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(sizeDp.dp)
            .shadow(6.dp, CircleShape)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(Color(0xFF334155), Color(0xFF1E293B))
                )
            )
            .border(2.dp, Color(0xFF94A3B8), CircleShape)
            .clickable { onClick() }
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = item.emoji, fontSize = if (sizeDp > 70) 22.sp else 16.sp)
            Text(
                text = item.name,
                color = Color.White,
                fontSize = if (sizeDp > 70) 9.sp else 8.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
            Text(
                text = "${item.calories} kcal",
                color = EmeraldGreen,
                fontSize = if (sizeDp > 70) 8.sp else 7.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/* =====================================================================
   SLEEP & RECOVERY BANNER WITH SLEEPING MASCOT
   ===================================================================== */

@Composable
fun SleepRecoveryBanner() {
    Surface(
        color = SurfaceDark,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, RoyalPurple.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.mascot_sleeping),
                contentDescription = "Sleeping Mascot",
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Sleep & Muscle Recovery",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "🌙", fontSize = 13.sp)
                }
                Text(
                    text = "Target: 7.5 – 8.5 Hours. Deep sleep allows walking muscles to repair, joints to recharge, and lowers cortisol for steady fat burn.",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tip: Warm Turmeric Milk (Haldi Doodh) & zero blue light 1 hr before bed.",
                    color = EmeraldGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/* =====================================================================
   DIET MEAL CARD COMPOSABLE
   ===================================================================== */

@Composable
fun DietMealCard(meal: DietMeal) {
    var logged by remember { mutableStateOf(false) }

    Surface(
        color = SurfaceDark,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    color = SurfaceLightDark,
                    shape = CircleShape,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = meal.emoji, fontSize = 22.sp)
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = meal.name,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        if (meal.isWeightLossRecommended) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = EmeraldGreen.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "Deficit",
                                    color = EmeraldGreen,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        } else if (meal.isWeightGainRecommended) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = AmberFire.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "Surplus",
                                    color = AmberFire,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = meal.description,
                        color = TextSecondary,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${meal.calories} kcal · ${meal.proteinG}g Protein · ${meal.carbsG}g Carbs",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            }

            IconButton(
                onClick = { logged = !logged }
            ) {
                Icon(
                    imageVector = if (logged) Icons.Default.CheckCircle else Icons.Default.AddCircleOutline,
                    contentDescription = "Log meal",
                    tint = if (logged) EmeraldGreen else TextSecondary
                )
            }
        }
    }
}

/* =====================================================================
   DISH PICKER DIALOG (SWAP ANY THALI KATORI IN REAL TIME)
   ===================================================================== */

@Composable
fun DishPickerDialog(
    slot: String,
    isVeg: Boolean,
    onDismiss: () -> Unit,
    onSelect: (KatoriItem) -> Unit
) {
    val options = getAvailableDishesForSlot(slot, isVeg)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Column {
                Text(
                    text = "Swap ${getSlotTitle(slot)}",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "Pick an authentic dish or portion",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                options.forEach { dish ->
                    Surface(
                        color = SurfaceLightDark,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(dish) }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = dish.emoji, fontSize = 22.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(text = dish.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(text = dish.description, color = TextSecondary, fontSize = 10.sp)
                                    Text(text = "${dish.proteinG}g Protein · ${dish.carbsG}g Carbs", color = TextMuted, fontSize = 9.sp)
                                }
                            }
                            Text(
                                text = "${dish.calories} kcal",
                                color = EmeraldGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

/* =====================================================================
   CUSTOM DISH CREATOR DIALOG
   ===================================================================== */

@Composable
fun CustomDishDialog(
    defaultSlot: String,
    onDismiss: () -> Unit,
    onAdd: (DietMeal) -> Unit
) {
    var dishName by remember { mutableStateOf("") }
    var caloriesText by remember { mutableStateOf("") }
    var selectedEmoji by remember { mutableStateOf("🥣") }
    var mealSlot by remember { mutableStateOf(defaultSlot) }

    val emojis = listOf("🫓", "🍚", "🥣", "🍛", "🥬", "🧀", "🥛", "🍳", "🍗", "🍋", "🍵", "☕", "🍎", "🍿")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Text(
                text = "Add Custom Food / Meal",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = dishName,
                    onValueChange = { dishName = it },
                    label = { Text("Food Name", color = TextSecondary) },
                    placeholder = { Text("e.g. Daal Chawal, Roti & Pickle", color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldGreen,
                        unfocusedBorderColor = SurfaceLightDark,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = caloriesText,
                    onValueChange = { caloriesText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Estimated Calories (kcal)", color = TextSecondary) },
                    placeholder = { Text("e.g. 250", color = TextMuted) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldGreen,
                        unfocusedBorderColor = SurfaceLightDark,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Select Food Clipart / Emoji:",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    emojis.forEach { emoji ->
                        val isSelected = selectedEmoji == emoji
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) EmeraldGreen.copy(alpha = 0.25f) else SurfaceLightDark)
                                .border(
                                    width = if (isSelected) 2.dp else 0.dp,
                                    color = if (isSelected) EmeraldGreen else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedEmoji = emoji }
                        ) {
                            Text(text = emoji, fontSize = 20.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (dishName.isNotBlank()) {
                        val cal = caloriesText.toIntOrNull() ?: 200
                        onAdd(
                            DietMeal(
                                id = System.currentTimeMillis().toString(),
                                name = dishName,
                                emoji = selectedEmoji,
                                calories = cal,
                                slot = mealSlot,
                                description = "Custom logged food",
                                proteinG = 8,
                                carbsG = 30,
                                fatG = 5
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
            ) {
                Text("Add to Diet", color = BackgroundDark, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

/* =====================================================================
   TAB 3: HYDRATION TRACKER
   ===================================================================== */

@Composable
fun HydrationTab(
    waterConsumedMl: Int,
    waterTargetMl: Int,
    onAddGlass: () -> Unit,
    onAddBottle: () -> Unit,
    onDonateClick: () -> Unit,
    onInstagramClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val progress = (waterConsumedMl.toFloat() / waterTargetMl.coerceAtLeast(1)).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Mascot Cycling Banner
        Surface(
            color = SurfaceDark,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.mascot_cycling),
                    contentDescription = "Cycling Mascot",
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(14.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Stay Hydrated!",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Target: 2,500 ml / day. Keeps your joints lubricated and calorie burn high!",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Circular Hydration Ring
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(220.dp)
        ) {
            Canvas(modifier = Modifier.size(200.dp)) {
                drawCircle(
                    color = TrackBackground,
                    style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                )
                drawArc(
                    brush = Brush.sweepGradient(listOf(OceanBlue, Color(0xFF38BDF8), OceanBlue)),
                    startAngle = -90f,
                    sweepAngle = progress * 360f,
                    useCenter = false,
                    style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = String.format(Locale.getDefault(), "%,d", waterConsumedMl),
                    color = TextPrimary,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "/ $waterTargetMl ml",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onAddGlass,
                colors = ButtonDefaults.buttonColors(containerColor = OceanBlue),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Text("💧 +250ml Glass", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
            Button(
                onClick = onAddBottle,
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, OceanBlue),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Text("🍼 +500ml Bottle", color = OceanBlue, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Reminder Interval Badge
        Surface(
            color = SurfaceDark,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Water Reminder Alarms", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(text = "Periodic alerts to keep you energized", color = TextSecondary, fontSize = 10.sp)
                }
                Surface(
                    color = OceanBlue.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Every 60 min",
                        color = OceanBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        UniversalDonateRow(onClick = onDonateClick)
        Spacer(modifier = Modifier.height(10.dp))
        UniversalFooterCredit(onInstagramClick = onInstagramClick)
    }
}

/* =====================================================================
   TAB 4: MORE / SETTINGS / FULL DONATE CARD (Brief 2c)
   ===================================================================== */

@Composable
fun SettingsTab(
    dailyGoal: Int,
    onEditGoalClick: () -> Unit,
    onDonateClick: () -> Unit,
    onCopyUpiClick: () -> Unit,
    onInstagramClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Daily Goal Setting Card
        Surface(
            color = SurfaceDark,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Daily Step Goal",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = String.format(Locale.getDefault(), "%,d steps / day", dailyGoal),
                        color = EmeraldGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Button(
                    onClick = onEditGoalClick,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text("Change", color = BackgroundDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Health Profile Card
        Surface(
            color = SurfaceDark,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Personal Health Profile",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Weight", color = TextSecondary, fontSize = 11.sp)
                        Text(text = "68.0 kg", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Column {
                        Text(text = "Height", color = TextSecondary, fontSize = 11.sp)
                        Text(text = "172.0 cm", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Column {
                        Text(text = "BMI", color = TextSecondary, fontSize = 11.sp)
                        Text(text = "23.0 (Normal)", color = EmeraldGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Sleep & Recovery Information Card
        SleepRecoveryBanner()

        Spacer(modifier = Modifier.height(16.dp))

        // FULL DONATE CARD (Brief 2c: exactly ordered)
        Surface(
            color = SurfaceDark,
            shape = RoundedCornerShape(22.dp),
            border = BorderStroke(1.dp, RosePink.copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Heading with heart icon
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Donate to the developer",
                        color = TextPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = RosePink,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Illustration (art-donate mascot holding heart)
                Image(
                    painter = painterResource(id = R.drawable.mascot_heart),
                    contentDescription = "Donate Illustration",
                    modifier = Modifier
                        .size(width = 160.dp, height = 90.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 3. QR Code (shown at 240x240, clickable)
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .size(240.dp)
                        .clickable { onDonateClick() }
                        .padding(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.donate_qr),
                        contentDescription = "Tap to pay with any UPI app",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 4. Scan to pay text
                Text(
                    text = "Scan to pay with any UPI app · or tap the code to pay.",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 5. "Click to pay" Button
                Button(
                    onClick = onDonateClick,
                    colors = ButtonDefaults.buttonColors(containerColor = RosePink),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Text(
                        text = "Click to pay",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 6. Row: UPI ID + Copy button
                Surface(
                    color = SurfaceLightDark,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "UPI ID: jaynishad2010@okhdfcbank",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                        Button(
                            onClick = onCopyUpiClick,
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("Copy", color = BackgroundDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 7. Text: Your donation helps...
                Text(
                    text = "Your donation helps keep Healbuds running and ad-free.",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Universal Footer Credit (Brief 2d)
        UniversalFooterCredit(onInstagramClick = onInstagramClick)
    }
}

/* =====================================================================
   DONATE FALLBACK SHEET (Brief Section 3)
   ===================================================================== */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonateFallbackModalSheet(
    onDismiss: () -> Unit,
    onCopyUpiClick: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.mascot_heart),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(14.dp))
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Donate to the developer",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Pay any amount you like, from any UPI app. Your donation helps keep Healbuds running and ad-free.",
                color = TextSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                color = BackgroundDark,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "UPI ID: jaynishad2010@okhdfcbank",
                    color = EmeraldGreen,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onCopyUpiClick,
                colors = ButtonDefaults.buttonColors(containerColor = RosePink),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Text("Copy UPI ID", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/* =====================================================================
   METRIC CARD COMPOSABLE
   ===================================================================== */

@Composable
fun MetricCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconTint: Color,
    value: String,
    unit: String,
    label: String
) {
    Surface(
        color = SurfaceDark,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = unit,
                    color = TextSecondary,
                    fontSize = 10.sp
                )
            }
            Text(
                text = label,
                color = TextSecondary,
                fontSize = 10.sp
            )
        }
    }
}

/* =====================================================================
   UPGRADED GOAL DIALOG (THOUSANDS CHIPS & ANY CUSTOM NUMBER HONESTLY)
   ===================================================================== */

@Composable
fun GoalDialog(
    currentGoal: Int,
    onDismiss: () -> Unit,
    onSelect: (Int) -> Unit
) {
    var customGoalText by remember { mutableStateOf(currentGoal.toString()) }
    var selectedGoal by remember { mutableIntStateOf(currentGoal) }
    val thousandGoals = listOf(5000, 6000, 7000, 8000, 9000, 10000, 11000, 12000, 13000, 14000, 15000)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Column {
                Text(
                    text = "🎯 Set Daily Step Goal",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = "Pick in thousands or enter any custom target",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Custom Number Input Field (Allows typing ANY number honestly)
                OutlinedTextField(
                    value = customGoalText,
                    onValueChange = { input ->
                        val filtered = input.filter { it.isDigit() }
                        customGoalText = filtered
                        filtered.toIntOrNull()?.let {
                            if (it > 0) selectedGoal = it
                        }
                    },
                    label = { Text("Custom Goal (steps)", color = TextSecondary) },
                    placeholder = { Text("e.g. 7,420 or 10,500", color = TextMuted) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldGreen,
                        unfocusedBorderColor = SurfaceLightDark,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        Text(
                            text = "steps",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                    }
                )

                // Quick Increment / Decrement Steppers (-500 / +500)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val newG = (selectedGoal - 500).coerceAtLeast(1000)
                            selectedGoal = newG
                            customGoalText = newG.toString()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceLightDark),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("-500 steps", color = TextPrimary, fontSize = 12.sp)
                    }
                    Button(
                        onClick = {
                            val newG = (selectedGoal + 500).coerceAtMost(50000)
                            selectedGoal = newG
                            customGoalText = newG.toString()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceLightDark),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("+500 steps", color = TextPrimary, fontSize = 12.sp)
                    }
                }

                HorizontalDivider(color = SurfaceLightDark)

                Text(
                    text = "Quick Choose in Thousands:",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                // Grid / Wrap of thousands (5000, 6000, 7000, 8000, 9000, 10000, 11000, 12000, 13000, 14000, 15000)
                val rows = thousandGoals.chunked(3)
                rows.forEach { rowGoals ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        rowGoals.forEach { g ->
                            val isSelected = selectedGoal == g
                            Surface(
                                color = if (isSelected) EmeraldGreen else SurfaceLightDark,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        selectedGoal = g
                                        customGoalText = g.toString()
                                    }
                            ) {
                                Text(
                                    text = String.format(Locale.getDefault(), "%,d", g),
                                    color = if (isSelected) BackgroundDark else TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalGoal = customGoalText.toIntOrNull() ?: selectedGoal
                    onSelect(finalGoal)
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Save Goal", color = BackgroundDark, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

/* =====================================================================
   HELPER FUNCTIONS FOR CURATED AUTHENTIC INDIAN FOOD DATA
   ===================================================================== */

fun getSlotTitle(slot: String): String = when (slot) {
    "center" -> "Grains / Rice"
    "katori1" -> "Dal / Lentils"
    "katori2" -> "Sabzi / Curry"
    "katori3" -> "Curd / Raita"
    "katori4" -> "Salad & Achar"
    "katori5" -> "Roti / Bread"
    else -> "Dish"
}

fun getAvailableDishesForSlot(slot: String, isVeg: Boolean): List<KatoriItem> = when (slot) {
    "center" -> listOf(
        KatoriItem("center", "Grains", "Basmati Rice", "🍚", 210, "Steamed fragrant white rice", 4, 45, 1),
        KatoriItem("center", "Grains", "Brown Rice", "🌾", 190, "Whole grain fiber rice", 5, 42, 2),
        KatoriItem("center", "Grains", "Moong Khichdi", "🍲", 180, "Gentle comfort lentil & rice", 7, 35, 2),
        KatoriItem("center", "Grains", "Vegetable Pulao", "🍛", 230, "Spiced rice with garden peas", 5, 48, 3),
        KatoriItem("center", "Grains", "Jeera Rice", "🍚", 200, "Cumin tempered basmati", 4, 44, 2)
    )
    "katori1" -> listOf(
        KatoriItem("katori1", "Dal", "Dal Tadka", "🥣", 180, "Yellow lentils with cumin & garlic", 11, 24, 5),
        KatoriItem("katori1", "Dal", "Moong Dal (Yellow)", "🥣", 140, "Light & soothing easy digest", 9, 20, 3),
        KatoriItem("katori1", "Dal", "Dal Makhani", "🍲", 260, "Rich slow-cooked black lentils", 12, 28, 12),
        KatoriItem("katori1", "Dal", "South Sambhar", "🥣", 130, "Tangy tamarind spiced stew", 6, 22, 2),
        KatoriItem("katori1", "Dal", "Chana Dal", "🥣", 190, "Hearty Bengal gram lentils", 13, 26, 4)
    )
    "katori2" -> if (isVeg) {
        listOf(
            KatoriItem("katori2", "Sabzi", "Rajma Masala", "🍛", 240, "Red kidney beans in gravy", 13, 38, 4),
            KatoriItem("katori2", "Sabzi", "Palak Paneer", "🧀", 230, "Cottage cheese in spinach gravy", 14, 10, 15),
            KatoriItem("katori2", "Sabzi", "Chole Masala", "🍛", 250, "Chickpeas in roasted spices", 12, 36, 6),
            KatoriItem("katori2", "Sabzi", "Aloo Bhindi Masala", "🥔", 140, "Okra and potato dry roast", 3, 18, 6),
            KatoriItem("katori2", "Sabzi", "Matar Paneer", "🧀", 240, "Green peas and fresh paneer", 12, 16, 14)
        )
    } else {
        listOf(
            KatoriItem("katori2", "Sabzi", "Chicken Curry", "🍗", 280, "Lean chicken in aromatic gravy", 28, 8, 14),
            KatoriItem("katori2", "Sabzi", "Egg Curry (2 Eggs)", "🥚", 210, "Boiled eggs in onion gravy", 14, 9, 13),
            KatoriItem("katori2", "Sabzi", "Fish Masala", "🐟", 220, "Fresh fish in turmeric curry", 24, 6, 11),
            KatoriItem("katori2", "Sabzi", "Mutton Rogan Josh", "🍖", 340, "Rich slow-braised mutton", 26, 6, 24)
        )
    }
    "katori3" -> listOf(
        KatoriItem("katori3", "Curd", "Curd / Dahi", "🥛", 100, "Fresh probiotic curd", 6, 8, 5),
        KatoriItem("katori3", "Curd", "Cucumber Mint Raita", "🥒", 80, "Cooling cucumber yogurt", 4, 8, 3),
        KatoriItem("katori3", "Curd", "Spiced Chaas", "🥤", 50, "Digestive buttermilk with roasted jeera", 3, 5, 2),
        KatoriItem("katori3", "Curd", "Boondi Raita", "🥣", 130, "Crispy gram pearls in spiced curd", 5, 14, 6)
    )
    "katori4" -> listOf(
        KatoriItem("katori4", "Salad", "Salad & Achar", "🥗", 35, "Fresh cucumber, tomato & lemon pickle", 1, 7, 0),
        KatoriItem("katori4", "Salad", "Sprouted Moong Salad", "🌱", 75, "High protein sprouted crunch", 6, 12, 1),
        KatoriItem("katori4", "Salad", "Roasted Papad & Achar", "🍋", 55, "Crispy papadum with mango pickle", 2, 9, 1),
        KatoriItem("katori4", "Salad", "Beetroot Kachumber", "🥕", 40, "Grated beetroot with lime juice", 1, 9, 0)
    )
    "katori5" -> listOf(
        KatoriItem("katori5", "Roti", "2x Roti / Phulka", "🫓", 160, "Whole wheat flame puffed", 6, 32, 1),
        KatoriItem("katori5", "Roti", "2x Multigrain Phulka", "🫓", 150, "Ragi, jowar & wheat blend", 7, 28, 2),
        KatoriItem("katori5", "Roti", "1x Paratha with Ghee", "🫓", 230, "Layered flatbread with pure desi ghee", 5, 30, 10),
        KatoriItem("katori5", "Roti", "2x Missi Roti", "🫓", 180, "Gram flour bread with fenugreek", 8, 28, 4)
    )
    else -> emptyList()
}

fun getCuratedMealsForSlot(slot: String, isVeg: Boolean, weightGoal: String): List<DietMeal> = when (slot) {
    "breakfast" -> listOf(
        DietMeal("b1", "Vegetable Poha", "🥣", 180, "breakfast", "Flattened rice tempered with mustard, peanuts & curry leaves", 4, 34, 4, isWeightLossRecommended = true),
        DietMeal("b2", "Besan Chilla (2 pcs)", "🥞", 210, "breakfast", "Savory gram flour pancakes with chopped veggies", 10, 26, 6, isWeightLossRecommended = true),
        DietMeal("b3", "Idli Sambhar (2 pcs)", "🫓", 190, "breakfast", "Fermented steamed rice cakes with vegetable lentil stew", 6, 38, 2, isWeightLossRecommended = true),
        DietMeal("b4", "2 Boiled Eggs & Toast", "🍳", 220, "breakfast", "Complete morning protein with 1 slice whole wheat", 14, 18, 9, isWeightLossRecommended = true),
        DietMeal("b5", "Peanut Butter Banana Toast", "🍌", 310, "breakfast", "Calorie dense energy booster for muscle growth", 11, 42, 12, isWeightGainRecommended = true),
        DietMeal("b6", "Paneer Paratha with Curd", "🫓", 380, "breakfast", "Cottage cheese stuffed flatbread with fresh curd", 16, 44, 16, isWeightGainRecommended = true)
    )
    "snacks" -> listOf(
        DietMeal("s1", "Roasted Makhana (Foxnuts)", "🍿", 110, "snacks", "Low GI crunchy lotus seeds roasted in light ghee", 3, 20, 2, isWeightLossRecommended = true),
        DietMeal("s2", "Green Tea & Roasted Chana", "🍵", 120, "snacks", "Antioxidant boost with roasted black chickpeas", 7, 18, 2, isWeightLossRecommended = true),
        DietMeal("s3", "Steamed Khaman Dhokla", "🥟", 150, "snacks", "Light spongy fermented snack with mustard tempering", 5, 24, 4, isWeightLossRecommended = true),
        DietMeal("s4", "Sprout Chaat with Pomegranate", "🥗", 140, "snacks", "Tangy sprouted pulses with chaat masala & lime", 8, 22, 2, isWeightLossRecommended = true),
        DietMeal("s5", "Almonds, Walnuts & Dates", "🥜", 240, "snacks", "Healthy fats, magnesium & natural sweetness", 6, 26, 14, isWeightGainRecommended = true)
    )
    "dinner" -> listOf(
        DietMeal("d1", "Moong Dal Khichdi & Dahi", "🍲", 260, "dinner", "Comforting digestible dinner, gentle on bedtime digestion", 9, 44, 4, isWeightLossRecommended = true),
        DietMeal("d2", "2 Phulka & Lauki Sabzi", "🫓", 240, "dinner", "Hydrating bottle gourd curry with dry roasted wheat breads", 6, 46, 3, isWeightLossRecommended = true),
        DietMeal("d3", "Paneer Bhurji with 2 Rotis", "🧀", 380, "dinner", "Crumbled spiced paneer with whole wheat breads", 18, 36, 16, isWeightGainRecommended = true),
        DietMeal("d4", "Grilled Chicken Salad", "🍗", 320, "dinner", "Tender chicken breast with cucumber, lettuce & lime", 28, 8, 12, isWeightLossRecommended = true),
        DietMeal("d5", "Vegetable Dalia Porridge", "🥣", 220, "dinner", "Broken wheat slow cooked with carrots & peas", 7, 40, 3, isWeightLossRecommended = true)
    )
    "latenight" -> listOf(
        DietMeal("l1", "Warm Haldi Doodh (Turmeric Milk)", "🥛", 120, "latenight", "Promotes melatonin release, joint repair & restful sleep", 4, 12, 5, isWeightLossRecommended = true, isWeightGainRecommended = true),
        DietMeal("l2", "Soaked Almonds & 2 Walnuts", "🌰", 110, "latenight", "High magnesium healthy fats to stop midnight hunger pangs", 4, 3, 10, isWeightLossRecommended = true),
        DietMeal("l3", "Chamomile or Herbal Tea", "🍵", 0, "latenight", "Zero calories, completely caffeine-free calming brew", 0, 0, 0, isWeightLossRecommended = true),
        DietMeal("l4", "Light Roasted Salted Makhana", "🍿", 80, "latenight", "Satisfies crunch cravings without spiking insulin", 2, 14, 1, isWeightLossRecommended = true),
        DietMeal("l5", "Sliced Apple with Cinnamon", "🍎", 70, "latenight", "Natural sweet fiber crunch", 0, 18, 0, isWeightLossRecommended = true)
    )
    else -> emptyList()
}
