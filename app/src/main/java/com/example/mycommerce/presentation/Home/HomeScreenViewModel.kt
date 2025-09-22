package com.example.mycommerce.presentation.Home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycommerce.data.model.Product
import com.example.mycommerce.domain.apiClient
import com.example.mycommerce.domain.networkObserver
import com.example.mycommerce.domain.useCases.getCategoriesUseCase
import com.example.mycommerce.domain.useCases.getProductsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeScreenViewModel(private val getProductUseCase : getProductsUseCase , private val networkObserver: networkObserver ,
    private val getCategoriesUseCase : getCategoriesUseCase) : ViewModel(){
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products = _products.asStateFlow()
    private  val _isConnected = MutableStateFlow(false)
    val isConnected = _isConnected.asStateFlow()
    private val _categories = MutableStateFlow<List<String>>(emptyList())
     val categories = _categories.asStateFlow()

    init {
        getNetworkStatus()
        getProducts()
        getCategories()
    }
    fun getProducts(){
        viewModelScope.launch {
            try {
                _products.value = getProductUseCase.getProducts()
            }
            catch (e: Exception){
                print(e.message)
            }
        }

    }
    fun getNetworkStatus(){
        viewModelScope.launch {
            networkObserver.isConnected().collect { it ->
         _isConnected.value = it
            }
        }
    }
    fun getCategories(){
            viewModelScope.launch {
                try {
                _categories.value = getCategoriesUseCase.getCategories()
            }
                catch (e: Exception){
                    print(e.message)
                }
        }

    }
}