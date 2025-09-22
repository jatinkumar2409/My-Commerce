package com.example.mycommerce.presentation.Orders

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.mycommerce.presentation.Checkout.OrderProduct
import com.example.mycommerce.presentation.OrdersScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class OrdersViewModel : ViewModel() {
   private val _orders = MutableStateFlow<List<OrderProduct>>(emptyList())
    val orders = _orders.asStateFlow()
    val idList = mutableStateListOf<String>()
  val firestore = FirebaseFirestore.getInstance()
    val userId = Firebase.auth.currentUser?.uid

    fun getOrders(){
     firestore.collection("users").whereEqualTo("id" , userId).get().addOnSuccessListener { result ->
         if (result.isEmpty.not()){
             result.first().reference.collection("orders").get().addOnSuccessListener {
                 if (it.isEmpty.not()){
                     _orders.value = it.toObjects(OrderProduct::class.java)
                 }
             }
         }
     }
    }
    fun getIds(){
        firestore.collection("users").whereEqualTo("id" , userId).get().addOnSuccessListener{ result ->
            result.first().reference.collection("orders").get().addOnSuccessListener { it ->
                for (doc in it){
                    idList.add(doc.id)
                }
            }

        }
    }
}