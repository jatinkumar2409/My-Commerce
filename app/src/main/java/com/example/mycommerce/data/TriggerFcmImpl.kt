package com.example.mycommerce.data

import android.util.Log
import androidx.compose.ui.autofill.ContentType
import com.example.mycommerce.domain.TriggerFcm
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class TriggerFcmImpl : TriggerFcm {
    override fun getClient(): HttpClient {
        return HttpClient {
            install(ContentNegotiation){
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
            install(DefaultRequest){
                url{
                    protocol = URLProtocol.HTTPS
                     host = "backend-razorpay-one.vercel.app"
                }
            }
        }
    }

    override suspend fun notifyfcm(
        token: String,
        title: String,
        body: String
    ) {
       val a = getClient().post("/send"){
            contentType(io.ktor.http.ContentType.Application.Json)
           setBody(fcmData(token , title , body))
        }.bodyAsText()

    }
}

@Serializable
data class fcmData(val token : String , val title : String , val body: String)