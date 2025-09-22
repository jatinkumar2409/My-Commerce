package com.example.mycommerce.presentation.Product

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycommerce.data.model.Product
import com.example.mycommerce.domain.networkObserver
import com.example.mycommerce.domain.useCases.getProductUseCase
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductViewModel(private val getProductUseCase : getProductUseCase ,private val networkObserver: networkObserver) : ViewModel() {
    private val _product = MutableStateFlow<Product?>(null)
    val product = _product.asStateFlow()
    private  val _isConnected = MutableStateFlow(false)
    val isConnected = _isConnected.asStateFlow()
    init {
        getNetworkStatus()
    }

    fun getProduct(id : Int){
        viewModelScope.launch {
                _product.value = getProductUseCase.getProduct(id)

        }
    }
    fun getNetworkStatus(){
        viewModelScope.launch {
            networkObserver.isConnected().collect { it ->
                _isConnected.value = it
            }
        }
    }
    fun addToCart(cartProduct: CartProduct , onComplete : () -> Unit){
        val userId = Firebase.auth.currentUser?.uid
      val firestore = FirebaseFirestore.getInstance()
        firestore.collection("users").whereEqualTo("id" , userId).get().addOnSuccessListener { result ->
            if (result.isEmpty.not()){
                result.first().reference.collection("cart").add(cartProduct).addOnSuccessListener {
                    onComplete()
                }
            }
        }
    }
}

