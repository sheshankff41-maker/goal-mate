package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BadgeEntity
import com.example.data.model.Priority
import com.example.ui.GoalMateUiState
import com.example.ui.components.CircularProgressBar
import com.example.ui.components.StatCard
import com.example.ui.components.WeeklyBarChart
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.GoldenBadgeGradient
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.PrimaryGradient
import com.example.ui.theme.PurplePrimary

@Composable
fun AnalyticsScreen(
    state: GoalMateUiState
) {
    val totalGoals = state.goals.size
    val completedGoals = state.goals.count { it.goal.isCompleted }
    val goalRate = if (totalGoals > 0) ((completedGoals.toFloat() / totalGoals) * 100).toInt() else 0

    val totalTasks = state.tasks.size
    val completedTasks = state.tasks.count { it.isCompleted }
    val taskRate = if (totalTasks > 0) ((completedTasks.toFloat() / totalTasks) * 100).toInt() else 0

    val highPriorityTasks = state.tasks.filter { it.priority == Priority.HIGH }
    val highDone = highPriorityTasks.count { it.isCompleted }

    val avgHabitConsistency = if (state.habitsWithStats.isNotEmpty()) {
        (state.habitsWithStats.map { it.completionRate30Days }.average() * 100).toInt()
    } else 0

    val unlockedBadgesCount = state.badges.count { it.isUnlocked }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Productivity Analytics",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Track your trajectory, milestones, and consistency.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Productivity Scores Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Daily Score",
                    value = "${state.dailyProductivity}%",
                    subtitle = "Today's output",
                    icon = Icons.Default.Bolt,
                    gradient = PrimaryGradient,
                    modifier = Modifier.weight(1f)
                )

                val weeklyAvg = if (state.weeklyProgress.isNotEmpty()) (state.weeklyProgress.average() * 100).toInt() else 0
                StatCard(
                    title = "Weekly Avg",
                    value = "$weeklyAvg%",
                    subtitle = "Past 7 days",
                    icon = Icons.Default.CheckCircle,
                    gradient = Brush.linearGradient(listOf(BluePrimary, NeonGreen)),
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "Habit Health",
                    value = "$avgHabitConsistency%",
                    subtitle = "30-day rhythm",
                    icon = Icons.Default.LocalFireDepartment,
                    gradient = Brush.linearGradient(listOf(NeonAmber, NeonPink)),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Weekly Activity Chart
        item {
            WeeklyBarChart(weeklyRates = state.weeklyProgress)
        }

        // Breakdown Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "Execution Breakdown",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Goals
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Goals Completed", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                        Text("$completedGoals / $totalGoals ($goalRate%)", fontWeight = FontWeight.Bold, color = PurplePrimary)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { goalRate / 100f },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = PurplePrimary,
                        trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Tasks
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Tasks Done", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                        Text("$completedTasks / $totalTasks ($taskRate%)", fontWeight = FontWeight.Bold, color = BluePrimary)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { taskRate / 100f },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = BluePrimary,
                        trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // High Priority
                    val highRate = if (highPriorityTasks.isNotEmpty()) ((highDone.toFloat() / highPriorityTasks.size) * 100).toInt() else 0
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("High Priority Tasks", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                        Text("$highDone / ${highPriorityTasks.size} ($highRate%)", fontWeight = FontWeight.Bold, color = NeonPink)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { highRate / 100f },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = NeonPink,
                        trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                }
            }
        }

        // Achievements & Badges Showcase
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Achievements & Badges",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$unlockedBadgesCount / ${state.badges.size} Unlocked",
                    style = MaterialTheme.typography.labelMedium,
                    color = PurplePrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        items(state.badges.size) { index ->
            val badge = state.badges[index]
            BadgeCard(badge = badge)
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun BadgeCard(badge: BadgeEntity) {
    val shape = RoundedCornerShape(16.dp)

    val iconVector = when (badge.iconName) {
        "Flag" -> Icons.Default.Flag
        "EmojiEvents" -> Icons.Default.EmojiEvents
        "CheckCircle" -> Icons.Default.CheckCircle
        "LocalFireDepartment" -> Icons.Default.LocalFireDepartment
        "Assignment" -> Icons.Default.Assignment
        "Bolt" -> Icons.Default.Bolt
        else -> Icons.Default.Star
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(
                1.dp,
                if (badge.isUnlocked) Color(0xFFFFD700).copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline,
                shape
            )
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        if (badge.isUnlocked) GoldenBadgeGradient else Brush.linearGradient(
                            listOf(Color.DarkGray, Color.Gray)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (badge.isUnlocked) iconVector else Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (badge.isUnlocked) Color.Black else Color.LightGray,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = badge.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    if (badge.isUnlocked) {
                        Text(
                            text = "UNLOCKED",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFFFB800)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = badge.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
