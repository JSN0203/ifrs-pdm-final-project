package com.example.finalproject.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.finalproject.R

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        Log.d("Notification", "Triggered")

        val plantName = intent.getStringExtra("plantName")
        val notification = NotificationCompat.Builder(context, "watering_channel")
            .setSmallIcon(R.drawable.ic_plant)
            .setContentTitle("Water your plant")
            .setContentText("It's time to water $plantName!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val notificationManager = NotificationManagerCompat.from(context)

        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            notificationManager.notify(plantName.hashCode(), notification)
        } else {
            Log.w("NotificationReceiver", "Notifications are disabled.")
        }
    }
}
