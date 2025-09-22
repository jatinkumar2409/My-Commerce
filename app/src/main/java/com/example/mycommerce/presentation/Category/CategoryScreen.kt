package com.example.mycommerce.presentation.Category

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SignalWifiConnectedNoInternet4
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.mycommerce.presentation.Home.AppBar
import com.example.mycommerce.presentation.Home.ShowProducts
import com.example.mycommerce.presentation.ProductScreen
import com.example.mycommerce.ui.theme.Blue

@Composable
fun CategoryScreen(viewModel: CategoryViewModel , category : String , navController: NavHostController) {
    LaunchedEffect(Unit) {
        viewModel.getCatProduct(category)
    }
    val catProduct by viewModel.catProducts.collectAsStateWithLifecycle()
    val isConnected by viewModel.isConnected.collectAsStateWithLifecycle()
    Scaffold(modifier = Modifier.fillMaxSize() , topBar = {
        AppBar(false){
            navController.popBackStack()
        }
    }) { ip->
        Column(modifier = Modifier.fillMaxSize().padding(top = ip.calculateTopPadding())) {
            if (!isConnected && catProduct.isEmpty()){
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.SignalWifiConnectedNoInternet4,
                        contentDescription = "NoInternet",
                        modifier = Modifier.size(48.dp),
                        tint = Blue
                    )
                    Text(text = "You're not not connected to the internet")
                }
            }
            else if (isConnected && (catProduct.isEmpty() || catProduct.first().category != category)){
                LaunchedEffect(Unit) {
                    viewModel.getCatProduct(category)
                }
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ProgressIndicatorDefaults.circularColor)
                }
            }
            else {
                LazyColumn {
                    items(catProduct){ item ->
                        ShowProducts(item , item.rating) { it ->
                            navController.navigate(ProductScreen(it))
                        }
                    }
                }
            }
        }
    }
}