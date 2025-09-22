package com.example.mycommerce.presentation.Orders

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.mycommerce.R
import com.example.mycommerce.presentation.Checkout.OrderProduct
import com.example.mycommerce.presentation.Home.AppBar
import com.example.mycommerce.presentation.OrderDetailsScreen
import com.example.mycommerce.presentation.OrdersScreen
import com.example.mycommerce.ui.theme.Blue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(navController: NavHostController , viewModel: OrdersViewModel) {
    LaunchedEffect(Unit) {
        viewModel.getOrders()
        viewModel.getIds()
    }
    val orders by viewModel.orders.collectAsStateWithLifecycle()

    val tabs = listOf("Orders" , "Completed")
    var selected by rememberSaveable {
        mutableStateOf(0)
    }
    val idList = viewModel.idList

    Scaffold(modifier = Modifier.fillMaxSize() , topBar = {
        AppBar(true)
    }){ ip ->
        Column(modifier = Modifier.fillMaxSize().padding(top = ip.calculateTopPadding())) {
            SecondaryTabRow(selectedTabIndex = selected , modifier = Modifier.fillMaxWidth()) {
                tabs.forEachIndexed { i ,d ->
                    Tab(
                        selected = selected == i,
                        onClick = {selected = i} ,
                        text = {Text(text = d)},
                        selectedContentColor = Blue,
                        unselectedContentColor = Blue
                    )

                }
            }
            if (selected == 0){
                if (orders.none { !it.isDelivered }){
                    Box(modifier = Modifier.fillMaxSize() , contentAlignment = Alignment.Center){
                        Text(text = "No orders pending" , color = Blue)
                    }
                }
                else {
                    LazyColumn {
                        itemsIndexed(orders) { i, item ->

                            if (!item.isDelivered) {
                                val price = item.prices.sum()
                                Box(modifier = Modifier.fillMaxWidth().padding(6.dp).clickable {
                                    navController.navigate(OrderDetailsScreen(idList[i]))
                                }) {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(if (isSystemInDarkTheme()) Color.DarkGray else Color.White)
                                    ) {
                                        Row(modifier = Modifier.fillMaxWidth()) {
                                            Image(
                                                painter = painterResource(R.drawable.outline_shopping_bag_speed_24),
                                                contentDescription = "image",
                                                modifier = Modifier.size(120.dp)
                                            )
                                            Column(modifier = Modifier.fillMaxWidth()) {
                                                Text(
                                                    text = item.titles.joinToString(),
                                                    fontSize = 18.sp,
                                                    maxLines = 2,
                                                    overflow = TextOverflow.Clip
                                                )
                                                Text(text = "Ordered on : ${item.orderPlacedDates}")
                                                Text(text = "Method : ${item.method}")
                                                Text(text = "₹$price")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else{
                if (orders.none { it.isDelivered }){
                    Box(modifier = Modifier.fillMaxSize() , contentAlignment = Alignment.Center){
                        Text(text = "No orders completed" , color = Blue)
                    }
                }
                else {
                    LazyColumn {
                        itemsIndexed(orders) { i, item ->
                            if (item.isDelivered) {
                                val price = item.prices.sum()
                                Box(modifier = Modifier.fillMaxWidth().padding(6.dp).clickable {
                                    navController.navigate(OrderDetailsScreen(idList[i]))
                                }) {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(if (isSystemInDarkTheme()) Color.DarkGray else Color.White)
                                    ) {
                                        Row(modifier = Modifier.fillMaxWidth()) {
                                            Image(
                                                painter = painterResource(R.drawable.outline_shopping_bag_speed_24),
                                                contentDescription = "image",
                                                modifier = Modifier.size(120.dp)
                                            )
                                            Column(modifier = Modifier.fillMaxWidth()) {
                                                Text(
                                                    text = item.titles.joinToString(),
                                                    fontSize = 18.sp,
                                                    maxLines = 2,
                                                    overflow = TextOverflow.Clip
                                                )
                                                Text(text = "Ordered on : ${item.orderPlacedDates}")
                                                Text(text = "Method : ${item.method}")
                                                Text(text = "₹$price")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
    }
}