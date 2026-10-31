package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String = "General",
    val iconName: String = "CheckCircle",
    val colorHex: String = "#9061F9", // Default purple
    val targetDaysPerWeek: Int = 7,
    val isArchived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "habit_logs",
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("habitId"),
        Index(value = ["habitId", "date"], unique = true)
    ]
)
data class HabitLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val habitId: Long,
    val date: String, // Format: YYYY-MM-DD
    val completedAt: Long = System.currentTimeMillis()
)

data class HabitWithStats(
    val habit: HabitEntity,
    val completedDates: Set<String>,
    val currentStreak: Int,
    val bestStreak: Int,
    val completionRate30Days: Float // 0.0 to 1.0
)
