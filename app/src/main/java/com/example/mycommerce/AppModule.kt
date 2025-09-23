package com.example.mycommerce

import com.example.mycommerce.data.AlarmSchedularImpl
import com.example.mycommerce.data.TriggerFcmImpl
import com.example.mycommerce.data.apiClientImpl
import com.example.mycommerce.data.networkObserverImpl
import com.example.mycommerce.domain.AlarmSchedular
import com.example.mycommerce.domain.TriggerFcm
import com.example.mycommerce.domain.apiClient
import com.example.mycommerce.domain.networkObserver
import com.example.mycommerce.presentation.BottomNavViewModel
import com.example.mycommerce.presentation.Cart.CartScreenViewModel
import com.example.mycommerce.presentation.Category.CategoryViewModel
import com.example.mycommerce.presentation.Checkout.CheckoutViewModel
import com.example.mycommerce.presentation.Home.HomeScreenViewModel
import com.example.mycommerce.presentation.OrderDetails.MiniViewModel
import com.example.mycommerce.presentation.Orders.OrdersViewModel
import com.example.mycommerce.presentation.Product.ProductViewModel
import org.koin.core.module.dsl.viewModel

import org.koin.dsl.module

val module = module {
    single<apiClient> {
        apiClientImpl()
    }
    single<networkObserver>{
        networkObserverImpl(get())
    }
     viewModel {
         HomeScreenViewModel(get() , get() , get())
     }
    viewModel {
        ProductViewModel(get() , get())
    }
    viewModel {
        CartScreenViewModel()
    }
    viewModel {
        CategoryViewModel(get() ,get())
    }
    viewModel {
        CheckoutViewModel(get() , get())
    }
    viewModel {
        OrdersViewModel()
    }
    viewModel {
        BottomNavViewModel()
    }
    single<AlarmSchedular> {
        AlarmSchedularImpl(get())
    }
    viewModel {
        MiniViewModel(get())
    }
    single<TriggerFcm> {
        TriggerFcmImpl()
    }

}