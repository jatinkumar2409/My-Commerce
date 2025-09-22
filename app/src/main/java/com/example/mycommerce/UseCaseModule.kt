package com.example.mycommerce

import com.example.mycommerce.domain.useCases.getCatProductUseCase
import com.example.mycommerce.domain.useCases.getCategoriesUseCase
import com.example.mycommerce.domain.useCases.getProductUseCase
import com.example.mycommerce.domain.useCases.getProductsUseCase
import org.koin.dsl.module

val useCaseModule = module {
    single<getProductsUseCase> {
        getProductsUseCase(get())
    }
    single<getCategoriesUseCase> {
        getCategoriesUseCase(get())
    }
    single<getProductUseCase> {
        getProductUseCase(get())
    }
    single<getCatProductUseCase> {
        getCatProductUseCase(get())
    }
}