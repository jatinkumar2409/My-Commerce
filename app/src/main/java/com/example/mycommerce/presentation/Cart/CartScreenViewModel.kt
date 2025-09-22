package com.example.mycommerce.presentation.Cart

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycommerce.domain.networkObserver
import com.example.mycommerce.presentation.Product.CartProduct
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CartScreenViewModel() : ViewModel() {
   private val _cartProducts = MutableStateFlow<List<CartProduct>>(emptyList())
    val cartProduct = _cartProducts.asStateFlow()

  init {

      loadCart()
  }
    fun loadCart(){
        val firestore = FirebaseFirestore.getInstance()
        val userId = Firebase.auth.currentUser?.uid

            firestore.collection("users").whereEqualTo("id", userId).get()
                .addOnSuccessListener { it ->
                    if (it.isEmpty.not()) {
                        it.first().reference.collection("cart").get().addOnSuccessListener { data ->
                            if (it.isEmpty.not()) {
                                _cartProducts.value = data.toObjects(CartProduct::class.java)
                            }

                        }
                    }
                }
    }


}