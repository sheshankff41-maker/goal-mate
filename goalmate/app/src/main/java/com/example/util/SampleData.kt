package com.example.util

import com.example.data.model.BadgeEntity
import com.example.data.model.GoalCategory
import com.example.data.model.GoalEntity
import com.example.data.model.HabitEntity
import com.example.data.model.HabitLogEntity
import com.example.data.model.Priority
import com.example.data.model.Recurrence
import com.example.data.model.ReminderEntity
import com.example.data.model.SubTaskEntity
import com.example.data.model.TaskEntity
import com.example.data.model.UserProfileEntity

object SampleData {
    val initialBadges = listOf(
        BadgeEntity(
            id = "first_step",
            title = "First Step",
            description = "Created your first goal on GoalMate",
            iconName = "Flag",
            isUnlocked = true,
            unlockedAt = System.currentTimeMillis() - 86400000L * 3
        ),
        BadgeEntity(
            id = "goal_crusher",
            title = "Goal Crusher",
            description = "Successfully marked a goal as 100% completed",
            iconName = "EmojiEvents",
            isUnlocked = true,
            unlockedAt = System.currentTimeMillis() - 86400000L
        ),
        BadgeEntity(
            id = "task_master",
            title = "Task Master",
            description = "Completed 5 or more daily tasks",
            iconName = "CheckCircle",
            isUnlocked = true,
            unlockedAt = System.currentTimeMillis() - 86400000L * 2
        ),
        BadgeEntity(
            id = "streak_champion",
            title = "Streak Champion",
            description = "Kept a 7-day continuous habit streak",
            iconName = "LocalFireDepartment",
            isUnlocked = false
        ),
        BadgeEntity(
            id = "master_planner",
            title = "Master Planner",
            description = "Added 3 detailed goals with subtasks",
            iconName = "Assignment",
            isUnlocked = true,
            unlockedAt = System.currentTimeMillis() - 86400000L * 2
        ),
        BadgeEntity(
            id = "zen_focus",
            title = "Laser Focus",
            description = "Achieved 100% daily productivity completion",
            iconName = "Bolt",
            isUnlocked = false
        )
    )

    val initialProfile = UserProfileEntity(
        id = 1,
        name = "Alex Rivera",
        motto = "Turn small daily efforts into massive lifelong achievements.",
        avatarChoice = "avatar_1",
        currentStreak = 5,
        bestStreak = 12,
        lastActiveDate = DateUtils.todayDateString()
    )

    fun getSampleGoals(): List<Pair<GoalEntity, List<SubTaskEntity>>> {
        val now = System.currentTimeMillis()
        val oneDay = 86400000L

        val goal1 = GoalEntity(
            id = 1,
            title = "Launch Android App Portfolio",
            description = "Build and release 3 production-grade Kotlin Jetpack Compose apps to Google Play.",
            category = GoalCategory.CAREER,
            priority = Priority.HIGH,
            startDate = now - oneDay * 14,
            deadline = now + oneDay * 30,
            progress = 66,
            isCompleted = false
        )
        val subtasks1 = listOf(
            SubTaskEntity(id = 1, goalId = 1, title = "Design UI wireframes & M3 tokens", isCompleted = true, orderIndex = 0),
            SubTaskEntity(id = 2, goalId = 1, title = "Integrate Room database & Offline Sync", isCompleted = true, orderIndex = 1),
            SubTaskEntity(id = 3, goalId = 1, title = "Publish to Google Play Store Track", isCompleted = false, orderIndex = 2)
        )

        val goal2 = GoalEntity(
            id = 2,
            title = "Run 10K Marathon in Under 50 Mins",
            description = "Gradually build endurance with interval training and progressive cardio sessions.",
            category = GoalCategory.FITNESS,
            priority = Priority.MEDIUM,
            startDate = now - oneDay * 20,
            deadline = now + oneDay * 45,
            progress = 50,
            isCompleted = false
        )
        val subtasks2 = listOf(
            SubTaskEntity(id = 4, goalId = 2, title = "5K trial under 26 mins", isCompleted = true, orderIndex = 0),
            SubTaskEntity(id = 5, goalId = 2, title = "8K distance test", isCompleted = false, orderIndex = 1),
            SubTaskEntity(id = 6, goalId = 2, title = "10K race paced rehearsal", isCompleted = false, orderIndex = 2)
        )

        val goal3 = GoalEntity(
            id = 3,
            title = "Read 12 Books on Personal Growth",
            description = "Read 20 pages every single day to expand perspective, focus, and leadership.",
            category = GoalCategory.PERSONAL_DEV,
            priority = Priority.LOW,
            startDate = now - oneDay * 60,
            deadline = now - oneDay * 2,
            progress = 100,
            isCompleted = true,
            completedAt = now - oneDay * 2
        )
        val subtasks3 = listOf(
            SubTaskEntity(id = 7, goalId = 3, title = "Atomic Habits by James Clear", isCompleted = true, orderIndex = 0),
            SubTaskEntity(id = 8, goalId = 3, title = "Deep Work by Cal Newport", isCompleted = true, orderIndex = 1),
            SubTaskEntity(id = 9, goalId = 3, title = "Show Your Work by Austin Kleon", isCompleted = true, orderIndex = 2)
        )

        val goal4 = GoalEntity(
            id = 4,
            title = "Build $10,000 Emergency Fund",
            description = "Automate 20% of monthly income into high-yield savings account.",
            category = GoalCategory.FINANCE,
            priority = Priority.HIGH,
            startDate = now - oneDay * 30,
            deadline = now + oneDay * 90,
            progress = 40,
            isCompleted = false
        )
        val subtasks4 = listOf(
            SubTaskEntity(id = 10, goalId = 4, title = "Set up high yield savings deposit", isCompleted = true, orderIndex = 0),
            SubTaskEntity(id = 11, goalId = 4, title = "Audit unnecessary monthly subscriptions", isCompleted = true, orderIndex = 1),
            SubTaskEntity(id = 12, goalId = 4, title = "Reach $5,000 midpoint milestone", isCompleted = false, orderIndex = 2),
            SubTaskEntity(id = 13, goalId = 4, title = "Reach $10,000 safety cushion", isCompleted = false, orderIndex = 3)
        )

        return listOf(
            goal1 to subtasks1,
            goal2 to subtasks2,
            goal3 to subtasks3,
            goal4 to subtasks4
        )
    }

    fun getSampleTasks(): List<TaskEntity> {
        val today = DateUtils.todayDateString()
        val tomorrow = DateUtils.offsetDate(1)
        val yesterday = DateUtils.offsetDate(-1)

        return listOf(
            TaskEntity(
                id = 1,
                title = "Review Weekly Goal Progress",
                description = "Inspect milestone accomplishments and plan next sprint targets.",
                dueDate = today,
                dueTime = "09:30",
                priority = Priority.HIGH,
                isCompleted = true,
                goalId = 1
            ),
            TaskEntity(
                id = 2,
                title = "45-Minute Interval Jogging",
                description = "Keep heart rate in zone 3 with 5-minute cooldown.",
                dueDate = today,
                dueTime = "17:00",
                priority = Priority.MEDIUM,
                isCompleted = false,
                goalId = 2
            ),
            TaskEntity(
                id = 3,
                title = "Read 25 Pages of Deep Work",
                description = "Take notes on key concepts and actionable habit rules.",
                dueDate = today,
                dueTime = "21:00",
                priority = Priority.LOW,
                isCompleted = false,
                goalId = 3
            ),
            TaskEntity(
                id = 4,
                title = "Transfer $250 to Emergency Savings",
                description = "Bi-weekly automatic budget allocation.",
                dueDate = tomorrow,
                dueTime = "10:00",
                priority = Priority.HIGH,
                isCompleted = false,
                goalId = 4
            ),
            TaskEntity(
                id = 5,
                title = "Clean Up Applet Architecture Docs",
                description = "Document data models and viewmodel events.",
                dueDate = yesterday,
                dueTime = "15:00",
                priority = Priority.MEDIUM,
                isCompleted = true,
                goalId = 1
            )
        )
    }

    fun getSampleReminders(): List<ReminderEntity> {
        val now = System.currentTimeMillis()
        val oneHour = 3600000L

        return listOf(
            ReminderEntity(
                id = 1,
                title = "Hydrate & Stand Up",
                description = "Drink 500ml water and do a quick 3-minute stretch.",
                triggerTime = now + oneHour * 2,
                recurrence = Recurrence.DAILY,
                isCompleted = false
            ),
            ReminderEntity(
                id = 2,
                title = "Submit Sprint Milestone Report",
                description = "Send summary email to team leads with deliverables.",
                triggerTime = now + oneHour * 5,
                recurrence = Recurrence.NONE,
                isCompleted = false
            ),
            ReminderEntity(
                id = 3,
                title = "Evening Meditation & Reflection",
                description = "10 minutes unguided mindfulness before bedtime.",
                triggerTime = now + oneHour * 9,
                recurrence = Recurrence.DAILY,
                isCompleted = false
            )
        )
    }

    fun getSampleHabits(): List<Pair<HabitEntity, List<HabitLogEntity>>> {
        val habit1 = HabitEntity(
            id = 1,
            name = "Morning Hydration (1L)",
            category = "Health",
            iconName = "LocalDrink",
            colorHex = "#38BDF8"
        )
        val habit2 = HabitEntity(
            id = 2,
            name = "Read 20+ Pages",
            category = "Learning",
            iconName = "MenuBook",
            colorHex = "#9061F9"
        )
        val habit3 = HabitEntity(
            id = 3,
            name = "Workout / Cardio 30m",
            category = "Fitness",
            iconName = "DirectionsRun",
            colorHex = "#10B981"
        )
        val habit4 = HabitEntity(
            id = 4,
            name = "Mindful Meditation 10m",
            category = "Wellness",
            iconName = "SelfImprovement",
            colorHex = "#F59E0B"
        )

        // Seed logs for the past 6 days
        val last7Days = DateUtils.getLastNDays(7)
        val logs = mutableListOf<HabitLogEntity>()
        var logId = 1L

        // Habit 1: checked 6 of 7 days
        for (i in 0 until 6) {
            logs.add(HabitLogEntity(id = logId++, habitId = 1, date = last7Days[i]))
        }
        // Habit 2: checked 5 of 7 days
        for (i in 1 until 6) {
            logs.add(HabitLogEntity(id = logId++, habitId = 2, date = last7Days[i]))
        }
        // Habit 3: checked 4 of 7 days
        for (i in listOf(0, 2, 4, 6)) {
            logs.add(HabitLogEntity(id = logId++, habitId = 3, date = last7Days[i]))
        }
        // Habit 4: checked 5 of 7 days
        for (i in 0 until 5) {
            logs.add(HabitLogEntity(id = logId++, habitId = 4, date = last7Days[i]))
        }

        return listOf(
            habit1 to logs.filter { it.habitId == 1L },
            habit2 to logs.filter { it.habitId == 2L },
            habit3 to logs.filter { it.habitId == 3L },
            habit4 to logs.filter { it.habitId == 4L }
        )
    }
}
