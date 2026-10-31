package com.example.data.db

import androidx.room.TypeConverter
import com.example.data.model.GoalCategory
import com.example.data.model.Priority
import com.example.data.model.Recurrence
import com.example.data.model.ThemeMode

class Converters {
    @TypeConverter
    fun fromGoalCategory(category: GoalCategory): String = category.name

    @TypeConverter
    fun toGoalCategory(value: String): GoalCategory = runCatching {
        GoalCategory.valueOf(value)
    }.getOrDefault(GoalCategory.OTHER)

    @TypeConverter
    fun fromPriority(priority: Priority): String = priority.name

    @TypeConverter
    fun toPriority(value: String): Priority = runCatching {
        Priority.valueOf(value)
    }.getOrDefault(Priority.MEDIUM)

    @TypeConverter
    fun fromRecurrence(recurrence: Recurrence): String = recurrence.name

    @TypeConverter
    fun toRecurrence(value: String): Recurrence = runCatching {
        Recurrence.valueOf(value)
    }.getOrDefault(Recurrence.NONE)

    @TypeConverter
    fun fromThemeMode(mode: ThemeMode): String = mode.name

    @TypeConverter
    fun toThemeMode(value: String): ThemeMode = runCatching {
        ThemeMode.valueOf(value)
    }.getOrDefault(ThemeMode.SYSTEM)
}
