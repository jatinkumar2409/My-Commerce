package com.example.mycommerce.domain

import com.example.mycommerce.data.model.Product
import io.ktor.client.HttpClient

interface apiClient {
    fun getClient() : HttpClient
    suspend fun getProducts() : List<Product>
    suspend fun getProduct(id : Int) : Product
    suspend fun getCategories() : List<String>
    suspend fun getCategoryProducts(category : String) : List<Product>
}