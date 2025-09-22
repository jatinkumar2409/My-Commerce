package com.example.mycommerce.data

import com.example.mycommerce.data.model.Product
import com.example.mycommerce.domain.apiClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class apiClientImpl : apiClient {
//    https://fakestoreapi.com/products
    override fun getClient(): HttpClient {
        return HttpClient {
            install(ContentNegotiation){
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
            install(DefaultRequest){
                url {
                    protocol = URLProtocol.HTTPS
                    host = "fakestoreapi.com/"
                }
            }
        }
    }

    override suspend fun getProducts(): List<Product> {
   return getClient().get("products").body<List<Product>>()
    }

    override suspend fun getProduct(id: Int): Product {
 return getClient().get("products/$id").body<Product>()
    }

    override suspend fun getCategories(): List<String> {
     return getClient().get("products/categories").body()
    }

    override suspend fun getCategoryProducts(category : String): List<Product> {
   return getClient().get("products/category/$category").body<List<Product>>()
    }
}