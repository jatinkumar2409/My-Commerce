package com.example.mycommerce.domain

import android.app.PendingIntent

interface AlarmSchedular {

    fun scheduleAlarm( id : String)
    fun cancelAlarm(id : String)
}