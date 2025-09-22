package com.example.mycommerce.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.mycommerce.domain.AlarmSchedular

class AlarmSchedularImpl(private val context : Context) : AlarmSchedular {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    override fun scheduleAlarm(id : String) {
        val requestCode = id.hashCode()
        val triggerTime = System.currentTimeMillis() + 10 * 1000
        val intent = Intent(context , AlarmReciever::class.java).apply {
            putExtra("id" , id)
        }
        val pendingIntent = PendingIntent.getBroadcast(context , requestCode , intent , PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP ,triggerTime , pendingIntent)
    }

    override fun cancelAlarm(id : String) {
        val requestCode = id.hashCode()
        val intent = Intent(context , AlarmReciever::class.java).apply {
            putExtra("id" , id)
        }
        val pendingIntent = PendingIntent.getBroadcast(context , requestCode , intent ,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }
}