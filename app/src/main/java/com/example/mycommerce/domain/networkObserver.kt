package com.example.mycommerce.domain

import kotlinx.coroutines.flow.Flow

interface networkObserver {
    fun isConnected() : Flow<Boolean>
}