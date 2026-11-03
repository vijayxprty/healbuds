package com.steptracker.app.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    onUpdateGoal: (Int) -> Unit
) {
    var showGoalDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val formattedDate = remember {
        val sdf = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
        sdf.format(Date())
    }

    val progress = remember(currentSteps, dailyGoal) {
        if (dailyGoal > 0) (currentSteps.toFloat() / dailyGoal).coerceIn(0f, 1f) else 0f
    }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000),
        label = "progress_anim"
    )

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
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
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "StepPulse",
                            color = TextPrimary,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = formattedDate,
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                IconButton(
                    onClick = { showGoalDialog = true },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(SurfaceDark)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Goal",
                        tint = EmeraldGreen
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Circular Step Progress Ring
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(260.dp)
                    .padding(16.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 18.dp.toPx()
                    // Background track
                    drawCircle(
                        color = SurfaceDark,
                        style = Stroke(width = strokeWidth)
                    )
                    // Progress Arc with gradient
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(EmeraldLight, EmeraldGreen, OceanBlue, EmeraldLight)
                        ),
                        startAngle = -90f,
                        sweepAngle = animatedProgress * 360f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsWalk,
                        contentDescription = null,
                        tint = EmeraldGreen,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = String.format(Locale.getDefault(), "%,d", currentSteps),
                        color = TextPrimary,
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = String.format(Locale.getDefault(), "Goal: %,d steps", dailyGoal),
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = if (currentSteps >= dailyGoal) EmeraldGreen.copy(alpha = 0.2f) else SurfaceDark,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (currentSteps >= dailyGoal) "🎉 Goal Achieved!" else "${(progress * 100).toInt()}% Done",
                            color = if (currentSteps >= dailyGoal) EmeraldGreen else TextSecondary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3 Metric Badges: Distance, Calories, Active Minutes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.PinDrop,
                    iconColor = OceanBlue,
                    value = String.format(Locale.getDefault(), "%.2f", distanceKm),
                    unit = "km",
                    label = "Distance"
                )
                MetricCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.LocalFireDepartment,
                    iconColor = AmberFire,
                    value = String.format(Locale.getDefault(), "%.0f", calories),
                    unit = "kcal",
                    label = "Burned"
                )
                MetricCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Schedule,
                    iconColor = PurpleMetric,
                    value = "$activeMinutes",
                    unit = "mins",
                    label = "Active"
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Recent 7 Days Chart Preview
            WeeklyProgressCard(
                history = recentHistory,
                dailyGoal = dailyGoal,
                todaySteps = currentSteps
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Privacy & Battery Card
            Surface(
                color = SurfaceDark,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = EmeraldGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "100% Offline & Private",
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Uses on-device hardware step sensor. Zero battery drain, no data leaves your phone.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
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
}

@Composable
fun MetricCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconColor: Color,
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
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = unit,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(bottom = 1.dp)
                )
            }
            Text(
                text = label,
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun WeeklyProgressCard(
    history: List<DailyStepRecord>,
    dailyGoal: Int,
    todaySteps: Int
) {
    Surface(
        color = SurfaceDark,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Weekly Activity",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "Goal: ${dailyGoal / 1000}k / day",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 7 Days Visual Bars
            val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            val maxSteps = maxOf(dailyGoal, 12000).toFloat()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                days.forEachIndexed { index, dayName ->
                    // For demo, if history is sparse, show today's and previous days
                    val daySteps = if (index == 6) todaySteps else (history.getOrNull(index)?.steps ?: 0)
                    val barHeightFraction = (daySteps / maxSteps).coerceIn(0.08f, 1f)
                    val isGoalMet = daySteps >= dailyGoal

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(18.dp)
                                .fillMaxHeight(barHeightFraction)
                                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                .background(
                                    if (isGoalMet) EmeraldGreen else SurfaceLightDark
                                )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = dayName,
                            color = if (index == 6) EmeraldGreen else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (index == 6) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
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
