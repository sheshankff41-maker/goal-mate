package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.dao.BadgeDao
import com.example.data.dao.GoalDao
import com.example.data.dao.HabitDao
import com.example.data.dao.ReminderDao
import com.example.data.dao.TaskDao
import com.example.data.dao.UserProfileDao
import com.example.data.model.BadgeEntity
import com.example.data.model.GoalEntity
import com.example.data.model.HabitEntity
import com.example.data.model.HabitLogEntity
import com.example.data.model.ReminderEntity
import com.example.data.model.SubTaskEntity
import com.example.data.model.TaskEntity
import com.example.data.model.UserProfileEntity

@Database(
    entities = [
        GoalEntity::class,
        SubTaskEntity::class,
        TaskEntity::class,
        ReminderEntity::class,
        HabitEntity::class,
        HabitLogEntity::class,
        UserProfileEntity::class,
        BadgeEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class GoalMateDatabase : RoomDatabase() {
    abstract fun goalDao(): GoalDao
    abstract fun taskDao(): TaskDao
    abstract fun reminderDao(): ReminderDao
    abstract fun habitDao(): HabitDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun badgeDao(): BadgeDao

    companion object {
        @Volatile
        private var INSTANCE: GoalMateDatabase? = null

        fun getDatabase(context: Context): GoalMateDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GoalMateDatabase::class.java,
                    "goalmate_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
