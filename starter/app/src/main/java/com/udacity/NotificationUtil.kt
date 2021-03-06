package com.udacity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat

// Notification ID.
private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
private val FLAGS = 0

//Reference Eggtimer App

fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context, fileName: String, downloadStatus: String) {

    val contentIntent = Intent(applicationContext, DetailActivity::class.java).putExtra("filename", fileName).putExtra("downloadstatus", downloadStatus).putExtra("notificationid", NOTIFICATION_ID)
    val pendingIntent = PendingIntent.getActivity(applicationContext, NOTIFICATION_ID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT)

    val builder = NotificationCompat.Builder(applicationContext, applicationContext.getString(R.string.notification_channel_id))
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(applicationContext
            .getString(R.string.notification_title))
        .setContentText(messageBody)
        .setAutoCancel(true)
        .addAction(0,"show details", pendingIntent)
    notify(NOTIFICATION_ID, builder.build())
}

