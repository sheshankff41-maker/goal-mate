package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.GoalWithSubTasks
import com.example.data.model.TaskEntity
import com.example.ui.components.AddEditGoalDialog
import com.example.ui.components.AddEditReminderDialog
import com.example.ui.components.AddEditTaskDialog
import com.example.ui.components.AddHabitDialog
import com.example.ui.components.CelebrationDialog
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.CalendarScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.GoalsScreen
import com.example.ui.screens.HabitsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.PurplePrimary

@Composable
fun MainScreen(
    viewModel: GoalMateViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Dialog state holders
    var showAddGoalDialog by remember { mutableStateOf(false) }
    var editingGoal by remember { mutableStateOf<GoalWithSubTasks?>(null) }
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<TaskEntity?>(null) }
    var initialDateForNewTask by remember { mutableStateOf<String?>(null) }
    var showAddReminderDialog by remember { mutableStateOf(false) }
    var showAddHabitDialog by remember { mutableStateOf(false) }

    // Display snackbar messages
    LaunchedEffect(state.snackbarMessage) {
        state.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    // Android predictive / system BackHandler: Return to Dashboard if on another tab
    if (state.selectedTab != NavTab.DASHBOARD) {
        BackHandler {
            viewModel.selectTab(NavTab.DASHBOARD)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                NavTab.values().forEach { tab ->
                    val isSelected = state.selectedTab == tab
                    val tabIcon = when (tab) {
                        NavTab.DASHBOARD -> Icons.Default.Dashboard
                        NavTab.GOALS -> Icons.Default.TrackChanges
                        NavTab.TASKS -> Icons.Default.Checklist
                        NavTab.HABITS -> Icons.Default.Loop
                        NavTab.CALENDAR -> Icons.Default.CalendarMonth
                        NavTab.ANALYTICS -> Icons.Default.BarChart
                        NavTab.PROFILE -> Icons.Default.Person
                    }

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.selectTab(tab) },
                        icon = {
                            Icon(
                                imageVector = tabIcon,
                                contentDescription = tab.title,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = PurplePrimary,
                            indicatorColor = PurplePrimary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (state.selectedTab) {
                NavTab.DASHBOARD -> DashboardScreen(
                    state = state,
                    onNavigateTab = { viewModel.selectTab(it) },
                    onToggleTask = { viewModel.toggleTask(it) },
                    onToggleReminder = { viewModel.toggleReminder(it) },
                    onSnoozeReminder = { viewModel.snoozeReminder(it) },
                    onRefreshQuote = { viewModel.refreshQuote() },
                    onOpenAddGoal = { showAddGoalDialog = true },
                    onOpenAddTask = {
                        initialDateForNewTask = null
                        showAddTaskDialog = true
                    }
                )

                NavTab.GOALS -> GoalsScreen(
                    state = state,
                    onToggleSubTask = { subTask, goalWithSubTasks ->
                        viewModel.toggleSubTask(subTask, goalWithSubTasks)
                    },
                    onToggleGoalCompleted = { goalWithSubTasks ->
                        viewModel.toggleGoalCompletion(goalWithSubTasks)
                    },
                    onDeleteGoal = { id -> viewModel.deleteGoal(id) },
                    onOpenAddGoal = { showAddGoalDialog = true },
                    onOpenEditGoal = { goalWithSubTasks -> editingGoal = goalWithSubTasks }
                )

                NavTab.TASKS -> TasksScreen(
                    state = state,
                    onToggleTask = { viewModel.toggleTask(it) },
                    onDeleteTask = { id -> viewModel.deleteTask(id) },
                    onOpenAddTask = {
                        initialDateForNewTask = null
                        showAddTaskDialog = true
                    },
                    onOpenEditTask = { task -> editingTask = task }
                )

                NavTab.HABITS -> HabitsScreen(
                    state = state,
                    onToggleHabit = { habitId, date, currentlyCompleted ->
                        viewModel.toggleHabit(habitId, date, currentlyCompleted)
                    },
                    onDeleteHabit = { id -> viewModel.deleteHabit(id) },
                    onOpenAddHabit = { showAddHabitDialog = true }
                )

                NavTab.CALENDAR -> CalendarScreen(
                    state = state,
                    onToggleTask = { viewModel.toggleTask(it) },
                    onToggleReminder = { viewModel.toggleReminder(it) },
                    onOpenAddTaskForDate = { dateStr ->
                        initialDateForNewTask = dateStr
                        showAddTaskDialog = true
                    }
                )

                NavTab.ANALYTICS -> AnalyticsScreen(
                    state = state
                )

                NavTab.PROFILE -> ProfileScreen(
                    state = state,
                    onUpdateProfile = { name, motto, theme, notifs, sound ->
                        viewModel.updateProfile(name, motto, theme, notifs, sound)
                    },
                    onSeedSampleData = { viewModel.seedSampleData() },
                    onResetAllData = { viewModel.resetAllData() },
                    onExportData = { onReady -> viewModel.exportData(onReady) }
                )
            }
        }
    }

    // Goal Creation / Edit Dialog
    if (showAddGoalDialog || editingGoal != null) {
        AddEditGoalDialog(
            initialGoal = editingGoal,
            onDismiss = {
                showAddGoalDialog = false
                editingGoal = null
            },
            onSave = { title, desc, cat, prio, start, deadline, subtasks ->
                viewModel.saveGoal(
                    id = editingGoal?.goal?.id ?: 0L,
                    title = title,
                    description = desc,
                    category = cat,
                    priority = prio,
                    startDate = start,
                    deadline = deadline,
                    subtaskTitles = subtasks
                )
            }
        )
    }

    // Task Creation / Edit Dialog
    if (showAddTaskDialog || editingTask != null) {
        AddEditTaskDialog(
            initialTask = editingTask,
            onDismiss = {
                showAddTaskDialog = false
                editingTask = null
                initialDateForNewTask = null
            },
            onSave = { title, desc, dueDate, dueTime, priority ->
                val finalDueDate = initialDateForNewTask ?: dueDate
                viewModel.saveTask(
                    id = editingTask?.id ?: 0L,
                    title = title,
                    description = desc,
                    dueDate = finalDueDate,
                    dueTime = dueTime,
                    priority = priority
                )
            }
        )
    }

    // Reminder Dialog
    if (showAddReminderDialog) {
        AddEditReminderDialog(
            onDismiss = { showAddReminderDialog = false },
            onSave = { title, desc, triggerTime, recurrence ->
                viewModel.saveReminder(
                    title = title,
                    description = desc,
                    triggerTime = triggerTime,
                    recurrence = recurrence
                )
            }
        )
    }

    // Habit Dialog
    if (showAddHabitDialog) {
        AddHabitDialog(
            onDismiss = { showAddHabitDialog = false },
            onSave = { name, category, colorHex ->
                viewModel.saveHabit(
                    name = name,
                    category = category,
                    colorHex = colorHex
                )
            }
        )
    }

    // Goal Completion Celebration Popup
    state.celebrationGoal?.let { goalWithSubTasks ->
        CelebrationDialog(
            goalWithSubTasks = goalWithSubTasks,
            onDismiss = { viewModel.dismissCelebration() }
        )
    }
}
