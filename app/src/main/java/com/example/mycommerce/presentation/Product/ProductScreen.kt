package com.example.mycommerce.presentation.Product

import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SignalWifiConnectedNoInternet4
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.mycommerce.data.model.Product
import com.example.mycommerce.presentation.CheckoutScreen
import com.example.mycommerce.presentation.Home.AppBar
import com.example.mycommerce.ui.theme.Blue
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.serialization.Serializable
import kotlin.reflect.typeOf

@Composable
fun ProductScreen(id: Int, viewModel: ProductViewModel, navController: NavHostController) {

     val product by viewModel.product.collectAsStateWithLifecycle()
    val isConnected by viewModel.isConnected.collectAsStateWithLifecycle()
    var quantity by rememberSaveable {
        mutableStateOf(1)
    }
    Scaffold(
        topBar = {
            AppBar(false){
                navController.popBackStack()
            }
        }
    ) { ip ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(top = ip.calculateTopPadding())) {
            if (!isConnected && (product == null || product?.id != id)){
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
            } else if (isConnected && (product == null || product?.id != id))  {
                LaunchedEffect(Unit) {
                    viewModel.getProduct(id)
                }
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ProgressIndicatorDefaults.circularColor)
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                ShowDetails(product!!,viewModel ,isConnected , navController, quantity, {
                    if (quantity > 1) {
                        quantity -= 1
                    }
                }) {
                    quantity += 1
                }
            }
        }
    }

}

@Composable
fun ShowDetails(product : Product  ,viewModel: ProductViewModel , isConnected : Boolean , navController: NavHostController,quantity : Int , onMinus: () -> Unit , onPlus : () -> Unit ) {
    val firestore = FirebaseFirestore.getInstance()
    val userId = Firebase.auth.currentUser?.uid
    var enabled by remember {
        mutableStateOf(true)
    }
    val context = LocalContext.current
    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())) {
        Row(modifier = Modifier.fillMaxWidth() , horizontalArrangement = Arrangement.Center) {
            AsyncImage(model = product.image , contentDescription = "image" , modifier = Modifier.size(240.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth() , horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = product.category.capitalize() , fontSize = 18.sp , fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = {
                    onMinus()
                }) {
                    Text(text = "-" , fontWeight = FontWeight.Bold , fontSize = 20.sp)
                }
                Text(text = quantity.toString() , fontSize = 18.sp)
                TextButton(onClick = {
                    onPlus()
                }) {
                    Text(text = "+" , fontWeight = FontWeight.Bold , fontSize = 20.sp)
                }
            }
        }
        Text(text = product.title.capitalize() , fontSize = 20.sp , fontWeight = FontWeight.SemiBold , modifier = Modifier.padding(horizontal = 4.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = product.description.capitalize() , fontSize = 18.sp , modifier = Modifier.padding(horizontal = 4.dp))
        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "₹${product.price.toInt()*80*quantity}" , fontSize = 22.sp , fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp) )
        Text(text = "Rating : ${product.rating.rate}⭐" , fontSize = 18.sp , fontWeight = FontWeight.SemiBold ,modifier = Modifier.padding(horizontal = 6.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth() , horizontalArrangement = Arrangement.SpaceAround) {
            Button(onClick = {

                firestore.collection("users").whereEqualTo("id" , userId).get().addOnSuccessListener { it ->

                    if (it.isEmpty.not()){

                        it.first().reference.collection("cart").whereEqualTo("id" , product.id).get().addOnSuccessListener {  result ->
                            if (result.isEmpty.not()){
                                Toast.makeText(context, "Product already in cart", Toast.LENGTH_SHORT).show()

                            }
                            else{
                                if (isConnected) {
                                    enabled = false
                                    val cartProduct = CartProduct(
                                        title = product.title,
                                        id = product.id,
                                        price = product.price.toInt() * 80 * quantity,
                                        quantity = quantity,
                                        image = product.image,
                                        desc = product.description
                                    )
                                    viewModel.addToCart(cartProduct) {
                                        Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                else{
                                    Toast.makeText(context, "Please connect to the internet", Toast.LENGTH_SHORT).show()
                                }
                            }

                        }
                    }
                    else{

                    }
                }

            } , enabled = enabled , modifier = Modifier.fillMaxWidth(0.4f)) {
                Text(text = "Add to Cart")
            }
            Button(onClick = {
                navController.navigate(CheckoutScreen(listOf(product.title) , listOf(product.price.toInt()*80) , listOf(quantity)))
            } , modifier = Modifier.fillMaxWidth(0.8f)) {
                Text(text = "Buy Now")
            }
        }

    }
}
data class CartProduct(val title : String = "" , val id : Int? = null , val price : Int = 0 , val quantity : Int = 1 , val image : String = "" , val desc : String = "" )