package com.base.androidstartertemplate.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.base.androidstartertemplate.MainActivity
import java.util.Calendar

class WorkoutReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val prefs = context.getSharedPreferences(ReminderManager.PREFS_NAME, Context.MODE_PRIVATE)

        // Check active days of week (Calendar.SUNDAY=1 .. Calendar.SATURDAY=7)
        val calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_WEEK)
        val activeDaysStr = prefs.getString(ReminderManager.KEY_ACTIVE_DAYS, "1,2,3,4,5,6,7") ?: "1,2,3,4,5,6,7"
        val activeDays = activeDaysStr.split(",").mapNotNull { it.toIntOrNull() }

        if (!activeDays.contains(currentDay)) {
            return Result.success()
        }

        val theme = prefs.getString(ReminderManager.KEY_THEME, "Beast Mode 💪") ?: "Beast Mode 💪"
        showNotification(theme)
        return Result.success()
    }

    private fun showNotification(theme: String) {
        val channelId = "workout_reminders_channel"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Daily Workout Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for daily home workout sessions"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val (title, content) = when (theme) {
            "Consistency 🔥" -> Pair(
                "Daily Consistency 🔥",
                "Keep your workout streak alive! A quick session awaits you."
            )
            "Quick Energizer ⚡" -> Pair(
                "Quick Workout Boost ⚡",
                "Supercharge your day with a fast home workout session!"
            )
            "Night Grind 🌙" -> Pair(
                "Evening Grind 🌙",
                "Finish your day strong with a rewarding workout!"
            )
            else -> Pair(
                "Beast Mode Time! 💪",
                "No excuses! Get in position and hit your daily workout now."
            )
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(1001, notification)
    }
}
