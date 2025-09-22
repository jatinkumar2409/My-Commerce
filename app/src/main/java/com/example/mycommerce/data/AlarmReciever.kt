package com.example.mycommerce.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.mycommerce.presentation.Checkout.OrderProduct
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class AlarmReciever : BroadcastReceiver() {
    val firestore = FirebaseFirestore.getInstance()
    val userId = Firebase.auth.currentUser?.uid
    override fun onReceive(context: Context?, intent: Intent?) {
//        Log.d("tag" , "on Recieve called")

    val id = intent?.getStringExtra("id")
        id?.let {
            firestore.collection("users").whereEqualTo("id" , userId).get().addOnSuccessListener { result ->
                result.first().reference.collection("orders").document(id).update(mapOf("delivered" to true)).addOnSuccessListener {
                    Log.d("tag" , " i have added")
                }

            }
        }
    }
}