package com.example.mycommerce.domain.useCases

import com.example.mycommerce.domain.apiClient

class getCategoriesUseCase(private val apiClient: apiClient) {
    suspend fun getCategories() : List<String>{
        return apiClient.getCategories()
    }
}