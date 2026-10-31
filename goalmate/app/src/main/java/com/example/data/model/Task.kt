package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TaskFilter(val label: String) {
    ALL("All"),
    TODAY("Today"),
    TOMORROW("Tomorrow"),
    UPCOMING("Upcoming"),
    OVERDUE("Overdue")
}

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val dueDate: String, // Format: YYYY-MM-DD
    val dueTime: String = "12:00", // Format: HH:mm
    val priority: Priority = Priority.MEDIUM,
    val isCompleted: Boolean = false,
    val goalId: Long? = null,
    val completedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
