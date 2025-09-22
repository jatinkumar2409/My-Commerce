package com.example.mycommerce

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.mycommerce.presentation.BottomBar
import com.example.mycommerce.presentation.BottomNavViewModel
import com.example.mycommerce.presentation.Cart.CartScreenViewModel
import com.example.mycommerce.presentation.CartScreen
import com.example.mycommerce.presentation.Category.CategoryViewModel
import com.example.mycommerce.presentation.CategoryScreen
import com.example.mycommerce.presentation.Checkout.CheckoutProduct
import com.example.mycommerce.presentation.Checkout.CheckoutViewModel
import com.example.mycommerce.presentation.CheckoutScreen
import com.example.mycommerce.presentation.Home.HomeScreen
import com.example.mycommerce.presentation.Home.HomeScreenViewModel
import com.example.mycommerce.presentation.HomeScreen
import com.example.mycommerce.presentation.OrderDetails.MiniViewModel
import com.example.mycommerce.presentation.OrderDetailsScreen
import com.example.mycommerce.presentation.Orders.OrdersViewModel
import com.example.mycommerce.presentation.OrdersScreen
import com.example.mycommerce.presentation.Product.ProductViewModel
import com.example.mycommerce.presentation.ProductScreen
import com.example.mycommerce.presentation.SignIn.SignInScreen
import com.example.mycommerce.presentation.SignInScreen
import com.example.mycommerce.presentation.UserScreen
import com.example.mycommerce.ui.theme.MyCommerceTheme
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity(), PaymentResultWithDataListener {
    val checkoutViewModel by viewModel<CheckoutViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val homeScreenViewModel by viewModel<HomeScreenViewModel>()
        val productViewModel by viewModel<ProductViewModel>()
        val cartScreenViewModel by viewModel<CartScreenViewModel>()
        val categoryViewModel by viewModel<CategoryViewModel>()
        val ordersViewModel by viewModel<OrdersViewModel>()
        val bottomNavViewModel by viewModel<BottomNavViewModel>()
        val miniViewModel by viewModel<MiniViewModel>()
        setContent {
            MyCommerceTheme {
               val navController = rememberNavController()
                val currentBackStack by navController.currentBackStackEntryAsState()
                val currentDestination = currentBackStack?.destination?.route
                val bottomScreens = listOf(
                    HomeScreen::class.qualifiedName!! ,
                    CartScreen::class.qualifiedName!! ,
                    OrdersScreen::class.qualifiedName!! ,
                    UserScreen::class.qualifiedName!!
                )
                Scaffold(
                    bottomBar = {
                        if (currentDestination in bottomScreens){
                            BottomBar(navController , bottomNavViewModel)
                        }
                    }
                ) { ip ->
                    NavHost(navController = navController, startDestination = SignInScreen , modifier = Modifier.padding(bottom = ip.calculateBottomPadding())) {
                        composable<SignInScreen> {
                            SignInScreen(navController)
                        }

                        composable<HomeScreen> {
                            HomeScreen(homeScreenViewModel , navController)
                        }
                        composable<CartScreen> {
                            com.example.mycommerce.presentation.Cart.CartScreen(cartScreenViewModel , navController)
                        }
                        composable<OrdersScreen> {
                            com.example.mycommerce.presentation.Orders.OrdersScreen(viewModel = ordersViewModel , navController = navController)
                        }
                        composable<UserScreen> {
                            com.example.mycommerce.presentation.About.UserScreen()
                        }
                        composable<ProductScreen> {
                            val args = it.toRoute<ProductScreen>()
                            com.example.mycommerce.presentation.Product.ProductScreen(id = args.id , viewModel = productViewModel ,navController =  navController)
                        }
                      composable<CategoryScreen> {
                          val args = it.toRoute<CategoryScreen>()
                          com.example.mycommerce.presentation.Category.CategoryScreen(categoryViewModel , args.category , navController)
                      }
                        composable<CheckoutScreen> {
                            var amount by rememberSaveable {
                                mutableStateOf(0)
                            }
                            val args = it.toRoute<CheckoutScreen>()
                            val list = remember {
                                mutableStateListOf<CheckoutProduct>()
                            }
                            val names = args.productList
                            val quantity = args.quantity
                            val priceList = args.priceList
                            LaunchedEffect(Unit) {
                                names.forEachIndexed { i, it ->
                                    list.add(CheckoutProduct(it, priceList[i], quantity[i]))
                                }
                            }
                            com.example.mycommerce.presentation.Checkout.CheckoutScreen(navController  ,checkoutViewModel,list){ it ->
                                
                                checkoutViewModel.startPayment(this@MainActivity , it )
                            }
                        }
                        composable<OrdersScreen> {
                            com.example.mycommerce.presentation.Orders.OrdersScreen(
                                navController = navController,
                                viewModel = ordersViewModel
                            )
                        }
                        composable<OrderDetailsScreen> {
                            val args = it.toRoute<OrderDetailsScreen>()
                            com.example.mycommerce.presentation.OrderDetails.OrderDetailsScreen(navController , id = args.id , miniViewModel)
                        }

                    }
                }
                }
            }
        }

    override fun onPaymentSuccess(paymentId: String?, paymentData: PaymentData?) {

       val orderId = paymentData?.orderId
        val signature = paymentData?.signature
     checkoutViewModel.verifyPayment(paymentId = paymentId?: "" , orderId = orderId?: "" , signature = signature?:"")
    }

    override fun onPaymentError(code: Int, p1: String?, paymentData: PaymentData?) {
        checkoutViewModel.onPaymentError()
    }
}


