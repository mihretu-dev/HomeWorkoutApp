package com.base.androidstartertemplate.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

object ReminderManager {

    private const val WORK_NAME = "DailyWorkoutReminderWork"
    const val PREFS_NAME = "workout_reminder_prefs"
    const val KEY_ENABLED = "reminder_enabled"
    const val KEY_HOUR = "reminder_hour"
    const val KEY_MINUTE = "reminder_minute"
    const val KEY_ACTIVE_DAYS = "reminder_days"
    const val KEY_THEME = "reminder_theme"

    fun scheduleDailyReminder(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val targetHour = prefs.getInt(KEY_HOUR, 18)
        val targetMinute = prefs.getInt(KEY_MINUTE, 0)

        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, targetHour)
            set(Calendar.MINUTE, targetMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(now)) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val initialDelay = target.timeInMillis - now.timeInMillis

        val workRequest = PeriodicWorkRequestBuilder<WorkoutReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    fun cancelDailyReminder(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
}
