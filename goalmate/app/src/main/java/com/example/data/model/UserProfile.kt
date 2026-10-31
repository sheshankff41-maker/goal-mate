package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ThemeMode {
    SYSTEM,
    DARK,
    LIGHT
}

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Int = 1,
    val name: String = "Alex Rivera",
    val motto: String = "Master your goals, conquer your day.",
    val avatarChoice: String = "avatar_1",
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val notificationsEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val currentStreak: Int = 1,
    val bestStreak: Int = 1,
    val lastActiveDate: String = ""
)
