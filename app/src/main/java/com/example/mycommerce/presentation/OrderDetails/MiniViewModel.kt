package com.example.mycommerce.presentation.OrderDetails

import androidx.lifecycle.ViewModel
import com.example.mycommerce.domain.AlarmSchedular

class MiniViewModel(private val alarmSchedular: AlarmSchedular) : ViewModel() {
    fun cancelOrder(id : String){
        alarmSchedular.cancelAlarm(id)
    }
}