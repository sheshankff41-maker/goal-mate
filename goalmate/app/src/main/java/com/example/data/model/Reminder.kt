package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Recurrence(val displayName: String) {
    NONE("One-time"),
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly")
}

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val triggerTime: Long, // Epoch millis
    val recurrence: Recurrence = Recurrence.NONE,
    val isCompleted: Boolean = false,
    val isSnoozed: Boolean = false,
    val snoozeUntil: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
