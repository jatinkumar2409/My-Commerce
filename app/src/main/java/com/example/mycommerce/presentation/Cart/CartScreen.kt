package com.example.mycommerce.presentation.Cart

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveShoppingCart
import androidx.compose.material.icons.filled.SignalWifiConnectedNoInternet4
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.mycommerce.presentation.CartScreen
import com.example.mycommerce.presentation.Home.AppBar
import com.example.mycommerce.presentation.Product.CartProduct
import com.example.mycommerce.presentation.ProductScreen
import com.example.mycommerce.ui.theme.Blue

@Composable
fun CartScreen(viewModel: CartScreenViewModel , navController: NavHostController) {
    LaunchedEffect(Unit) {
        viewModel.loadCart()
    }
    val cartProduct by viewModel.cartProduct.collectAsStateWithLifecycle()

    Scaffold(modifier = Modifier.fillMaxSize() , topBar = {
        AppBar(true)
    }) { ip ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = ip.calculateTopPadding())
        ) {
      if (cartProduct.isEmpty()){
              Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                  Text(text = "No Items")
              }
          }
            else{
              LazyColumn(modifier = Modifier.fillMaxSize()) {
                  items(cartProduct){ item ->
                      CartProd(item) { it ->
                          navController.navigate(ProductScreen(it))
                      }
                  }
              }
          }
        }
    }

}

@Composable
fun CartProd(cartProduct: CartProduct ,onClick : (Int) -> Unit) {
    Spacer(modifier = Modifier.height(6.dp))
    Box(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            cartProduct.id?.let {
                onClick(it)
            }
        }){
        Card(modifier = Modifier.fillMaxWidth() , elevation = CardDefaults.cardElevation(4.dp) , colors = CardDefaults.cardColors(if (isSystemInDarkTheme())
            Color.DarkGray else Color.White)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(model = cartProduct.image , contentDescription = "image" , modifier = Modifier.size(120.dp))
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)) {
                    Text(text = cartProduct.title , fontSize = 18.sp , fontWeight = FontWeight.SemiBold , maxLines = 2 , overflow = TextOverflow.Clip)
                    Text(text = cartProduct.desc , maxLines = 2 , overflow = TextOverflow.Ellipsis)
                    Text(text = "Quantity : ${cartProduct.quantity}")
                    Text(text = "₹${cartProduct.price}" , fontSize = 20.sp , fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}