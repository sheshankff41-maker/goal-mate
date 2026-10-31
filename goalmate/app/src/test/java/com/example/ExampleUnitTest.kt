package com.example

import com.example.data.model.GoalCategory
import com.example.data.model.GoalEntity
import com.example.data.model.GoalWithSubTasks
import com.example.data.model.Priority
import com.example.data.model.SubTaskEntity
import com.example.util.DateUtils
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testGoalCalculatedProgressWithSubTasks() {
        val goal = GoalEntity(
            id = 1,
            title = "Test Goal",
            deadline = System.currentTimeMillis() + 86400000L * 10
        )
        val subtasks = listOf(
            SubTaskEntity(id = 1, goalId = 1, title = "Subtask 1", isCompleted = true),
            SubTaskEntity(id = 2, goalId = 1, title = "Subtask 2", isCompleted = true),
            SubTaskEntity(id = 3, goalId = 1, title = "Subtask 3", isCompleted = false),
            SubTaskEntity(id = 4, goalId = 1, title = "Subtask 4", isCompleted = false)
        )
        val goalWithSubTasks = GoalWithSubTasks(goal = goal, subTasks = subtasks)
        assertEquals(50, goalWithSubTasks.calculatedProgress)
    }

    @Test
    fun testGoalCompletedAlways100Percent() {
        val goal = GoalEntity(
            id = 2,
            title = "Completed Goal",
            deadline = System.currentTimeMillis() + 86400000L * 10,
            isCompleted = true
        )
        val subtasks = listOf(
            SubTaskEntity(id = 5, goalId = 2, title = "Subtask", isCompleted = false)
        )
        val goalWithSubTasks = GoalWithSubTasks(goal = goal, subTasks = subtasks)
        assertEquals(100, goalWithSubTasks.calculatedProgress)
    }

    @Test
    fun testDateFormatting() {
        val today = DateUtils.todayDateString()
        assertEquals(10, today.length) // YYYY-MM-DD
    }
}
