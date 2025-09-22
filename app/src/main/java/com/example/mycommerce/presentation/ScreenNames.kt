package com.example.mycommerce.presentation

import com.example.mycommerce.presentation.Product.CartProduct
import kotlinx.serialization.Serializable

@Serializable
object HomeScreen

@Serializable
object CartScreen

@Serializable
object OrdersScreen

@Serializable
data class ProductScreen(val id : Int)

@Serializable
object UserScreen

@Serializable
object SignInScreen

@Serializable
data class CategoryScreen(val category : String)

@Serializable
data class CheckoutScreen(val productList : List<String> , val priceList : List<Int> ,val quantity: List<Int>)

@Serializable
data class OrderDetailsScreen(val id : String)
