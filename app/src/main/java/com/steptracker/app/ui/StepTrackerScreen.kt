package com.steptracker.app.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.steptracker.app.R
import com.steptracker.app.data.DailyStepRecord
import com.steptracker.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
                    recentHistory = recentHistory,
                    onDonateClick = onDonateClick,
                    onInstagramClick = onInstagramClick
                )
                1 -> StreaksTab(
                    streakDays = streakDays,
                    restDaysUsed = restDaysUsed,
                    onDonateClick = onDonateClick,
                    onInstagramClick = onInstagramClick
                )
                2 -> IndianDietTab(
                    onDonateClick = onDonateClick,
                    onInstagramClick = onInstagramClick
                )
                3 -> HydrationTab(
                    waterConsumedMl = waterConsumedMl,
                    waterTargetMl = waterTargetMl,
                    onAddGlass = { onLogWater(250) },
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
   HEADER & BOTTOM NAV
   ===================================================================== */

@Composable
fun HealbudsHeader(
    streakDays: Int,
    onHeartClick: () -> Unit,
    onEditGoalClick: () -> Unit
) {
    val formattedDate = remember {
        val sdf = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
        sdf.format(Date())
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundDark)
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
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Private",
                    color = EmeraldGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(EmeraldGreen.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            Text(
                text = formattedDate,
                color = TextSecondary,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Heart donate button - Universal across all screens (Brief Section 2a)
            IconButton(
                onClick = onHeartClick,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(RosePink.copy(alpha = 0.15f))
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
                    indicatorColor = EmeraldGreen.copy(alpha = 0.15f)
                )
            )
        }
    }
}

/* =====================================================================
   UNIVERSAL COMPONENTS (DONATE ROW & FOOTER CREDIT)
   ===================================================================== */

/**
 * Universal Donate Row appearing at the bottom of screens 0, 1, 2, 3 (Brief Section 2b)
 */
@Composable
fun UniversalDonateRow(onClick: () -> Unit) {
    Surface(
        color = SurfaceDark,
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, RosePink.copy(alpha = 0.25f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.mascot_heart),
                contentDescription = "Donate to the developer",
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

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

/**
 * Universal Footer Credit appearing at the bottom of every screen (Brief Section 2d)
 */
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
   TAB 0: STEPS & ACTIVITY
   ===================================================================== */

@Composable
fun TodayStepsTab(
    currentSteps: Int,
    dailyGoal: Int,
    distanceKm: Double,
    calories: Double,
    activeMinutes: Int,
    recentHistory: List<DailyStepRecord>,
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

        // Circular Step Progress Ring
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(230.dp)
        ) {
            Canvas(modifier = Modifier.size(200.dp)) {
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

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = String.format(Locale.getDefault(), "%,d", currentSteps),
                    color = TextPrimary,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "/ %,d steps".format(dailyGoal),
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))
                val percentage = (progress * 100).toInt()
                Surface(
                    color = EmeraldGreen.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "% Goal",
                        color = EmeraldGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
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
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "10-Step Cadence Filter Active",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Ignores vehicle bumps & pocket twitches · Hardware sensor",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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
                        text = if (progress >= 1f) "Goal Achieved! You rock! 🎉" else "Keep going, buddy!",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = if (progress >= 1f)
                            "You crushed your daily goal! Time to celebrate!"
                        else
                            "Just %d steps left to hit your goal today!".format((dailyGoal - currentSteps).coerceAtLeast(0)),
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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
                iconTint = PurpleMetric,
                value = activeMinutes.toString(),
                unit = "min",
                label = "Active"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Universal Donate Row (Brief 2b)
        UniversalDonateRow(onClick = onDonateClick)

        Spacer(modifier = Modifier.height(10.dp))

        // Universal Footer Credit (Brief 2d)
        UniversalFooterCredit(onInstagramClick = onInstagramClick)
    }
}

/* =====================================================================
   TAB 1: STREAKS & SMART CALENDAR (2 REST DAYS/WEEK)
   ===================================================================== */

@Composable
fun StreaksTab(
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
                            .size(72.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = " Day Streak",
                                color = TextPrimary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "🔥", fontSize = 20.sp)
                        }
                        Text(
                            text = "You're on fire! Keep it going.",
                            color = EmeraldGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = SurfaceLightDark)
                Spacer(modifier = Modifier.height(12.dp))

                // Rest Day Protection Rule
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Smart Rest Protection",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "2 Rest Days allowed per week (Mon–Sun)",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    Surface(
                        color = OceanBlue.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = " / 2 Used",
                            color = OceanBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Streak Rules Info
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
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "3-Day Inactivity Rule",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "If inactive for 3 consecutive days, streak breaks into history and restarts at Day 1.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        UniversalDonateRow(onClick = onDonateClick)
        Spacer(modifier = Modifier.height(10.dp))
        UniversalFooterCredit(onInstagramClick = onInstagramClick)
    }
}

/* =====================================================================
   TAB 2: INDIAN THALI & DIET PLANNER
   ===================================================================== */

@Composable
fun IndianDietTab(
    onDonateClick: () -> Unit,
    onInstagramClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    var isVeg by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Diet Preference Switcher
        Surface(
            color = SurfaceDark,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Button(
                    onClick = { isVeg = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isVeg) EmeraldGreen else SurfaceDark,
                        contentColor = if (isVeg) BackgroundDark else TextSecondary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("🌱 Pure Veg Thali", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Button(
                    onClick = { isVeg = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isVeg) AmberFire else SurfaceDark,
                        contentColor = if (!isVeg) BackgroundDark else TextSecondary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("🍗 Non-Veg Thali", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mascot Gym Banner
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
                        text = "Fuel Your Movement",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Balanced Indian macros: Daal for protein, Roti/Rice for clean carbs, Salad for fiber.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Visual Thali Plate Representation
        Text(
            text = "Interactive Thali Plate",
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        val katoris = if (isVeg) {
            listOf(
                Triple("🥣 Dal Tadka", "180 kcal", "High Protein"),
                Triple("🍛 Rajma Masala", "240 kcal", "Fiber & Complex Carbs"),
                Triple("🍚 Basmati Rice", "210 kcal", "Sustained Energy"),
                Triple("🫓 Roti (2 pcs)", "160 kcal", "Whole Wheat"),
                Triple("🥗 Salad & Cucumber", "35 kcal", "Hydrating Micronutrients"),
                Triple("🥛 Fresh Curd / Dahi", "100 kcal", "Gut Health Probiotics")
            )
        } else {
            listOf(
                Triple("🍗 Chicken Curry", "280 kcal", "Lean Protein"),
                Triple("🥚 Boiled Eggs (2)", "155 kcal", "Essential Amino Acids"),
                Triple("🍚 Basmati Rice", "210 kcal", "Sustained Energy"),
                Triple("🫓 Roti (2 pcs)", "160 kcal", "Whole Wheat"),
                Triple("🥗 Salad & Greens", "35 kcal", "Vitamins & Fiber"),
                Triple("🥛 Chhaas / Buttermilk", "60 kcal", "Refreshing & Digestive")
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            katoris.forEach { (name, cal, note) ->
                Surface(
                    color = SurfaceDark,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = note, color = TextSecondary, fontSize = 10.sp)
                        }
                        Text(
                            text = cal,
                            color = EmeraldGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        UniversalDonateRow(onClick = onDonateClick)
        Spacer(modifier = Modifier.height(10.dp))
        UniversalFooterCredit(onInstagramClick = onInstagramClick)
    }
}

/* =====================================================================
   TAB 3: HYDRATION TRACKER
   ===================================================================== */

@Composable
fun HydrationTab(
    waterConsumedMl: Int,
    waterTargetMl: Int,
    onAddGlass: () -> Unit,
    onDonateClick: () -> Unit,
    onInstagramClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val progress = (waterConsumedMl.toFloat() / waterTargetMl).coerceIn(0f, 1f)

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
                        text = "Every walk & workout burns water. Keep your joints & metabolism happy!",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Hydration Progress Ring
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(190.dp)
        ) {
            Canvas(modifier = Modifier.size(160.dp)) {
                drawCircle(
                    color = TrackBackground,
                    style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
                )
                drawArc(
                    brush = Brush.sweepGradient(listOf(CyanWater, OceanBlue, CyanWater)),
                    startAngle = -90f,
                    sweepAngle = progress * 360f,
                    useCenter = false,
                    style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "%,d".format(waterConsumedMl),
                    color = TextPrimary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "/ %,d ml".format(waterTargetMl),
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // +250ml Glass Quick Log Button
        Button(
            onClick = onAddGlass,
            colors = ButtonDefaults.buttonColors(
                containerColor = CyanWater,
                contentColor = BackgroundDark
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(Icons.Default.WaterDrop, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Log +250ml Glass of Water", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        UniversalDonateRow(onClick = onDonateClick)
        Spacer(modifier = Modifier.height(10.dp))
        UniversalFooterCredit(onInstagramClick = onInstagramClick)
    }
}

/* =====================================================================
   TAB 4: MORE & SETTINGS (FULL DONATE SECTION - BRIEF SECTION 2c)
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

        // Profile & Goal Card
        Surface(
            color = SurfaceDark,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Personal Health Profile",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Daily Step Goal", color = TextSecondary, fontSize = 11.sp)
                        Text("%,d steps".format(dailyGoal), color = EmeraldGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    TextButton(onClick = onEditGoalClick) {
                        Text("Change", color = EmeraldGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        /* -------------------------------------------------------------
           FULL DONATE SECTION (Exact specification from Brief Section 2c)
           1. Heading: Donate to the developer (with a heart icon)
           2. The donate illustration (art-donate / mascot_heart, 160 x 90)
           3. The QR code (donate-qr, shown at 240 x 240), itself tappable
           4. Text: Scan to pay with any UPI app · or tap the code to pay.
           5. Button: Click to pay
           6. Row: UPI ID: jaynishad2010@okhdfcbank followed by Copy button
           7. Text: Your donation helps keep Healbuds running and ad-free.
           ------------------------------------------------------------- */
        Surface(
            color = SurfaceDark,
            shape = RoundedCornerShape(24.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, RosePink.copy(alpha = 0.25f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Heading
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Donate to the developer",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = RosePink,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Donate illustration (160 x 90)
                Image(
                    painter = painterResource(id = R.drawable.mascot_heart),
                    contentDescription = "Donate Illustration",
                    modifier = Modifier
                        .width(160.dp)
                        .height(90.dp)
                        .clip(RoundedCornerShape(14.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 3. QR code (240 x 240), tappable
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .size(240.dp)
                        .clickable { onDonateClick() }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.donate_qr),
                        contentDescription = "Tap to pay with any UPI app",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 4. Helper text
                Text(
                    text = "Scan to pay with any UPI app · or tap the code to pay.",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 5. Button: Click to pay
                Button(
                    onClick = onDonateClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RosePink,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Click to pay", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 6. Row: UPI ID followed by Copy button
                Surface(
                    color = SurfaceLightDark,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "UPI ID: jaynishad2010@okhdfcbank",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Button(
                            onClick = onCopyUpiClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SurfaceDark,
                                contentColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Copy", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 7. Subtext
                Text(
                    text = "Your donation helps keep Healbuds running and ad-free.",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Universal Footer Credit (Brief Section 2d)
        UniversalFooterCredit(onInstagramClick = onInstagramClick)
    }
}

/* =====================================================================
   FALLBACK BOTTOM SHEET (Brief Section 3, step 3)
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
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Donate to the developer",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Pay any amount you like, from any UPI app. Your donation helps keep Healbuds running and ad-free.",
                color = TextSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(14.dp))
            Surface(
                color = SurfaceLightDark,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "UPI ID: jaynishad2010@okhdfcbank",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onCopyUpiClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = RosePink,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Copy UPI ID", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

/* =====================================================================
   HELPER CARDS & DIALOGS
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
            modifier = Modifier.padding(12.dp),
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

@Composable
fun GoalDialog(
    currentGoal: Int,
    onDismiss: () -> Unit,
    onSelect: (Int) -> Unit
) {
    val presetGoals = listOf(5000, 8000, 10000, 12000, 15000)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Text(
                text = "Set Daily Step Goal",
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                presetGoals.forEach { goal ->
                    Surface(
                        color = if (goal == currentGoal) EmeraldGreen.copy(alpha = 0.2f) else SurfaceLightDark,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(goal) }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = String.format(Locale.getDefault(), "%,d steps", goal),
                                color = if (goal == currentGoal) EmeraldGreen else TextPrimary,
                                fontWeight = if (goal == currentGoal) FontWeight.Bold else FontWeight.Medium
                            )
                            if (goal == currentGoal) {
                                Text(
                                    text = "Active",
                                    color = EmeraldGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = EmeraldGreen)
            }
        }
    )
}
