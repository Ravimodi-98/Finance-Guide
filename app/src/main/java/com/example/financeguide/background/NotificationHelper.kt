package com.example.financeguide.background

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.financeguide.MainActivity
import com.example.financeguide.R

class NotificationHelper(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val budgetChannel = NotificationChannel(
                CHANNEL_BUDGET_ALERTS,
                "Budget Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts when you are close to or exceeding your budget"
            }

            val tipsChannel = NotificationChannel(
                CHANNEL_SMART_TIPS,
                "Smart Tips",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Financial suggestions and tips"
            }

            notificationManager.createNotificationChannels(listOf(budgetChannel, tipsChannel))
        }
    }

    fun showBudgetAlert(title: String, message: String, notificationId: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_BUDGET_ALERTS)
            .setSmallIcon(android.R.drawable.ic_dialog_alert) // Ideally use app icon
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(notificationId, builder.build())
    }

    companion object {
        const val CHANNEL_BUDGET_ALERTS = "budget_alerts_channel"
        const val CHANNEL_SMART_TIPS = "smart_tips_channel"
    }
}
