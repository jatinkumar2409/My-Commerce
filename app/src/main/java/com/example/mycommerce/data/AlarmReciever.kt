package com.example.mycommerce.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.mycommerce.domain.TriggerFcm
import com.example.mycommerce.presentation.Checkout.OrderProduct
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlarmReciever : BroadcastReceiver() , KoinComponent {
    val fcm: TriggerFcm by inject()
    val firestore = FirebaseFirestore.getInstance()
    val userId = Firebase.auth.currentUser?.uid
   var token = ""
    var body : String = ""
    override fun onReceive(context: Context?, intent: Intent?) {



    val id = intent?.getStringExtra("id")

            id?.let {
                firestore.collection("users").whereEqualTo("id", userId).get()
                    .addOnSuccessListener { result ->
                        result.first().reference.collection("orders").document(id)
                            .update(mapOf("delivered" to true)).addOnSuccessListener {
                            result.first().reference.collection("orders").document(id).get()
                                .addOnSuccessListener { it ->
                                  firestore.collection("users").whereEqualTo("id" ,userId ).get().addOnSuccessListener {
                                              token = it.first().getString("token") ?: ""
                                                  CoroutineScope(Dispatchers.Default).launch {

                                                  fcm.notifyfcm(
                                                      token,
                                                      "Order Delivered",
                                                      "Click to place your next order"
                                                  )

                                          }

        }
                                }
                        }

                    }

            }

    }
}