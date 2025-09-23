package com.example.mycommerce.presentation.Checkout

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycommerce.domain.AlarmSchedular
import com.example.mycommerce.domain.networkObserver
import com.example.mycommerce.presentation.OrderDetailsScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.razorpay.Checkout
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CheckoutViewModel(private val networkObserver: networkObserver , private val alarmSchedular: AlarmSchedular) : ViewModel() {
   val _paymentVerified = MutableStateFlow<VerifiedPayment?>(null)
    val paymentVerified = _paymentVerified.asStateFlow()
    private  val _isConnected = MutableStateFlow(false)
    val isConnected = _isConnected.asStateFlow()
    val firestore = FirebaseFirestore.getInstance()
    val userId = Firebase.auth.currentUser?.uid
    var isOrderPlaced = false
    var amount_ = 0
    init {
        getNetworkStatus()
    }
    fun getClient() : HttpClient{
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


    fun startPayment(context : Context , amount : Int){
       viewModelScope.launch {
           try {
               amount_ = amount*100
               val resp = getClient().post("/create_order") {
                   contentType(ContentType.Application.Json)
                   setBody(mapOf("amount" to amount))
               }.body<OrderResponse>()
               val orderId = resp.orderId

               Checkout.preload(context.applicationContext)
               val checkout = Checkout()
               checkout.setKeyID("rzp_test_R7BBdkwYK1PRnt")
               val options = JSONObject().apply {
                   put("name", "My Commerce")
                   put("description", "This is the commerce app")
                   put("order_id", orderId)
                   put("currency", "INR")
                   put("amount", resp.amount)

               }
               checkout.open(context as Activity, options)

           } catch (e: Exception) {
               Log.d("tag", e.message.toString())
           }
       }
    }
    fun addOrder( isPayLater : Boolean,list : List<CheckoutProduct> , onOrderPlaced : (String) -> Unit){
        if (isOrderPlaced.not()) {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = format.format(Date())
            val order = OrderProduct(
                titles = list.map { it.name },
                quantities = list.map { it.quantity },
                prices = list.map { it.price },
                method = if (isPayLater) "Pay on Delivery" else "Online",
                isDelivered = false,
                orderPlacedDates = date
            )

            firestore.collection("users").whereEqualTo("id", userId).get()
                .addOnSuccessListener { result ->
                    if (result.isEmpty.not()) {
                        result.first().reference.collection("orders").add(order)
                            .addOnSuccessListener { it ->
                                alarmSchedular.scheduleAlarm(it.id)
                                isOrderPlaced = true
                                val id = it.id
                                onOrderPlaced(id)
                            }
                    }
                }
        }

    }

    fun verifyPayment(orderId : String , paymentId : String , signature : String){
        viewModelScope.launch {
         val body = getClient().post("/verify_payment") {
                contentType(ContentType.Application.Json)
                setBody(PaymentChecker(orderId = orderId , payment = amount_ , razorpayPaymentId = paymentId , razorpaySignature  = signature))
            }.body<VerifiedPayment>()

            if (body.success){
                _paymentVerified.value = body
            }
        }
    }
    fun onPaymentError(){
        _paymentVerified.value = VerifiedPayment(success = false , payment = amount_ , message = "Payment failed")
    }
    fun getNetworkStatus(){
        viewModelScope.launch {
            networkObserver.isConnected().collect { it ->
                _isConnected.value = it
            }
        }
    }
}
@Serializable
data class OrderResponse(val success: Boolean,
                         val orderId: String,
                         val amount: Int,
                         val currency: String)

@Serializable
data class VerifiedPayment(val success : Boolean , val payment : Int ,val message : String)
@Serializable
data class PaymentChecker(val orderId : String ,  val payment : Int, val razorpayPaymentId: String  , val razorpaySignature: String)