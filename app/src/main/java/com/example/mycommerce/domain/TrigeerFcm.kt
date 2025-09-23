package com.example.mycommerce.domain

import io.ktor.client.HttpClient

interface TriggerFcm {
    fun getClient() : HttpClient
    suspend fun notifyfcm(token : String , title : String , body : String)
}