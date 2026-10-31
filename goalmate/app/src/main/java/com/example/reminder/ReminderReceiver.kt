package com.example.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.data.db.GoalMateDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "goalmate_reminders_channel"
        const val CHANNEL_NAME = "GoalMate Reminders"
        const val ACTION_FIRE_REMINDER = "com.example.ACTION_REMINDER_NOTIFICATION"
        const val ACTION_SNOOZE = "com.example.ACTION_REMINDER_SNOOZE"
        const val ACTION_MARK_DONE = "com.example.ACTION_REMINDER_DONE"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val reminderId = intent.getLongExtra(ReminderScheduler.EXTRA_REMINDER_ID, -1L)
        val title = intent.getStringExtra(ReminderScheduler.EXTRA_REMINDER_TITLE) ?: "GoalMate Reminder"
        val desc = intent.getStringExtra(ReminderScheduler.EXTRA_REMINDER_DESC) ?: "It's time to work on your goals!"

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(notificationManager)

        when (action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                // Reschedule active reminders
                CoroutineScope(Dispatchers.IO).launch {
                    val db = GoalMateDatabase.getDatabase(context)
                    val reminderDao = db.reminderDao()
                    // Re-register alarms for active reminders
                }
            }
            ACTION_SNOOZE -> {
                notificationManager.cancel(reminderId.toInt())
                CoroutineScope(Dispatchers.IO).launch {
                    val db = GoalMateDatabase.getDatabase(context)
                    val reminder = db.reminderDao().getReminderById(reminderId)
                    if (reminder != null) {
                        val snoozedTime = System.currentTimeMillis() + 10 * 60 * 1000L // 10 minutes
                        val updated = reminder.copy(isSnoozed = true, snoozeUntil = snoozedTime)
                        db.reminderDao().updateReminder(updated)
                        ReminderScheduler.schedule(context, updated)
                    }
                }
            }
            ACTION_MARK_DONE -> {
                notificationManager.cancel(reminderId.toInt())
                CoroutineScope(Dispatchers.IO).launch {
                    val db = GoalMateDatabase.getDatabase(context)
                    val reminder = db.reminderDao().getReminderById(reminderId)
                    if (reminder != null) {
                        db.reminderDao().updateReminder(reminder.copy(isCompleted = true, isSnoozed = false))
                    }
                }
            }
            ACTION_FIRE_REMINDER -> {
                showNotification(context, notificationManager, reminderId, title, desc)
            }
            else -> {
                showNotification(context, notificationManager, reminderId, title, desc)
            }
        }
    }

    private fun showNotification(
        context: Context,
        notificationManager: NotificationManager,
        reminderId: Long,
        title: String,
        desc: String
    ) {
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openPendingIntent = PendingIntent.getActivity(
            context,
            reminderId.toInt(),
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Snooze intent
        val snoozeIntent = Intent(context, ReminderReceiver::class.java).apply {
            action = ACTION_SNOOZE
            putExtra(ReminderScheduler.EXTRA_REMINDER_ID, reminderId)
            putExtra(ReminderScheduler.EXTRA_REMINDER_TITLE, title)
            putExtra(ReminderScheduler.EXTRA_REMINDER_DESC, desc)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            (reminderId + 10000).toInt(),
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Mark done intent
        val doneIntent = Intent(context, ReminderReceiver::class.java).apply {
            action = ACTION_MARK_DONE
            putExtra(ReminderScheduler.EXTRA_REMINDER_ID, reminderId)
        }
        val donePendingIntent = PendingIntent.getBroadcast(
            context,
            (reminderId + 20000).toInt(),
            doneIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(title)
            .setContentText(desc)
            .setStyle(NotificationCompat.BigTextStyle().bigText(desc))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(openPendingIntent)
            .setAutoCancel(true)
            .addAction(android.R.drawable.ic_media_pause, "Snooze 10m", snoozePendingIntent)
            .addAction(android.R.drawable.checkbox_on_background, "Mark Done", donePendingIntent)
            .build()

        notificationManager.notify(reminderId.toInt(), notification)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for scheduled goal and task reminders"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
}
