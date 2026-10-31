package com.example.data.repository

import com.example.data.db.GoalMateDatabase
import com.example.data.model.BadgeEntity
import com.example.data.model.GoalEntity
import com.example.data.model.GoalWithSubTasks
import com.example.data.model.HabitEntity
import com.example.data.model.HabitLogEntity
import com.example.data.model.ReminderEntity
import com.example.data.model.SubTaskEntity
import com.example.data.model.TaskEntity
import com.example.data.model.UserProfileEntity
import com.example.util.DateUtils
import com.example.util.SampleData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import org.json.JSONArray
import org.json.JSONObject

class GoalMateRepository(private val database: GoalMateDatabase) {
    private val goalDao = database.goalDao()
    private val taskDao = database.taskDao()
    private val reminderDao = database.reminderDao()
    private val habitDao = database.habitDao()
    private val userProfileDao = database.userProfileDao()
    private val badgeDao = database.badgeDao()

    // Goals & SubTasks
    val allGoals: Flow<List<GoalWithSubTasks>> = goalDao.getAllGoalsWithSubTasks()

    suspend fun saveGoal(goal: GoalEntity, subTaskTitles: List<String>): Long {
        val goalId = if (goal.id == 0L) {
            goalDao.insertGoal(goal)
        } else {
            goalDao.updateGoal(goal)
            goal.id
        }

        // Check badge for first goal
        badgeDao.unlockBadge("first_step", System.currentTimeMillis())

        if (subTaskTitles.isNotEmpty()) {
            if (goal.id != 0L) {
                // Delete existing if editing
                goalDao.deleteSubTasksByGoalId(goalId)
            }
            val subTasks = subTaskTitles.mapIndexed { index, title ->
                SubTaskEntity(
                    goalId = goalId,
                    title = title,
                    isCompleted = false,
                    orderIndex = index
                )
            }
            goalDao.insertSubTasks(subTasks)
            badgeDao.unlockBadge("master_planner", System.currentTimeMillis())
        }

        return goalId
    }

    suspend fun updateGoal(goal: GoalEntity) {
        goalDao.updateGoal(goal)
        if (goal.isCompleted) {
            badgeDao.unlockBadge("goal_crusher", System.currentTimeMillis())
        }
    }

    suspend fun deleteGoal(goalId: Long) {
        goalDao.deleteSubTasksByGoalId(goalId)
        goalDao.deleteGoalById(goalId)
    }

    suspend fun toggleSubTask(subTask: SubTaskEntity, goalWithSubTasks: GoalWithSubTasks) {
        val updatedSubTask = subTask.copy(isCompleted = !subTask.isCompleted)
        goalDao.updateSubTask(updatedSubTask)

        // Recompute goal progress
        val allSubtasks = goalWithSubTasks.subTasks.map {
            if (it.id == subTask.id) updatedSubTask else it
        }
        val completedCount = allSubtasks.count { it.isCompleted }
        val newProgress = if (allSubtasks.isEmpty()) 0 else ((completedCount.toFloat() / allSubtasks.size) * 100).toInt()
        val isCompleted = newProgress == 100
        goalDao.updateGoal(
            goalWithSubTasks.goal.copy(
                progress = newProgress,
                isCompleted = isCompleted,
                completedAt = if (isCompleted) System.currentTimeMillis() else null
            )
        )
        if (isCompleted) {
            badgeDao.unlockBadge("goal_crusher", System.currentTimeMillis())
        }
    }

    suspend fun setGoalCompleted(goalWithSubTasks: GoalWithSubTasks, completed: Boolean) {
        val now = if (completed) System.currentTimeMillis() else null
        goalDao.updateGoal(
            goalWithSubTasks.goal.copy(
                isCompleted = completed,
                progress = if (completed) 100 else goalWithSubTasks.goal.progress,
                completedAt = now
            )
        )
        // Also update subtasks to match if completed
        if (completed) {
            goalWithSubTasks.subTasks.forEach {
                goalDao.updateSubTask(it.copy(isCompleted = true))
            }
            badgeDao.unlockBadge("goal_crusher", System.currentTimeMillis())
        }
    }

    // Tasks
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()

    suspend fun insertTask(task: TaskEntity): Long {
        return taskDao.insertTask(task)
    }

    suspend fun updateTask(task: TaskEntity) {
        taskDao.updateTask(task)
    }

    suspend fun toggleTaskCompleted(task: TaskEntity) {
        val completed = !task.isCompleted
        val updated = task.copy(
            isCompleted = completed,
            completedAt = if (completed) System.currentTimeMillis() else null
        )
        taskDao.updateTask(updated)
        if (completed) {
            badgeDao.unlockBadge("task_master", System.currentTimeMillis())
        }
    }

    suspend fun deleteTask(taskId: Long) {
        taskDao.deleteTaskById(taskId)
    }

    // Reminders
    val allReminders: Flow<List<ReminderEntity>> = reminderDao.getAllReminders()
    val activeReminders: Flow<List<ReminderEntity>> = reminderDao.getActiveReminders()

    suspend fun insertReminder(reminder: ReminderEntity): Long {
        return reminderDao.insertReminder(reminder)
    }

    suspend fun updateReminder(reminder: ReminderEntity) {
        reminderDao.updateReminder(reminder)
    }

    suspend fun deleteReminder(reminderId: Long) {
        reminderDao.deleteReminderById(reminderId)
    }

    suspend fun toggleReminderCompleted(reminder: ReminderEntity) {
        reminderDao.updateReminder(reminder.copy(isCompleted = !reminder.isCompleted))
    }

    suspend fun snoozeReminder(reminder: ReminderEntity, minutes: Int = 10) {
        val snoozeTime = System.currentTimeMillis() + minutes * 60 * 1000L
        reminderDao.updateReminder(reminder.copy(isSnoozed = true, snoozeUntil = snoozeTime))
    }

    // Habits
    val allHabits: Flow<List<HabitEntity>> = habitDao.getAllHabits()
    val allHabitLogs: Flow<List<HabitLogEntity>> = habitDao.getAllHabitLogs()

    suspend fun insertHabit(habit: HabitEntity): Long {
        return habitDao.insertHabit(habit)
    }

    suspend fun updateHabit(habit: HabitEntity) {
        habitDao.updateHabit(habit)
    }

    suspend fun deleteHabit(habitId: Long) {
        habitDao.deleteHabitById(habitId)
    }

    suspend fun toggleHabitLog(habitId: Long, date: String, currentlyCompleted: Boolean) {
        if (currentlyCompleted) {
            habitDao.deleteHabitLog(habitId, date)
        } else {
            habitDao.insertHabitLog(HabitLogEntity(habitId = habitId, date = date))
        }
    }

    // Badges & Achievements
    val allBadges: Flow<List<BadgeEntity>> = badgeDao.getAllBadges()

    suspend fun unlockBadge(badgeId: String) {
        badgeDao.unlockBadge(badgeId, System.currentTimeMillis())
    }

    // User Profile
    val userProfile: Flow<UserProfileEntity?> = userProfileDao.getUserProfile()

    suspend fun saveUserProfile(profile: UserProfileEntity) {
        userProfileDao.upsertUserProfile(profile)
    }

    // Initial Seeding
    suspend fun checkAndSeedInitialData() {
        val existingProfile = userProfileDao.getUserProfileDirect()
        if (existingProfile == null) {
            seedDemoData()
        }
    }

    suspend fun seedDemoData() {
        // Badges
        badgeDao.insertBadges(SampleData.initialBadges)

        // Profile
        userProfileDao.upsertUserProfile(SampleData.initialProfile)

        // Goals & subtasks
        val sampleGoals = SampleData.getSampleGoals()
        sampleGoals.forEach { (goal, subtasks) ->
            val gid = goalDao.insertGoal(goal)
            val subtasksWithGid = subtasks.map { it.copy(goalId = gid) }
            goalDao.insertSubTasks(subtasksWithGid)
        }

        // Daily tasks
        SampleData.getSampleTasks().forEach {
            taskDao.insertTask(it)
        }

        // Reminders
        SampleData.getSampleReminders().forEach {
            reminderDao.insertReminder(it)
        }

        // Habits & logs
        val sampleHabits = SampleData.getSampleHabits()
        sampleHabits.forEach { (habit, logs) ->
            val hid = habitDao.insertHabit(habit)
            logs.forEach { log ->
                habitDao.insertHabitLog(log.copy(habitId = hid))
            }
        }
    }

    suspend fun resetAllData() {
        goalDao.clearAllGoals()
        goalDao.clearAllSubTasks()
        taskDao.clearAllTasks()
        reminderDao.clearAllReminders()
        habitDao.clearAllHabits()
        habitDao.clearAllHabitLogs()
        badgeDao.resetBadges()
        userProfileDao.clearUserProfile()
        userProfileDao.upsertUserProfile(SampleData.initialProfile)
    }

    suspend fun exportDataAsJson(): String {
        val root = JSONObject()

        // Profile
        val profile = userProfileDao.getUserProfileDirect() ?: SampleData.initialProfile
        root.put("user", JSONObject().apply {
            put("name", profile.name)
            put("motto", profile.motto)
            put("streak", profile.currentStreak)
        })

        // Goals
        val goals = goalDao.getAllGoalsWithSubTasks().firstOrNull() ?: emptyList()
        val goalsArray = JSONArray()
        goals.forEach { g ->
            val gObj = JSONObject().apply {
                put("id", g.goal.id)
                put("title", g.goal.title)
                put("category", g.goal.category.name)
                put("priority", g.goal.priority.name)
                put("progress", g.calculatedProgress)
                put("isCompleted", g.goal.isCompleted)
                val subArray = JSONArray()
                g.subTasks.forEach { st ->
                    subArray.put(JSONObject().apply {
                        put("title", st.title)
                        put("completed", st.isCompleted)
                    })
                }
                put("subtasks", subArray)
            }
            goalsArray.put(gObj)
        }
        root.put("goals", goalsArray)

        // Tasks
        val tasks = taskDao.getAllTasks().firstOrNull() ?: emptyList()
        val tasksArray = JSONArray()
        tasks.forEach { t ->
            tasksArray.put(JSONObject().apply {
                put("title", t.title)
                put("dueDate", t.dueDate)
                put("priority", t.priority.name)
                put("isCompleted", t.isCompleted)
            })
        }
        root.put("tasks", tasksArray)

        // Habits
        val habits = habitDao.getAllHabits().firstOrNull() ?: emptyList()
        val habitsArray = JSONArray()
        habits.forEach { h ->
            habitsArray.put(JSONObject().apply {
                put("name", h.name)
                put("category", h.category)
            })
        }
        root.put("habits", habitsArray)

        root.put("exportTimestamp", System.currentTimeMillis())
        root.put("exportDate", DateUtils.todayDateString())

        return root.toString(2)
    }
}
