package com.example.mycommerce.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class BottomNavViewModel : ViewModel() {
    var selectedTab by mutableStateOf(0)
        private set

    fun setTab(tab : Int){
        selectedTab = tab
    }
}