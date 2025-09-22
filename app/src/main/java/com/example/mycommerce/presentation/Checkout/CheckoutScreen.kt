package com.example.mycommerce.presentation.Checkout

import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.mycommerce.presentation.CheckoutScreen
import com.example.mycommerce.presentation.Home.AppBar
import com.example.mycommerce.presentation.OrderDetailsScreen
import com.example.mycommerce.presentation.OrdersScreen
import com.example.mycommerce.ui.theme.Blue
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.PropertyName
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale

@Composable
fun CheckoutScreen(navController: NavHostController , checkoutViewModel: CheckoutViewModel , list : List<CheckoutProduct> , onPayClick : (Int) -> Unit){
    val status by checkoutViewModel.paymentVerified.collectAsStateWithLifecycle()
    val isConnected by checkoutViewModel.isConnected.collectAsStateWithLifecycle()
    Log.d("tag1" , list.toString())
    val price = list.sumOf {
        it.price
    }
    val auth = Firebase.auth.currentUser!!
    val firestore = FirebaseFirestore.getInstance()
    var phone by remember {
        mutableStateOf("")
    }
    var address by remember {
        mutableStateOf("")
    }

    var isPayLater by rememberSaveable {
        mutableStateOf(false)
    }


    val userId = Firebase.auth.currentUser?.uid
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        firestore.collection("users").whereEqualTo("id" , auth.uid).get().addOnSuccessListener { result ->
            if (result.isEmpty.not()){
                phone = result.first().getString("phone") ?: ""
                address = result.first().getString("address") ?: ""
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize() , topBar = {
        AppBar(false){
            navController.popBackStack()
        }
    }) { ip->
    if (status?.success == true || isPayLater){
  checkoutViewModel.addOrder(isPayLater , list){
      navController.navigate(OrderDetailsScreen(it))
      Toast.makeText(context, "Order Placed", Toast.LENGTH_SHORT).show()
  }
    }
        if(status?.success == false){
            Toast.makeText(context, "Error in Payment !", Toast.LENGTH_LONG).show()
        }

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(top = ip.calculateTopPadding())) {
            Spacer(modifier = Modifier.height(16.dp))
               Text(text = "Basic Details" , fontWeight = FontWeight.Bold , fontSize = 24.sp)
            Box(modifier = Modifier.fillMaxWidth()){
                Card(modifier = Modifier.fillMaxWidth() , colors = CardDefaults.cardColors(if (isSystemInDarkTheme()) Color.DarkGray else Color.White ) ,
                    elevation = CardDefaults.cardElevation(4.dp)) {
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp)) {
                        Text(text = "Name : ${auth.email?.split("@gmail.com")[0]}", fontSize = 18.sp)
                        Text(text = "Email : ${auth.email}", fontSize = 18.sp)
                        Text(text = "Phone : +91 ${phone}")
                        Text(text = "Deliver to : $address")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Checkout" , fontSize = 24.sp , fontWeight = FontWeight.Bold)
            Box(modifier = Modifier.fillMaxWidth()){
                Column {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(if (isSystemInDarkTheme()) Color.DarkGray else Color.White),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        LazyColumn(modifier = Modifier.padding(6.dp)) {
                            items(list) { item ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = item.name + " - ${item.quantity} " + if (item.quantity == 1) "Unit" else "Units",
                                        modifier = Modifier.fillMaxWidth(0.8f),
                                        overflow = TextOverflow.Clip
                                    )
                                    Text(text = "₹${item.price}")
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Text(
                                        text = "₹${price}.00",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }

                            }
                        }

                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Button(onClick = {
                            if (isConnected) {
                                onPayClick(price)
                            }

                        },
                            modifier = Modifier.fillMaxWidth(0.4f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Blue,
                                contentColor = Color.White
                            )) {
                            Text(text = "Pay ₹$price")
                        }
                        Button(onClick = {
                            isPayLater = true
                        },
                            modifier = Modifier.fillMaxWidth(0.8f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Blue,
                                contentColor = Color.White
                            )) {
                            Text(text = "Pay on Delivery")
                        }
                    }
                }
            }
        }
    }
}

data class CheckoutProduct(val name : String , val price : Int , val quantity : Int)
data class OrderProduct(val titles : List<String> = emptyList(), val quantities: List<Int> = emptyList(), val method : String ="", val prices :List<Int> = emptyList(), val orderPlacedDates : String = "",
                        @set:PropertyName("delivered")
                        @get:PropertyName("delivered")
                       var isDelivered : Boolean = false)