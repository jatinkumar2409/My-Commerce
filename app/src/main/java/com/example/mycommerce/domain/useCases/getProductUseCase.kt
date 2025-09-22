package com.example.mycommerce.domain.useCases

import com.example.mycommerce.data.model.Product
import com.example.mycommerce.domain.apiClient

class getProductUseCase(private val apiClient: apiClient) {
    suspend fun getProduct(id : Int) : Product{
        return apiClient.getProduct(id)
    }
}