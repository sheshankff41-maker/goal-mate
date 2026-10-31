package com.example

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.data.db.GoalMateDatabase
import com.example.data.repository.GoalMateRepository
import com.example.reminder.ReminderReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GoalMateApp : Application() {
    val database by lazy { GoalMateDatabase.getDatabase(this) }
    val repository by lazy { GoalMateRepository(database) }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        // Seed initial data if first run
        CoroutineScope(Dispatchers.IO).launch {
            repository.checkAndSeedInitialData()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ReminderReceiver.CHANNEL_ID,
                ReminderReceiver.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "GoalMate Reminders & Milestone Notifications"
                enableVibration(true)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }
}
