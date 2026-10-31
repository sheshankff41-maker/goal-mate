package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.GoalMateApp
import com.example.data.model.BadgeEntity
import com.example.data.model.GoalCategory
import com.example.data.model.GoalEntity
import com.example.data.model.GoalWithSubTasks
import com.example.data.model.HabitEntity
import com.example.data.model.HabitLogEntity
import com.example.data.model.HabitWithStats
import com.example.data.model.Priority
import com.example.data.model.Recurrence
import com.example.data.model.ReminderEntity
import com.example.data.model.SubTaskEntity
import com.example.data.model.TaskEntity
import com.example.data.model.ThemeMode
import com.example.data.model.UserProfileEntity
import com.example.reminder.ReminderScheduler
import com.example.util.DateUtils
import com.example.util.MotivationalQuote
import com.example.util.QuotesProvider
import com.example.util.SampleData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class NavTab(val title: String, val iconName: String) {
    DASHBOARD("Dashboard", "Dashboard"),
    GOALS("Goals", "TrackChanges"),
    TASKS("Tasks", "Checklist"),
    HABITS("Habits", "Loop"),
    CALENDAR("Calendar", "CalendarMonth"),
    ANALYTICS("Analytics", "BarChart"),
    PROFILE("Profile", "Person")
}

data class GoalMateUiState(
    val goals: List<GoalWithSubTasks> = emptyList(),
    val tasks: List<TaskEntity> = emptyList(),
    val reminders: List<ReminderEntity> = emptyList(),
    val habitsWithStats: List<HabitWithStats> = emptyList(),
    val userProfile: UserProfileEntity = SampleData.initialProfile,
    val badges: List<BadgeEntity> = emptyList(),
    val selectedTab: NavTab = NavTab.DASHBOARD,
    val dailyProductivity: Int = 0, // 0 - 100
    val weeklyProgress: List<Float> = emptyList(), // 7 days of percentages
    val dailyQuote: MotivationalQuote = QuotesProvider.quotes.first(),
    val celebrationGoal: GoalWithSubTasks? = null,
    val snackbarMessage: String? = null
)

class GoalMateViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = (application as GoalMateApp).repository

    private val _selectedTab = MutableStateFlow(NavTab.DASHBOARD)
    private val _dailyQuote = MutableStateFlow(QuotesProvider.getDailyQuote(DateUtils.todayDateString()))
    private val _celebrationGoal = MutableStateFlow<GoalWithSubTasks?>(null)
    private val _snackbarMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<GoalMateUiState> = combine(
        repository.allGoals,
        repository.allTasks,
        repository.allReminders,
        repository.allHabits,
        repository.allHabitLogs,
        repository.userProfile,
        repository.allBadges,
        _selectedTab,
        _dailyQuote,
        _celebrationGoal,
        _snackbarMessage
    ) { args ->
        @Suppress("UNCHECKED_CAST")
        val goals = args[0] as List<GoalWithSubTasks>
        @Suppress("UNCHECKED_CAST")
        val tasks = args[1] as List<TaskEntity>
        @Suppress("UNCHECKED_CAST")
        val reminders = args[2] as List<ReminderEntity>
        @Suppress("UNCHECKED_CAST")
        val habits = args[3] as List<HabitEntity>
        @Suppress("UNCHECKED_CAST")
        val habitLogs = args[4] as List<HabitLogEntity>
        val profile = (args[5] as? UserProfileEntity) ?: SampleData.initialProfile
        @Suppress("UNCHECKED_CAST")
        val badges = args[6] as List<BadgeEntity>
        val tab = args[7] as NavTab
        val quote = args[8] as MotivationalQuote
        val celebration = args[9] as? GoalWithSubTasks
        val snackbar = args[10] as? String

        val today = DateUtils.todayDateString()

        // Compute habit stats
        val logsByHabit = habitLogs.groupBy { it.habitId }
        val habitsWithStats = habits.map { habit ->
            val logs = logsByHabit[habit.id]?.map { it.date }?.toSet() ?: emptySet()
            val (currentStreak, bestStreak) = calculateStreaks(logs)
            val past30Days = DateUtils.getLastNDays(30)
            val completed30 = past30Days.count { logs.contains(it) }
            val rate30 = completed30.toFloat() / 30f
            HabitWithStats(
                habit = habit,
                completedDates = logs,
                currentStreak = currentStreak,
                bestStreak = bestStreak,
                completionRate30Days = rate30
            )
        }

        // Compute today's productivity percentage
        val todayTasks = tasks.filter { it.dueDate == today }
        val completedTodayTasks = todayTasks.count { it.isCompleted }

        val todayHabitsCount = habits.size
        val completedTodayHabits = habitsWithStats.count { it.completedDates.contains(today) }

        val totalTodayItems = todayTasks.size + todayHabitsCount
        val completedTodayItems = completedTodayTasks + completedTodayHabits

        val dailyProductivity = if (totalTodayItems > 0) {
            ((completedTodayItems.toFloat() / totalTodayItems) * 100).toInt()
        } else {
            0
        }

        // Compute weekly progress
        val last7Days = DateUtils.getLastNDays(7)
        val weeklyProgress = last7Days.map { day ->
            val dayTasks = tasks.filter { it.dueDate == day }
            val doneTasks = dayTasks.count { it.isCompleted }
            val doneHabits = habitLogs.count { it.date == day }
            val dayTotal = (dayTasks.size + habits.size).coerceAtLeast(1)
            val dayDone = doneTasks + doneHabits
            (dayDone.toFloat() / dayTotal).coerceIn(0f, 1f)
        }

        GoalMateUiState(
            goals = goals,
            tasks = tasks,
            reminders = reminders,
            habitsWithStats = habitsWithStats,
            userProfile = profile,
            badges = badges,
            selectedTab = tab,
            dailyProductivity = dailyProductivity,
            weeklyProgress = weeklyProgress,
            dailyQuote = quote,
            celebrationGoal = celebration,
            snackbarMessage = snackbar
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GoalMateUiState()
    )

    fun selectTab(tab: NavTab) {
        _selectedTab.value = tab
    }

    fun refreshQuote() {
        _dailyQuote.value = QuotesProvider.getRandomQuote()
    }

    fun dismissCelebration() {
        _celebrationGoal.value = null
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    // --- GOAL ACTIONS ---
    fun saveGoal(
        id: Long = 0,
        title: String,
        description: String,
        category: GoalCategory,
        priority: Priority,
        startDate: Long,
        deadline: Long,
        subtaskTitles: List<String>
    ) {
        viewModelScope.launch {
            val goal = GoalEntity(
                id = id,
                title = title.trim(),
                description = description.trim(),
                category = category,
                priority = priority,
                startDate = startDate,
                deadline = deadline,
                progress = 0,
                isCompleted = false
            )
            repository.saveGoal(goal, subtaskTitles.filter { it.isNotBlank() })
            _snackbarMessage.value = if (id == 0L) "Goal created successfully!" else "Goal updated!"
        }
    }

    fun toggleSubTask(subTask: SubTaskEntity, goalWithSubTasks: GoalWithSubTasks) {
        viewModelScope.launch {
            repository.toggleSubTask(subTask, goalWithSubTasks)
        }
    }

    fun toggleGoalCompletion(goalWithSubTasks: GoalWithSubTasks) {
        viewModelScope.launch {
            val willBeCompleted = !goalWithSubTasks.goal.isCompleted
            repository.setGoalCompleted(goalWithSubTasks, willBeCompleted)
            if (willBeCompleted) {
                _celebrationGoal.value = goalWithSubTasks
            }
        }
    }

    fun deleteGoal(goalId: Long) {
        viewModelScope.launch {
            repository.deleteGoal(goalId)
            _snackbarMessage.value = "Goal deleted"
        }
    }

    // --- TASK ACTIONS ---
    fun saveTask(
        id: Long = 0,
        title: String,
        description: String = "",
        dueDate: String,
        dueTime: String = "12:00",
        priority: Priority = Priority.MEDIUM,
        goalId: Long? = null
    ) {
        viewModelScope.launch {
            val task = TaskEntity(
                id = id,
                title = title.trim(),
                description = description.trim(),
                dueDate = dueDate,
                dueTime = dueTime,
                priority = priority,
                goalId = goalId
            )
            if (id == 0L) {
                repository.insertTask(task)
                _snackbarMessage.value = "Task created!"
            } else {
                repository.updateTask(task)
                _snackbarMessage.value = "Task updated!"
            }
        }
    }

    fun toggleTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.toggleTaskCompleted(task)
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
            _snackbarMessage.value = "Task deleted"
        }
    }

    // --- REMINDER ACTIONS ---
    fun saveReminder(
        id: Long = 0,
        title: String,
        description: String = "",
        triggerTime: Long,
        recurrence: Recurrence = Recurrence.NONE
    ) {
        viewModelScope.launch {
            val reminder = ReminderEntity(
                id = id,
                title = title.trim(),
                description = description.trim(),
                triggerTime = triggerTime,
                recurrence = recurrence,
                isCompleted = false
            )
            val assignedId = if (id == 0L) {
                repository.insertReminder(reminder)
            } else {
                repository.updateReminder(reminder)
                id
            }
            // Schedule Alarm
            ReminderScheduler.schedule(getApplication(), reminder.copy(id = assignedId))
            _snackbarMessage.value = "Reminder scheduled!"
        }
    }

    fun toggleReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            repository.toggleReminderCompleted(reminder)
        }
    }

    fun snoozeReminder(reminder: ReminderEntity, minutes: Int = 10) {
        viewModelScope.launch {
            repository.snoozeReminder(reminder, minutes)
            ReminderScheduler.schedule(getApplication(), reminder.copy(snoozeUntil = System.currentTimeMillis() + minutes * 60 * 1000L))
            _snackbarMessage.value = "Snoozed for $minutes minutes"
        }
    }

    fun deleteReminder(reminderId: Long) {
        viewModelScope.launch {
            ReminderScheduler.cancel(getApplication(), reminderId)
            repository.deleteReminder(reminderId)
            _snackbarMessage.value = "Reminder removed"
        }
    }

    // --- HABIT ACTIONS ---
    fun saveHabit(
        id: Long = 0,
        name: String,
        category: String = "General",
        iconName: String = "CheckCircle",
        colorHex: String = "#9061F9"
    ) {
        viewModelScope.launch {
            val habit = HabitEntity(
                id = id,
                name = name.trim(),
                category = category.trim(),
                iconName = iconName,
                colorHex = colorHex
            )
            if (id == 0L) {
                repository.insertHabit(habit)
                _snackbarMessage.value = "Habit added!"
            } else {
                repository.updateHabit(habit)
                _snackbarMessage.value = "Habit updated!"
            }
        }
    }

    fun toggleHabit(habitId: Long, date: String, currentlyCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleHabitLog(habitId, date, currentlyCompleted)
        }
    }

    fun deleteHabit(habitId: Long) {
        viewModelScope.launch {
            repository.deleteHabit(habitId)
            _snackbarMessage.value = "Habit deleted"
        }
    }

    // --- PROFILE & SETTINGS ACTIONS ---
    fun updateProfile(
        name: String,
        motto: String,
        themeMode: ThemeMode,
        notificationsEnabled: Boolean,
        soundEnabled: Boolean
    ) {
        viewModelScope.launch {
            val current = uiState.value.userProfile
            val updated = current.copy(
                name = name.trim(),
                motto = motto.trim(),
                themeMode = themeMode,
                notificationsEnabled = notificationsEnabled,
                soundEnabled = soundEnabled
            )
            repository.saveUserProfile(updated)
            _snackbarMessage.value = "Profile updated!"
        }
    }

    fun seedSampleData() {
        viewModelScope.launch {
            repository.seedDemoData()
            _snackbarMessage.value = "Demo goals, tasks, and habits loaded!"
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            repository.resetAllData()
            _snackbarMessage.value = "All data reset to clean slate"
        }
    }

    fun exportData(onReady: (String) -> Unit) {
        viewModelScope.launch {
            val json = repository.exportDataAsJson()
            onReady(json)
        }
    }

    private fun calculateStreaks(completedDates: Set<String>): Pair<Int, Int> {
        if (completedDates.isEmpty()) return Pair(0, 0)
        var currentStreak = 0
        var bestStreak = 0
        var tempStreak = 0

        val today = DateUtils.todayDateString()
        val yesterday = DateUtils.offsetDate(-1)

        // Check current streak starting from today or yesterday
        var checkDate = if (completedDates.contains(today)) today else if (completedDates.contains(yesterday)) yesterday else null
        while (checkDate != null && completedDates.contains(checkDate)) {
            currentStreak++
            checkDate = DateUtils.offsetDate(-currentStreak)
        }

        // Calculate best streak over past 60 days
        val pastDays = DateUtils.getLastNDays(60)
        for (day in pastDays) {
            if (completedDates.contains(day)) {
                tempStreak++
                if (tempStreak > bestStreak) bestStreak = tempStreak
            } else {
                tempStreak = 0
            }
        }
        if (currentStreak > bestStreak) bestStreak = currentStreak

        return Pair(currentStreak, bestStreak)
    }
}
