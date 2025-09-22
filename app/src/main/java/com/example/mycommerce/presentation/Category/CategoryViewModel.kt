package com.example.mycommerce.presentation.Category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycommerce.data.model.Product
import com.example.mycommerce.domain.networkObserver
import com.example.mycommerce.domain.useCases.getCatProductUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryViewModel(private val getCatProductUseCase : getCatProductUseCase , private val networkObserver: networkObserver) : ViewModel() {
    private val _catProducts = MutableStateFlow<List<Product>>(emptyList())
    val catProducts = _catProducts.asStateFlow()
    private val _isConnected = MutableStateFlow(false)
    val isConnected = _isConnected.asStateFlow()
    init {
        getNetworkStatus()
    }
    fun getCatProduct(category : String){
        viewModelScope.launch {
            _catProducts.value = getCatProductUseCase.getCatProduct(category)
        }
    }

    fun getNetworkStatus(){
        viewModelScope.launch {
            networkObserver.isConnected().collect { it ->
                _isConnected.value = it
            }
        }
    }
}