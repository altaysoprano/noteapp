package com.plcoding.cleanarchitecturenoteapp.feature_note.domain.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.plcoding.cleanarchitecturenoteapp.R

class MyAlarm: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        try {
            val title = intent?.extras?.get("title")
            val content = intent?.extras?.get("content")
            val id = intent?.extras?.getInt("id")
            if (id != null) {
                showNotification(context, title.toString(), content.toString(), id)
            }
        }catch (e: Exception) {
            Log.d("Receive Ex", "onReceive: ${e.printStackTrace()}")
        }
    }
}

private fun showNotification(context: Context, title: String, desc: String, id: Int) {
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channelId = "message_channel"
    val channelName = "message_name"

    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        manager.createNotificationChannel(channel)
    }

    val builder = NotificationCompat.Builder(context, channelId)
        .setContentTitle(title)
        .setContentText(desc)
        .setSmallIcon(R.drawable.ic_baseline_notifications_active_24)

    manager.notify(id, builder.build())
}