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
    val bowlLabel: String,    // "Grains", "Dal", "Sabzi", "Curd", "Salad", "Roti" / Beverage
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
    val fatG: Int
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
    waterReminderIntervalHours: Int,
    streakDays: Int,
    restDaysUsed: Int,
    weightKg: Float,
    heightCm: Float,
    weightGoal: String,
    onUpdateGoal: (Int) -> Unit,
    onLogWater: (Int) -> Unit,
    onResetWater: () -> Unit,
    onUpdateHealthProfile: (Float, Float, String) -> Unit,
    onUpdateWaterReminderInterval: (Int) -> Unit,
    onTriggerTestWaterReminder: () -> Unit,
    onDonateClick: () -> Unit,
    onCopyUpiClick: () -> Unit,
    onInstagramClick: () -> Unit,
    showFallbackSheet: Boolean,
    onDismissFallbackSheet: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showGoalDialog by remember { mutableStateOf(false) }
    var showHealthProfileDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            HealbudsHeader(
                onDonateClick = onDonateClick,
                onEditGoalClick = { showGoalDialog = true }
            )
        },
        bottomBar = {
            HealbudsBottomNav(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> TodayStepsTab(
                    currentSteps = currentSteps,
                    dailyGoal = dailyGoal,
                    distanceKm = distanceKm,
                    calories = calories,
                    activeMinutes = activeMinutes,
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
                    weightKg = weightKg,
                    heightCm = heightCm,
                    weightGoal = weightGoal,
                    onEditHealthProfile = { showHealthProfileDialog = true },
                    onDonateClick = onDonateClick,
                    onInstagramClick = onInstagramClick
                )
                3 -> HydrationTab(
                    waterConsumedMl = waterConsumedMl,
                    waterTargetMl = waterTargetMl,
                    reminderIntervalHours = waterReminderIntervalHours,
                    onAddGlass = { onLogWater(250) },
                    onAddBottle = { onLogWater(500) },
                    onResetWater = onResetWater,
                    onSelectReminderInterval = onUpdateWaterReminderInterval,
                    onTriggerTestReminder = onTriggerTestWaterReminder,
                    onDonateClick = onDonateClick,
                    onInstagramClick = onInstagramClick
                )
                4 -> SettingsTab(
                    dailyGoal = dailyGoal,
                    weightKg = weightKg,
                    heightCm = heightCm,
                    weightGoal = weightGoal,
                    waterReminderIntervalHours = waterReminderIntervalHours,
                    onEditGoalClick = { showGoalDialog = true },
                    onEditHealthProfile = { showHealthProfileDialog = true },
                    onDonateClick = onDonateClick,
                    onCopyUpiClick = onCopyUpiClick,
                    onInstagramClick = onInstagramClick
                )
            }
        }
    }

    if (showGoalDialog) {
        GoalDialog(
            currentGoal = dailyGoal,
            onDismiss = { showGoalDialog = false },
            onSelect = { newGoal ->
                onUpdateGoal(newGoal)
                showGoalDialog = false
            }
        )
    }

    if (showHealthProfileDialog) {
        HealthProfileDialog(
            currentWeightKg = weightKg,
            currentHeightCm = heightCm,
            currentGoal = weightGoal,
            onDismiss = { showHealthProfileDialog = false },
            onSave = { newWeight, newHeight, newGoal ->
                onUpdateHealthProfile(newWeight, newHeight, newGoal)
                showHealthProfileDialog = false
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

/* =====================================================================
   HEADER & NAVIGATION
   ===================================================================== */

@Composable
fun HealbudsHeader(
    onDonateClick: () -> Unit,
    onEditGoalClick: () -> Unit
) {
    Surface(
        color = BackgroundDark,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(EmeraldGreen, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Healbuds",
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Surface(
                        color = EmeraldGreen.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "On your device",
                            color = EmeraldGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Header heart button (Brief 2a)
                    IconButton(
                        onClick = onDonateClick,
                        modifier = Modifier
                            .size(38.dp)
                            .background(RosePink.copy(alpha = 0.15f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Donate to the developer",
                            tint = RosePink,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onEditGoalClick,
                        modifier = Modifier
                            .size(38.dp)
                            .background(SurfaceDark, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Set Daily Step Goal",
                            tint = EmeraldGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Track steps, authentic Indian thali diet & hydration",
                color = TextSecondary,
                fontSize = 12.sp
            )
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
        val tabs = listOf(
            Triple(0, "Steps", Icons.Default.DirectionsWalk),
            Triple(1, "Streaks", Icons.Default.LocalFireDepartment),
            Triple(2, "Diet & Thali", Icons.Default.Restaurant),
            Triple(3, "Water", Icons.Default.WaterDrop),
            Triple(4, "More", Icons.Default.Settings)
        )

        tabs.forEach { (index, title, icon) ->
            NavigationBarItem(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = title,
                        fontSize = 11.sp,
                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = EmeraldGreen,
                    selectedTextColor = EmeraldGreen,
                    indicatorColor = EmeraldGreen.copy(alpha = 0.18f),
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
                )
            )
        }
    }
}

/* =====================================================================
   UNIVERSAL COMPONENTS (Brief 2b & 2d)
   ===================================================================== */

@Composable
fun UniversalDonateRow(onClick: () -> Unit) {
    Surface(
        color = SurfaceDark,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, RosePink.copy(alpha = 0.35f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.mascot_heart),
                contentDescription = "Donate Illustration",
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Donate to the developer",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Pay any amount · helps keep Healbuds running and ad-free",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Donate Heart",
                tint = RosePink,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun UniversalFooterCredit(onInstagramClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
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
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Made with love ♥ by Vijay Mamta Sahani (",
                color = TextSecondary,
                fontSize = 12.sp
            )
            Text(
                text = "@momentswithvms",
                color = RosePink,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(onClick = onInstagramClick)
            )
            Text(
                text = ")",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
    }
}

/* =====================================================================
   TAB 0: STEPS (TODAY) — ZERO DUMMY DATA, GENEROUS BREATHING ROOM
   ===================================================================== */

@Composable
fun TodayStepsTab(
    currentSteps: Int,
    dailyGoal: Int,
    distanceKm: Double,
    calories: Double,
    activeMinutes: Int,
    onDonateClick: () -> Unit,
    onInstagramClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val progress = (currentSteps.toFloat() / dailyGoal.coerceAtLeast(1)).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 800),
        label = "StepProgress"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        // Large Circular Step Progress Ring (275dp diameter, 14dp stroke, ample clearance)
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(275.dp)
                .padding(4.dp)
        ) {
            Canvas(modifier = Modifier.size(252.dp)) {
                // Background Track
                drawCircle(
                    color = TrackBackground,
                    style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
                )
                // Active Sweep Arc
                drawArc(
                    brush = Brush.sweepGradient(listOf(EmeraldGreen, EmeraldLight, EmeraldGreen)),
                    startAngle = -90f,
                    sweepAngle = animatedProgress * 360f,
                    useCenter = false,
                    style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // Interior elements spaced generously to never touch or crowd the ring
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 26.dp, vertical = 12.dp)
            ) {
                Text(text = "👟", fontSize = 22.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = String.format(Locale.getDefault(), "%,d", currentSteps),
                    color = TextPrimary,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 36.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = String.format(Locale.getDefault(), "Daily Goal: %,d steps", dailyGoal),
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(10.dp))
                // Percentage completed pill with ample breathing clearance
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

        Spacer(modifier = Modifier.height(16.dp))

        // VMS Buddy Mascot Motivational Card
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
                    painter = painterResource(id = R.drawable.mascot_walking),
                    contentDescription = "VMS Buddy Walking Mascot",
                    modifier = Modifier
                        .size(58.dp)
                        .clip(RoundedCornerShape(14.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "VMS Buddy 👟",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (currentSteps >= dailyGoal) "Goal Achieved!" else "Active Mascot",
                            color = EmeraldGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    val remaining = (dailyGoal - currentSteps).coerceAtLeast(0)
                    val motivationalQuote = if (currentSteps >= dailyGoal) {
                        "Outstanding! You crushed your daily goal of %,d steps! Rest up or keep going!".format(dailyGoal)
                    } else if (currentSteps == 0) {
                        "\"0 steps crushed! Only %,d more to hit our daily goal. Let's walk together!\"".format(dailyGoal)
                    } else {
                        "\"%,d steps crushed! Only %,d more to hit our daily goal. Let's walk together!\"".format(currentSteps, remaining)
                    }
                    Text(
                        text = motivationalQuote,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3 Key Health Metrics Row (Distance, Calories, Active Minutes)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                icon = Icons.Default.Place,
                iconColor = OceanBlue,
                value = String.format(Locale.getDefault(), "%.2f", distanceKm),
                unit = "km",
                label = "Distance",
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                icon = Icons.Default.LocalFireDepartment,
                iconColor = AmberFire,
                value = String.format(Locale.getDefault(), "%.0f", calories),
                unit = "kcal",
                label = "Calories",
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                icon = Icons.Default.Schedule,
                iconColor = PurpleMetric,
                value = activeMinutes.toString(),
                unit = "min",
                label = "Active",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Universal Donate Row (Brief 2b)
        UniversalDonateRow(onClick = onDonateClick)

        Spacer(modifier = Modifier.height(10.dp))

        // Universal Footer Credit (Brief 2d)
        UniversalFooterCredit(onInstagramClick = onInstagramClick)
    }
}

/* =====================================================================
   TAB 1: STREAKS & SMART REST DAYS CALENDAR
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
        Spacer(modifier = Modifier.height(14.dp))

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
                        text = "If inactive for 3 consecutive days without rest credit, streak resets to Day 1.",
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
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "November 2026",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "$streakDays Active Days",
                        color = EmeraldGreen,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Day headers (Mon - Sun)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (day in listOf("M", "T", "W", "T", "F", "S", "S")) {
                        Text(
                            text = day,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = SurfaceLightDark)
                Spacer(modifier = Modifier.height(10.dp))

                // 6 Weeks of November 2026, starting Monday (Nov 1 is Sunday)
                val weeks = listOf(
                    listOf(Pair(26, false), Pair(27, false), Pair(28, false), Pair(29, false), Pair(30, false), Pair(31, false), Pair(1, false)),
                    listOf(Pair(2, false), Pair(3, false), Pair(4, false), Pair(5, false), Pair(6, false), Pair(7, false), Pair(8, false)),
                    listOf(Pair(9, false), Pair(10, false), Pair(11, false), Pair(12, false), Pair(13, false), Pair(14, false), Pair(15, false)),
                    listOf(Pair(16, false), Pair(17, false), Pair(18, false), Pair(19, false), Pair(20, false), Pair(21, false), Pair(22, false)),
                    listOf(Pair(23, false), Pair(24, false), Pair(25, false), Pair(26, false), Pair(27, false), Pair(28, false), Pair(29, false)),
                    listOf(Pair(30, false), Pair(1, false), Pair(2, false), Pair(3, false), Pair(4, false), Pair(5, false), Pair(6, false))
                )

                for (week in weeks) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for ((dayNum, _) in week) {
                            val isToday = (dayNum == 4)
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .weight(1f)
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isToday) EmeraldGreen.copy(alpha = 0.25f) else Color.Transparent)
                                    .border(
                                        width = if (isToday) 2.dp else 0.dp,
                                        color = if (isToday) EmeraldGreen else Color.Transparent,
                                        shape = CircleShape
                                    )
                            ) {
                                Text(
                                    text = dayNum.toString(),
                                    color = if (isToday) EmeraldLight else TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        UniversalDonateRow(onClick = onDonateClick)
        Spacer(modifier = Modifier.height(10.dp))
        UniversalFooterCredit(onInstagramClick = onInstagramClick)
    }
}

/* =====================================================================
   TAB 2: AUTHENTIC INDIAN THALI FOR ALL MEALS & ACTIONABLE DIET PLAN
   ===================================================================== */

@Composable
fun IndianThaliDietTab(
    weightKg: Float,
    heightCm: Float,
    weightGoal: String,
    onEditHealthProfile: () -> Unit,
    onDonateClick: () -> Unit,
    onInstagramClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    var activeGoal by remember { mutableStateOf(weightGoal) }
    var isVeg by remember { mutableStateOf(true) }
    var selectedMealSlot by remember { mutableStateOf("lunch") } // breakfast, lunch, snacks, dinner, latenight

    var showDishPickerSlot by remember { mutableStateOf<String?>(null) }
    var showCustomDishDialog by remember { mutableStateOf(false) }

    // Bowls for Breakfast Thali
    var bfCenter by remember { mutableStateOf(KatoriItem("center", "Grains", "Poha & Peanuts", "🥣", 210, "Light flattened rice with roasted peanuts", 5, 38, 5)) }
    var bfK1 by remember { mutableStateOf(KatoriItem("katori1", "Pulses", "Moong Sambhar", "🍲", 130, "Lentils with drumstick & pumpkin", 7, 20, 2)) }
    var bfK2 by remember { mutableStateOf(KatoriItem("katori2", "Protein", "Paneer Bhurji", "🧀", 180, "Scrambled cottage cheese with herbs", 14, 4, 12)) }
    var bfK3 by remember { mutableStateOf(KatoriItem("katori3", "Chutney", "Mint Chutney", "🌿", 45, "Fresh mint, coriander & lemon", 1, 4, 1)) }
    var bfK4 by remember { mutableStateOf(KatoriItem("katori4", "Salad", "Sprout Chaat", "🥗", 75, "Sprouted moong & pomegranate", 6, 12, 1)) }
    var bfK5 by remember { mutableStateOf(KatoriItem("katori5", "Brew", "Masala Chai", "☕", 55, "Cardamom & ginger morning tea", 2, 8, 2)) }

    // Bowls for Lunch Thali
    var lunchCenter by remember { mutableStateOf(KatoriItem("center", "Grains", "Basmati Rice", "🍚", 210, "Fragrant steamed white rice", 4, 45, 1)) }
    var lunchK1 by remember { mutableStateOf(KatoriItem("katori1", "Dal", "Dal Tadka", "🥣", 180, "Yellow arhar lentils with cumin & garlic", 11, 24, 5)) }
    var lunchK2 by remember { mutableStateOf(KatoriItem("katori2", "Sabzi", "Rajma Masala", "🍛", 240, "Red kidney beans in spiced onion gravy", 13, 38, 4)) }
    var lunchK3 by remember { mutableStateOf(KatoriItem("katori3", "Curd", "Curd / Dahi", "🥛", 100, "Fresh probiotic whole curd", 6, 8, 5)) }
    var lunchK4 by remember { mutableStateOf(KatoriItem("katori4", "Salad", "Salad & Achar", "🥗", 35, "Cucumber, tomato & lemon pickle", 1, 7, 0)) }
    var lunchK5 by remember { mutableStateOf(KatoriItem("katori5", "Roti", "2x Phulka Roti", "🫓", 160, "Whole wheat soft rotis", 6, 32, 1)) }

    // Bowls for Snacks Platter
    var snackCenter by remember { mutableStateOf(KatoriItem("center", "Crunch", "Roasted Makhana", "🍿", 110, "Light foxnuts roasted in olive oil", 3, 20, 2)) }
    var snackK1 by remember { mutableStateOf(KatoriItem("katori1", "Steamed", "Besan Dhokla", "🥮", 140, "Steamed fermented gram flour sponge", 6, 22, 3)) }
    var snackK2 by remember { mutableStateOf(KatoriItem("katori2", "Protein", "Roasted Chana", "🥜", 120, "High fiber dry roasted chickpeas", 8, 18, 3)) }
    var snackK3 by remember { mutableStateOf(KatoriItem("katori3", "Brew", "Ginger Green Tea", "🍵", 10, "Metabolism booster antioxidant brew", 0, 2, 0)) }
    var snackK4 by remember { mutableStateOf(KatoriItem("katori4", "Fruit", "Apple Slices", "🍎", 65, "Rich in natural fiber and pectin", 0, 16, 0)) }
    var snackK5 by remember { mutableStateOf(KatoriItem("katori5", "Nuts", "7x Badam (Almonds)", "🌰", 50, "Healthy fats for brain & vitality", 2, 2, 4)) }

    // Bowls for Dinner Thali
    var dinCenter by remember { mutableStateOf(KatoriItem("center", "Grains", "Moong Khichdi", "🍲", 230, "Easy digestible comforting grains", 8, 40, 4)) }
    var dinK1 by remember { mutableStateOf(KatoriItem("katori1", "Dal", "Yellow Moong Dal", "🥣", 140, "Light gut-friendly lentil soup", 9, 20, 2)) }
    var dinK2 by remember { mutableStateOf(KatoriItem("katori2", "Sabzi", "Lauki Chana Sabzi", "🥒", 130, "Bottle gourd with light chana dal", 6, 18, 3)) }
    var dinK3 by remember { mutableStateOf(KatoriItem("katori3", "Raita", "Cucumber Raita", "🥛", 70, "Cooling digestive probiotic", 4, 6, 3)) }
    var dinK4 by remember { mutableStateOf(KatoriItem("katori4", "Salad", "Beetroot Salad", "🥗", 40, "Nitrate-rich stamina booster", 1, 8, 0)) }
    var dinK5 by remember { mutableStateOf(KatoriItem("katori5", "Roti", "2x Jowar Roti", "🫓", 150, "Gluten-free sorghum rotis", 4, 30, 2)) }

    // Bowls for Late Night Cravings Platter
    var lnCenter by remember { mutableStateOf(KatoriItem("center", "Recovery", "Warm Haldi Doodh", "🥛", 120, "Golden turmeric milk for deep joint repair", 4, 12, 5)) }
    var lnK1 by remember { mutableStateOf(KatoriItem("katori1", "Sleep", "Chamomile Brew", "🫖", 15, "Calming herbal infusion for deep sleep", 0, 3, 0)) }
    var lnK2 by remember { mutableStateOf(KatoriItem("katori2", "Nuts", "Soaked Walnuts", "🧠", 90, "Rich in brain omega-3 & melatonin", 2, 2, 9)) }
    var lnK3 by remember { mutableStateOf(KatoriItem("katori3", "Seeds", "Pumpkin Seeds", "🌱", 60, "Natural magnesium for muscle relaxation", 3, 2, 5)) }
    var lnK4 by remember { mutableStateOf(KatoriItem("katori4", "Crunch", "Salted Makhana", "🍿", 55, "Low calorie guilt-free crunchy craving", 1, 10, 1)) }
    var lnK5 by remember { mutableStateOf(KatoriItem("katori5", "Treat", "Dark Chocolate (70%)", "🍫", 55, "Rich flavanols to soothe sweet tooth", 1, 5, 4)) }

    // Dynamic nutrition calculations per meal slot
    val bfCals = bfCenter.calories + bfK1.calories + bfK2.calories + bfK3.calories + bfK4.calories + bfK5.calories
    val lunchCals = lunchCenter.calories + lunchK1.calories + lunchK2.calories + lunchK3.calories + lunchK4.calories + lunchK5.calories
    val snackCals = snackCenter.calories + snackK1.calories + snackK2.calories + snackK3.calories + snackK4.calories + snackK5.calories
    val dinCals = dinCenter.calories + dinK1.calories + dinK2.calories + dinK3.calories + dinK4.calories + dinK5.calories
    val lnCals = lnCenter.calories + lnK1.calories + lnK2.calories + lnK3.calories + lnK4.calories + lnK5.calories

    val totalDayPlannedCalories = bfCals + lunchCals + snackCals + dinCals + lnCals

    // Personalized Calorie Budget Target based on Mifflin-St Jeor
    val bmr = (10.0 * weightKg) + (6.25 * heightCm) - (5.0 * 28) + 5.0
    val tdee = bmr * 1.35
    val calorieTargetBudget = when (activeGoal) {
        "lose" -> (tdee - 400).toInt().coerceAtLeast(1200)
        "gain" -> (tdee + 400).toInt()
        else -> tdee.toInt()
    }

    // Active Thali references for the currently selected slot
    val (curCenter, curK1, curK2, curK3, curK4, curK5) = when (selectedMealSlot) {
        "breakfast" -> Tuple6(bfCenter, bfK1, bfK2, bfK3, bfK4, bfK5)
        "snacks" -> Tuple6(snackCenter, snackK1, snackK2, snackK3, snackK4, snackK5)
        "dinner" -> Tuple6(dinCenter, dinK1, dinK2, dinK3, dinK4, dinK5)
        "latenight" -> Tuple6(lnCenter, lnK1, lnK2, lnK3, lnK4, lnK5)
        else -> Tuple6(lunchCenter, lunchK1, lunchK2, lunchK3, lunchK4, lunchK5)
    }

    val curThaliCalories = curCenter.calories + curK1.calories + curK2.calories + curK3.calories + curK4.calories + curK5.calories
    val curThaliProtein = curCenter.proteinG + curK1.proteinG + curK2.proteinG + curK3.proteinG + curK4.proteinG + curK5.proteinG
    val curThaliCarbs = curCenter.carbsG + curK1.carbsG + curK2.carbsG + curK3.carbsG + curK4.carbsG + curK5.carbsG

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(14.dp))

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
                    val isSelected = activeGoal == goalKey
                    Button(
                        onClick = { activeGoal = goalKey },
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

        // 2. Pure Veg vs Non-Veg Switcher
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
                        lunchK2 = KatoriItem("katori2", "Sabzi", "Rajma Masala", "🍛", 240, "Red kidney beans in gravy", 13, 38, 4)
                        dinK2 = KatoriItem("katori2", "Sabzi", "Lauki Chana Sabzi", "🥒", 130, "Bottle gourd with light dal", 6, 18, 3)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isVeg) EmeraldGreen.copy(alpha = 0.25f) else SurfaceDark,
                        contentColor = if (isVeg) EmeraldGreen else TextSecondary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("🌱 Pure Veg Thalis", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Button(
                    onClick = {
                        isVeg = false
                        lunchK2 = KatoriItem("katori2", "Curry", "Chicken Curry", "🍗", 280, "Lean chicken breast in aromatic gravy", 28, 8, 14)
                        dinK2 = KatoriItem("katori2", "Protein", "Tandoori Chicken", "🍗", 210, "Roasted chicken with mint seasoning", 26, 4, 8)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isVeg) AmberFire.copy(alpha = 0.25f) else SurfaceDark,
                        contentColor = if (!isVeg) AmberFire else TextSecondary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("🍗 Non-Veg Thalis", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 3. Gym Fuel Banner with Personalized Calorie Target
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
                        text = when (activeGoal) {
                            "lose" -> "Weight Loss Calorie Deficit Target: $calorieTargetBudget kcal"
                            "gain" -> "Weight Gain Calorie Surplus Target: $calorieTargetBudget kcal"
                            else -> "Maintenance Calorie Target: $calorieTargetBudget kcal"
                        },
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Total Day Planned: $totalDayPlannedCalories kcal. Build your authentic Indian meals below to meet your goal!",
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

        // 5. INTERACTIVE THALI PLATE (Renders for ALL 5 MEALS!)
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
                        text = getSlotTitle(selectedMealSlot),
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

                // The Circular Metallic Thali Plate (280dp diameter)
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
                    // Center Bowl (78dp)
                    KatoriBowlView(
                        item = curCenter,
                        sizeDp = 78,
                        onClick = { showDishPickerSlot = "center" }
                    )

                    // Top Bowl (katori1)
                    Box(modifier = Modifier.offset(x = 0.dp, y = (-92).dp)) {
                        KatoriBowlView(
                            item = curK1,
                            sizeDp = 64,
                            onClick = { showDishPickerSlot = "katori1" }
                        )
                    }

                    // Top-Right Bowl (katori2)
                    Box(modifier = Modifier.offset(x = 88.dp, y = (-28).dp)) {
                        KatoriBowlView(
                            item = curK2,
                            sizeDp = 64,
                            onClick = { showDishPickerSlot = "katori2" }
                        )
                    }

                    // Bottom-Right Bowl (katori3)
                    Box(modifier = Modifier.offset(x = 54.dp, y = 74.dp)) {
                        KatoriBowlView(
                            item = curK3,
                            sizeDp = 64,
                            onClick = { showDishPickerSlot = "katori3" }
                        )
                    }

                    // Bottom-Left Bowl (katori4)
                    Box(modifier = Modifier.offset(x = (-54).dp, y = 74.dp)) {
                        KatoriBowlView(
                            item = curK4,
                            sizeDp = 64,
                            onClick = { showDishPickerSlot = "katori4" }
                        )
                    }

                    // Top-Left Bowl (katori5)
                    Box(modifier = Modifier.offset(x = (-88).dp, y = (-28).dp)) {
                        KatoriBowlView(
                            item = curK5,
                            sizeDp = 64,
                            onClick = { showDishPickerSlot = "katori5" }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Current Meal Macros Breakdown
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
                            Text(text = "This Meal Total", color = TextSecondary, fontSize = 11.sp)
                            Text(
                                text = "$curThaliCalories kcal",
                                color = TextPrimary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "${curThaliProtein}g", color = EmeraldGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(text = "Protein", color = TextSecondary, fontSize = 10.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "${curThaliCarbs}g", color = AmberFire, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(text = "Carbs", color = TextSecondary, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 6. ACTIONABLE DAILY DIET PLAN TRACKER (User Follows & Follows Through)
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
                    Column {
                        Text(
                            text = "📋 My Daily Diet Plan",
                            color = TextPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Follow your personalized meals throughout the day",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    Button(
                        onClick = onEditHealthProfile,
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceLightDark),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Profile & Goal", color = EmeraldGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Planned vs Target Comparison Bar
                val diff = calorieTargetBudget - totalDayPlannedCalories
                val statusText = when {
                    activeGoal == "lose" && diff >= 0 -> "🎯 On track! Perfect $diff kcal deficit for weight loss."
                    activeGoal == "lose" && diff < 0 -> "⚠️ ${-diff} kcal over deficit budget. Consider lighter snacks."
                    activeGoal == "gain" && diff <= 0 -> "🎯 On track! Great ${-diff} kcal surplus for healthy weight gain."
                    activeGoal == "gain" && diff > 0 -> "📈 Need $diff more kcal to reach weight gain surplus."
                    else -> "⚖️ Balanced maintenance: Planned $totalDayPlannedCalories / Target $calorieTargetBudget kcal."
                }

                Surface(
                    color = EmeraldGreen.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = statusText,
                        color = EmeraldGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // The 5 Actionable Meal Plan Cards
                DietPlanRowItem(
                    slotTitle = "🌅 Breakfast",
                    dishes = "${bfCenter.name}, ${bfK1.name}, ${bfK2.name}",
                    calories = bfCals,
                    onTap = { selectedMealSlot = "breakfast" }
                )
                Spacer(modifier = Modifier.height(8.dp))
                DietPlanRowItem(
                    slotTitle = "☀️ Lunch Thali",
                    dishes = "${lunchCenter.name}, ${lunchK1.name}, ${lunchK2.name}, ${lunchK5.name}",
                    calories = lunchCals,
                    onTap = { selectedMealSlot = "lunch" }
                )
                Spacer(modifier = Modifier.height(8.dp))
                DietPlanRowItem(
                    slotTitle = "☕ Evening Snacks",
                    dishes = "${snackCenter.name}, ${snackK1.name}, ${snackK2.name}",
                    calories = snackCals,
                    onTap = { selectedMealSlot = "snacks" }
                )
                Spacer(modifier = Modifier.height(8.dp))
                DietPlanRowItem(
                    slotTitle = "🌙 Dinner Thali",
                    dishes = "${dinCenter.name}, ${dinK1.name}, ${dinK2.name}, ${dinK5.name}",
                    calories = dinCals,
                    onTap = { selectedMealSlot = "dinner" }
                )
                Spacer(modifier = Modifier.height(8.dp))
                DietPlanRowItem(
                    slotTitle = "🌃 Late Night Cravings",
                    dishes = "${lnCenter.name}, ${lnK2.name}, ${lnK5.name}",
                    calories = lnCals,
                    onTap = { selectedMealSlot = "latenight" }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 7. Custom Food Creator Button
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
                        text = "Add custom dishes to any Thali meal",
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

        Spacer(modifier = Modifier.height(14.dp))

        // Sleep & Recovery Information Card
        SleepRecoveryBanner()

        Spacer(modifier = Modifier.height(18.dp))

        UniversalDonateRow(onClick = onDonateClick)
        Spacer(modifier = Modifier.height(10.dp))
        UniversalFooterCredit(onInstagramClick = onInstagramClick)
    }

    // Dish Picker Dialog to swap bowls
    if (showDishPickerSlot != null) {
        val slot = showDishPickerSlot!!
        DishPickerDialog(
            slot = slot,
            mealSlot = selectedMealSlot,
            isVeg = isVeg,
            onDismiss = { showDishPickerSlot = null },
            onSelect = { newItem ->
                when (selectedMealSlot) {
                    "breakfast" -> when (slot) {
                        "center" -> bfCenter = newItem
                        "katori1" -> bfK1 = newItem
                        "katori2" -> bfK2 = newItem
                        "katori3" -> bfK3 = newItem
                        "katori4" -> bfK4 = newItem
                        "katori5" -> bfK5 = newItem
                    }
                    "snacks" -> when (slot) {
                        "center" -> snackCenter = newItem
                        "katori1" -> snackK1 = newItem
                        "katori2" -> snackK2 = newItem
                        "katori3" -> snackK3 = newItem
                        "katori4" -> snackK4 = newItem
                        "katori5" -> snackK5 = newItem
                    }
                    "dinner" -> when (slot) {
                        "center" -> dinCenter = newItem
                        "katori1" -> dinK1 = newItem
                        "katori2" -> dinK2 = newItem
                        "katori3" -> dinK3 = newItem
                        "katori4" -> dinK4 = newItem
                        "katori5" -> dinK5 = newItem
                    }
                    "latenight" -> when (slot) {
                        "center" -> lnCenter = newItem
                        "katori1" -> lnK1 = newItem
                        "katori2" -> lnK2 = newItem
                        "katori3" -> lnK3 = newItem
                        "katori4" -> lnK4 = newItem
                        "katori5" -> lnK5 = newItem
                    }
                    else -> when (slot) {
                        "center" -> lunchCenter = newItem
                        "katori1" -> lunchK1 = newItem
                        "katori2" -> lunchK2 = newItem
                        "katori3" -> lunchK3 = newItem
                        "katori4" -> lunchK4 = newItem
                        "katori5" -> lunchK5 = newItem
                    }
                }
                showDishPickerSlot = null
            }
        )
    }

    // Custom Dish Dialog
    if (showCustomDishDialog) {
        CustomDishDialog(
            defaultSlot = selectedMealSlot,
            onDismiss = { showCustomDishDialog = false },
            onAdd = { newKatori ->
                when (selectedMealSlot) {
                    "breakfast" -> bfCenter = newKatori
                    "snacks" -> snackCenter = newKatori
                    "dinner" -> dinCenter = newKatori
                    "latenight" -> lnCenter = newKatori
                    else -> lunchCenter = newKatori
                }
                showCustomDishDialog = false
            }
        )
    }
}

data class Tuple6<A, B, C, D, E, F>(
    val a: A, val b: B, val c: C, val d: D, val e: E, val f: F
)

@Composable
fun DietPlanRowItem(
    slotTitle: String,
    dishes: String,
    calories: Int,
    onTap: () -> Unit
) {
    Surface(
        color = SurfaceLightDark,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onTap)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = slotTitle, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(
                    text = dishes,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                color = EmeraldGreen.copy(alpha = 0.18f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "$calories kcal",
                    color = EmeraldGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
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
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(sizeDp.dp)
            .shadow(6.dp, CircleShape)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(Color(0xFF334155), Color(0xFF1E293B), Color(0xFF0F172A))
                )
            )
            .border(2.dp, EmeraldGreen.copy(alpha = 0.6f), CircleShape)
            .clickable(onClick = onClick)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(2.dp)
        ) {
            Text(text = item.emoji, fontSize = if (sizeDp > 70) 22.sp else 18.sp)
            Text(
                text = item.name,
                color = TextPrimary,
                fontSize = if (sizeDp > 70) 10.sp else 9.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
            Text(
                text = "${item.calories}k",
                color = EmeraldGreen,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun SleepRecoveryBanner() {
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
                painter = painterResource(id = R.drawable.mascot_sleeping),
                contentDescription = "Sleeping Mascot",
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Sleep & Muscle Recovery 💤",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = "7-8 hours of quality sleep lowers cortisol, boosts growth hormone, and aids fat loss & muscle synthesis.",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

/* =====================================================================
   TAB 3: HYDRATION — ZERO DUMMY DATA, ACTIONABLE NOTIFICATIONS
   ===================================================================== */

@Composable
fun HydrationTab(
    waterConsumedMl: Int,
    waterTargetMl: Int,
    reminderIntervalHours: Int,
    onAddGlass: () -> Unit,
    onAddBottle: () -> Unit,
    onResetWater: () -> Unit,
    onSelectReminderInterval: (Int) -> Unit,
    onTriggerTestReminder: () -> Unit,
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
        Spacer(modifier = Modifier.height(14.dp))

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

        // Circular Hydration Ring (Ample clearance)
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(220.dp)
        ) {
            Canvas(modifier = Modifier.size(200.dp)) {
                drawCircle(
                    color = TrackBackground,
                    style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
                )
                drawArc(
                    brush = Brush.sweepGradient(listOf(OceanBlue, Color(0xFF38BDF8), OceanBlue)),
                    startAngle = -90f,
                    sweepAngle = progress * 360f,
                    useCenter = false,
                    style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = String.format(Locale.getDefault(), "%,d", waterConsumedMl),
                    color = TextPrimary,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "/ $waterTargetMl ml",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Action Buttons Row
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

        Spacer(modifier = Modifier.height(10.dp))

        // Reset to 0 button
        OutlinedButton(
            onClick = onResetWater,
            border = BorderStroke(1.dp, SurfaceLightDark),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reset Water to 0 ml (Fresh Start)", color = TextSecondary, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // WATER REMINDER INTERVAL SELECTOR (1 Hour, 2 Hours, 3 Hours, Off)
        Surface(
            color = SurfaceDark,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "⏰ Water Reminder Interval",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "Receive notifications on your phone with +250ml, +500ml, and Skip buttons",
                    color = TextSecondary,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        Pair(1, "1 Hour"),
                        Pair(2, "2 Hours"),
                        Pair(3, "3 Hours"),
                        Pair(0, "Off")
                    ).forEach { (hrs, label) ->
                        val isSelected = reminderIntervalHours == hrs
                        Button(
                            onClick = { onSelectReminderInterval(hrs) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) OceanBlue else SurfaceLightDark,
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
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Send instant test notification button
                Button(
                    onClick = onTriggerTestReminder,
                    colors = ButtonDefaults.buttonColors(containerColor = OceanBlue.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = OceanBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "🔔 Test Notification (with +250ml / +500ml / Skip)",
                        color = OceanBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

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
    weightKg: Float,
    heightCm: Float,
    weightGoal: String,
    waterReminderIntervalHours: Int,
    onEditGoalClick: () -> Unit,
    onEditHealthProfile: () -> Unit,
    onDonateClick: () -> Unit,
    onCopyUpiClick: () -> Unit,
    onInstagramClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    // Calculate dynamic BMI and Calorie target
    val heightM = heightCm / 100f
    val bmi = if (heightM > 0) weightKg / (heightM * heightM) else 22f
    val bmiCat = when {
        bmi < 18.5f -> "Underweight"
        bmi < 25.0f -> "Normal weight"
        bmi < 30.0f -> "Overweight"
        else -> "Obese"
    }
    val bmr = (10.0 * weightKg) + (6.25 * heightCm) - (5.0 * 28) + 5.0
    val tdee = bmr * 1.35
    val calorieTarget = when (weightGoal) {
        "lose" -> (tdee - 400).toInt().coerceAtLeast(1200)
        "gain" -> (tdee + 400).toInt()
        else -> tdee.toInt()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        // Daily Step Goal Card
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

        // EDITABLE PERSONAL HEALTH PROFILE CARD
        Surface(
            color = SurfaceDark,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Personal Health Profile",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Goal: ${weightGoal.replaceFirstChar { it.uppercase() }} · $calorieTarget kcal/day",
                            color = EmeraldGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Button(
                        onClick = onEditHealthProfile,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Text("Edit", color = BackgroundDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Weight", color = TextSecondary, fontSize = 11.sp)
                        Text(text = "%.1f kg".format(weightKg), color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Column {
                        Text(text = "Height", color = TextSecondary, fontSize = 11.sp)
                        Text(text = "%.0f cm".format(heightCm), color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Column {
                        Text(text = "BMI", color = TextSecondary, fontSize = 11.sp)
                        Text(text = "%.1f ($bmiCat)".format(bmi), color = EmeraldGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Water Reminders Summary
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
                    Text(text = "Water Drink Reminder", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(
                        text = if (waterReminderIntervalHours > 0) "Every $waterReminderIntervalHours hour(s)" else "Turned Off",
                        color = OceanBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Icon(
                    imageVector = Icons.Default.WaterDrop,
                    contentDescription = null,
                    tint = OceanBlue,
                    modifier = Modifier.size(24.dp)
                )
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

                // 3. QR code (shown at 240 x 240, tappable)
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .size(240.dp)
                        .clickable(
                            onClick = onDonateClick,
                            onClickLabel = "Tap to pay with any UPI app"
                        )
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize().padding(12.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.donate_qr),
                            contentDescription = "Tap to pay with any UPI app",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 4. Text: Scan to pay with any UPI app · or tap the code to pay.
                Text(
                    text = "Scan to pay with any UPI app · or tap the code to pay.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 5. Button: Click to pay
                Button(
                    onClick = onDonateClick,
                    colors = ButtonDefaults.buttonColors(containerColor = RosePink),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Click to pay",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 6. Row: UPI ID: jaynishad2010@okhdfcbank followed by Copy button
                Surface(
                    color = SurfaceLightDark,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "UPI ID: jaynishad2010@okhdfcbank",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = onCopyUpiClick,
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Copy",
                                color = BackgroundDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 7. Text: Your donation helps keep Healbuds running and ad-free.
                Text(
                    text = "Your donation helps keep Healbuds running and ad-free.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Universal Footer Credit (Brief 2d)
        UniversalFooterCredit(onInstagramClick = onInstagramClick)
    }
}

/* =====================================================================
   FALLBACK BOTTOM SHEET (Brief Section 3)
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
                contentDescription = "Mascot Heart",
                modifier = Modifier
                    .size(width = 120.dp, height = 70.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Donate to the developer",
                color = TextPrimary,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Pay any amount you like, from any UPI app. Your donation helps keep Healbuds running and ad-free.",
                color = TextSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                color = BackgroundDark,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "UPI ID: jaynishad2010@okhdfcbank",
                    color = EmeraldGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onCopyUpiClick,
                colors = ButtonDefaults.buttonColors(containerColor = RosePink),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = "Copy UPI ID",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
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
    icon: ImageVector,
    iconColor: Color,
    value: String,
    unit: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = SurfaceDark,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = value,
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = unit,
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
    }
}

/* =====================================================================
   DIALOG: SET DAILY STEP GOAL (Thousands quick chips + custom input)
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
                    placeholder = { Text("e.g. 7,420 or 10,000", color = TextMuted) },
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
                    text = "Quick Thousand Goals:",
                    color = TextSecondary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )

                // Quick buttons in thousands (5000, 6000, ..., 15000)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    thousandGoals.chunked(3).forEach { rowGoals ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            rowGoals.forEach { g ->
                                val isSelected = selectedGoal == g
                                Button(
                                    onClick = {
                                        selectedGoal = g
                                        customGoalText = g.toString()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSelected) EmeraldGreen else SurfaceLightDark,
                                        contentColor = if (isSelected) BackgroundDark else TextPrimary
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(horizontal = 2.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "${g / 1000}k",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSelect(selectedGoal) },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
            ) {
                Text("Set Goal", color = BackgroundDark, fontWeight = FontWeight.Bold)
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
   DIALOG: EDIT PERSONAL HEALTH PROFILE (Weight, Height, Goal)
   ===================================================================== */

@Composable
fun HealthProfileDialog(
    currentWeightKg: Float,
    currentHeightCm: Float,
    currentGoal: String,
    onDismiss: () -> Unit,
    onSave: (Float, Float, String) -> Unit
) {
    var weightText by remember { mutableStateOf(currentWeightKg.toString()) }
    var heightText by remember { mutableStateOf(currentHeightCm.toString()) }
    var selectedGoal by remember { mutableStateOf(currentGoal) }

    val weightVal = weightText.toFloatOrNull() ?: currentWeightKg
    val heightVal = heightText.toFloatOrNull() ?: currentHeightCm

    val heightM = heightVal / 100f
    val previewBmi = if (heightM > 0) weightVal / (heightM * heightM) else 22f
    val bmr = (10.0 * weightVal) + (6.25 * heightVal) - (5.0 * 28) + 5.0
    val tdee = bmr * 1.35
    val previewCals = when (selectedGoal) {
        "lose" -> (tdee - 400).toInt().coerceAtLeast(1200)
        "gain" -> (tdee + 400).toInt()
        else -> tdee.toInt()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Text(
                text = "👤 Personal Health Profile",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Your metrics calculate personal daily calories and stride length.",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                // Weight Input
                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it },
                    label = { Text("Weight (kg)", color = TextSecondary) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldGreen,
                        unfocusedBorderColor = SurfaceLightDark,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Height Input
                OutlinedTextField(
                    value = heightText,
                    onValueChange = { heightText = it },
                    label = { Text("Height (cm)", color = TextSecondary) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldGreen,
                        unfocusedBorderColor = SurfaceLightDark,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Fitness Goal:",
                    color = TextSecondary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        Pair("lose", "🔻 Lose"),
                        Pair("gain", "🔺 Gain"),
                        Pair("maintain", "⚖️ Maintain")
                    ).forEach { (key, label) ->
                        val isSelected = selectedGoal == key
                        Button(
                            onClick = { selectedGoal = key },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) EmeraldGreen else SurfaceLightDark,
                                contentColor = if (isSelected) BackgroundDark else TextPrimary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                        ) {
                            Text(label, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }

                // Live Preview Card
                Surface(
                    color = BackgroundDark,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Calculated Live Results:",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "BMI: %.1f · Daily Target: %d kcal".format(previewBmi, previewCals),
                            color = EmeraldGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val w = weightText.toFloatOrNull() ?: currentWeightKg
                    val h = heightText.toFloatOrNull() ?: currentHeightCm
                    onSave(w, h, selectedGoal)
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
            ) {
                Text("Save Profile", color = BackgroundDark, fontWeight = FontWeight.Bold)
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
   DIALOG: DISH PICKER (SWAP KATORI BOWL FOR ANY MEAL)
   ===================================================================== */

@Composable
fun DishPickerDialog(
    slot: String,
    mealSlot: String,
    isVeg: Boolean,
    onDismiss: () -> Unit,
    onSelect: (KatoriItem) -> Unit
) {
    val options = getAvailableDishesForSlot(slot, mealSlot, isVeg)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Text(
                text = "🍲 Swap Bowl Dish (${getSlotTitle(mealSlot)})",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                options.forEach { item ->
                    Surface(
                        color = SurfaceLightDark,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(item) }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = item.emoji, fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.name,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = item.description,
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = EmeraldGreen.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "${item.calories} kcal",
                                    color = EmeraldGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

/* =====================================================================
   DIALOG: CUSTOM DISH CREATOR
   ===================================================================== */

@Composable
fun CustomDishDialog(
    defaultSlot: String,
    onDismiss: () -> Unit,
    onAdd: (KatoriItem) -> Unit
) {
    var dishName by remember { mutableStateOf("") }
    var emoji by remember { mutableStateOf("🍲") }
    var caloriesText by remember { mutableStateOf("200") }
    var proteinText by remember { mutableStateOf("10") }
    var carbsText by remember { mutableStateOf("30") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Text(
                text = "➕ Add Custom Food to Thali",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
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
                    placeholder = { Text("e.g. Matar Paneer, Dosa, etc.", color = TextMuted) },
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
                    value = emoji,
                    onValueChange = { emoji = it },
                    label = { Text("Emoji", color = TextSecondary) },
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
                    label = { Text("Calories (kcal)", color = TextSecondary) },
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = proteinText,
                        onValueChange = { proteinText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Protein (g)", color = TextSecondary) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldGreen,
                            unfocusedBorderColor = SurfaceLightDark,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = carbsText,
                        onValueChange = { carbsText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Carbs (g)", color = TextSecondary) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldGreen,
                            unfocusedBorderColor = SurfaceLightDark,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalName = if (dishName.isBlank()) "Custom Food" else dishName
                    val cals = caloriesText.toIntOrNull() ?: 200
                    val prot = proteinText.toIntOrNull() ?: 8
                    val carb = carbsText.toIntOrNull() ?: 25
                    onAdd(
                        KatoriItem(
                            slotId = "center",
                            bowlLabel = "Custom",
                            name = finalName,
                            emoji = if (emoji.isBlank()) "🍲" else emoji,
                            calories = cals,
                            description = "Custom homemade healthy dish",
                            proteinG = prot,
                            carbsG = carb,
                            fatG = 4
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
            ) {
                Text("Add to Thali", color = BackgroundDark, fontWeight = FontWeight.Bold)
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
   HELPER FUNCTIONS: SLOT TITLES & CURATED DISHES
   ===================================================================== */

fun getSlotTitle(slot: String): String = when (slot) {
    "breakfast" -> "🌅 Breakfast Platter (Interactive)"
    "lunch" -> "☀️ Authentic Lunch Thali (Interactive)"
    "snacks" -> "☕ Evening Snacks Platter (Interactive)"
    "dinner" -> "🌙 Digestible Dinner Thali (Interactive)"
    "latenight" -> "🌃 Late Night Cravings Platter (Interactive)"
    else -> "🍲 Interactive Thali Plate"
}

fun getAvailableDishesForSlot(
    bowlSlot: String,
    mealSlot: String,
    isVeg: Boolean
): List<KatoriItem> {
    return when (bowlSlot) {
        "center" -> listOf(
            KatoriItem("center", "Grains", "Basmati Rice", "🍚", 210, "Steamed fragrant white rice", 4, 45, 1),
            KatoriItem("center", "Grains", "Jeera Rice", "🍚", 225, "Cumin tempered rice", 4, 46, 3),
            KatoriItem("center", "Grains", "Moong Khichdi", "🍲", 230, "Comforting digestible moong dal & rice", 8, 40, 4),
            KatoriItem("center", "Grains", "Poha & Peanuts", "🥣", 210, "Light flattened rice with peanuts", 5, 38, 5),
            KatoriItem("center", "Grains", "Oats Upma", "🥣", 190, "Heart-healthy vegetable oats upma", 6, 32, 4),
            KatoriItem("center", "Recovery", "Warm Haldi Doodh", "🥛", 120, "Golden turmeric milk for deep joint repair", 4, 12, 5)
        )
        "katori1" -> listOf(
            KatoriItem("katori1", "Dal", "Dal Tadka", "🥣", 180, "Yellow arhar lentils with cumin & garlic", 11, 24, 5),
            KatoriItem("katori1", "Dal", "Moong Dal", "🥣", 140, "Light yellow split moong dal", 9, 20, 2),
            KatoriItem("katori1", "Dal", "Dal Makhani (Light)", "🥣", 220, "Slow-simmered black lentils with low cream", 10, 26, 8),
            KatoriItem("katori1", "Pulses", "Moong Sambhar", "🍲", 130, "Lentils with drumstick & pumpkin", 7, 20, 2),
            KatoriItem("katori1", "Steamed", "Besan Dhokla", "🥮", 140, "Steamed fermented gram flour sponge", 6, 22, 3),
            KatoriItem("katori1", "Sleep", "Chamomile Cinnamon Brew", "🫖", 15, "Calming herbal infusion for deep sleep", 0, 3, 0)
        )
        "katori2" -> if (isVeg) listOf(
            KatoriItem("katori2", "Sabzi", "Rajma Masala", "🍛", 240, "Red kidney beans in spiced gravy", 13, 38, 4),
            KatoriItem("katori2", "Sabzi", "Paneer Bhurji", "🧀", 180, "Scrambled cottage cheese with herbs", 14, 4, 12),
            KatoriItem("katori2", "Sabzi", "Chole Masala", "🍛", 260, "Spiced chickpea curry", 12, 42, 5),
            KatoriItem("katori2", "Sabzi", "Palak Paneer", "🥬", 210, "Spinach curry with cottage cheese", 12, 10, 14),
            KatoriItem("katori2", "Sabzi", "Lauki Chana Sabzi", "🥒", 130, "Bottle gourd with light dal", 6, 18, 3),
            KatoriItem("katori2", "Nuts", "Soaked Walnuts & Almonds", "🧠", 90, "Rich in brain omega-3 & melatonin", 2, 2, 9)
        ) else listOf(
            KatoriItem("katori2", "Curry", "Chicken Curry", "🍗", 280, "Lean chicken breast in aromatic gravy", 28, 8, 14),
            KatoriItem("katori2", "Protein", "Tandoori Chicken", "🍗", 210, "Roasted chicken with mint seasoning", 26, 4, 8),
            KatoriItem("katori2", "Protein", "2 Boiled Eggs", "🥚", 155, "Farm fresh hard-boiled eggs", 13, 1, 11),
            KatoriItem("katori2", "Sabzi", "Rajma Masala", "🍛", 240, "Red kidney beans in spiced gravy", 13, 38, 4)
        )
        "katori3" -> listOf(
            KatoriItem("katori3", "Curd", "Curd / Dahi", "🥛", 100, "Fresh probiotic curd", 6, 8, 5),
            KatoriItem("katori3", "Raita", "Cucumber Raita", "🥛", 70, "Cooling digestive probiotic", 4, 6, 3),
            KatoriItem("katori3", "Raita", "Boondi Raita", "🥣", 110, "Crispy gram pearls in spiced curd", 4, 12, 6),
            KatoriItem("katori3", "Chutney", "Mint Coconut Chutney", "🌿", 45, "Fresh mint, coriander & lemon", 1, 4, 1),
            KatoriItem("katori3", "Seeds", "Roasted Pumpkin Seeds", "🌱", 60, "Natural magnesium for muscle relaxation", 3, 2, 5)
        )
        "katori4" -> listOf(
            KatoriItem("katori4", "Salad", "Salad & Achar", "🥗", 35, "Cucumber, tomato & lemon pickle", 1, 7, 0),
            KatoriItem("katori4", "Salad", "Sprout Chaat", "🥗", 75, "Sprouted moong & pomegranate", 6, 12, 1),
            KatoriItem("katori4", "Salad", "Beetroot Salad", "🥗", 40, "Nitrate-rich stamina booster", 1, 8, 0),
            KatoriItem("katori4", "Fruit", "Apple & Papaya Slices", "🍎", 65, "Rich in digestive enzymes", 0, 16, 0),
            KatoriItem("katori4", "Crunch", "Salted Makhana", "🍿", 55, "Low calorie crunchy craving", 1, 10, 1)
        )
        else -> listOf(
            KatoriItem("katori5", "Roti", "2x Phulka Roti", "🫓", 160, "Whole wheat soft rotis", 6, 32, 1),
            KatoriItem("katori5", "Roti", "2x Jowar Roti", "🫓", 150, "Gluten-free sorghum rotis", 4, 30, 2),
            KatoriItem("katori5", "Roti", "2x Bajra Roti", "🫓", 170, "Pearl millet winter roti", 5, 34, 2),
            KatoriItem("katori5", "Brew", "Masala Chai", "☕", 55, "Cardamom & ginger morning tea", 2, 8, 2),
            KatoriItem("katori5", "Treat", "Dark Chocolate (70%)", "🍫", 55, "Flavanols to satisfy sweet cravings", 1, 5, 4)
        )
    }
}
