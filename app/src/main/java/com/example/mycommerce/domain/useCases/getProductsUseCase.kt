package com.example.mycommerce.domain.useCases

import com.example.mycommerce.data.model.Product
import com.example.mycommerce.domain.apiClient

class getProductsUseCase(private val apiClient: apiClient) {
   suspend fun getProducts() : List<Product>{
        return apiClient.getProducts()
    }
}