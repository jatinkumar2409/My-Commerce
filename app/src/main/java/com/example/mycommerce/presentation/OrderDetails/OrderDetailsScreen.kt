package com.example.mycommerce.presentation.OrderDetails

import android.graphics.Color
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.mycommerce.presentation.Checkout.OrderProduct
import com.example.mycommerce.presentation.Home.AppBar
import com.example.mycommerce.ui.theme.Blue
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun OrderDetailsScreen(navController: NavHostController , id : String , viewModel: MiniViewModel) {
    val context = LocalContext.current
    var enabled by rememberSaveable {
        mutableStateOf(true)
    }
    var list by remember {
        mutableStateOf<OrderProduct?>(null)
    }
    val firestore = FirebaseFirestore.getInstance()
    val userId = Firebase.auth.currentUser?.uid
    Scaffold(modifier = Modifier.fillMaxSize() , topBar = { AppBar(false){
        navController.popBackStack()
    } }) { ip ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(top = ip.calculateTopPadding())) {
            LaunchedEffect(Unit) {
                firestore.collection("users").whereEqualTo("id", userId).get().addOnSuccessListener {  result ->
                    if (result.isEmpty.not()){
                        result.first().reference.collection("orders").document(id).get().addOnSuccessListener {
                            if (it.exists()) {
                                list = it.toObject(OrderProduct::class.java)
                            }
                        }
                    }
                }
            }
            if (list == null){
                Box(modifier = Modifier.fillMaxSize() , contentAlignment = Alignment.Center){
                    CircularProgressIndicator()
                }
            }
            else {
                var txt by rememberSaveable {
                    mutableStateOf("")
                }
                LaunchedEffect(Unit) {
                    list?.quantities?.forEachIndexed { i, it ->
                        txt += "${list?.titles[i]} - $it " + if (it==1) "Unit\n" else "Units \n" + "\n"
                    }
                }
                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)) {
                    Text(
                        text = txt,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "₹${list?.prices?.sum()}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "Ordered on : ${list?.orderPlacedDates}", fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .height(16.dp)
                                .clip(shape = CircleShape)
                                .background(color = if (list?.isDelivered == true) Blue else androidx.compose.ui.graphics.Color.White)

                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(0.8f),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Ordered")
                            Text(text = "Delivered")
                        }
                    }
                    if (list?.isDelivered == false) {
                        Button(
                            onClick = {
                                enabled = false
                         viewModel.cancelOrder(id)
                                firestore.collection("users").whereEqualTo("id" ,userId).get().addOnSuccessListener { result ->
                                    result.first().reference.collection("orders").document(id).delete().addOnSuccessListener {
                                        Toast.makeText(context, "Order Cancelled", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },enabled = enabled ,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Blue,
                                contentColor = androidx.compose.ui.graphics.Color.White
                            )
                        ) {
                            Text(text = "Cancel Order")
                        }
                    }
                }
            }
        }
    }
}