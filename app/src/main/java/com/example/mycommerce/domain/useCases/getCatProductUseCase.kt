package com.example.mycommerce.domain.useCases

import com.example.mycommerce.data.model.Product
import com.example.mycommerce.domain.apiClient

class getCatProductUseCase(private val apiClient: apiClient){
   suspend fun getCatProduct(category : String) : List<Product>{
        return apiClient.getCategoryProducts(category)
    }
}