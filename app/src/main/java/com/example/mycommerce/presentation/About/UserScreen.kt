package com.example.mycommerce.presentation.About

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.mycommerce.presentation.Home.AppBar
import com.example.mycommerce.presentation.SignInScreen
import com.example.mycommerce.presentation.UserScreen
import com.example.mycommerce.ui.theme.Blue
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun UserScreen(navController : NavHostController) {
    val firestore = FirebaseFirestore.getInstance()
    val user = Firebase.auth.currentUser
  val context = LocalContext.current
    var name by remember {
        mutableStateOf("")
    }
    var phoneNumber by remember {
        mutableStateOf("")
    }
    var address by remember {
        mutableStateOf("")
    }
    var oldInfo by remember {
        mutableStateOf(UserData("a" , "" , ""))
    }
    Scaffold(modifier = Modifier.fillMaxSize() , topBar = {
        AppBar(true)
    }) { ip->
        LaunchedEffect(Unit) {
            firestore.collection("users").whereEqualTo("id", user?.uid).get()
                .addOnSuccessListener { it ->
                    it.first().let {
                        name = it.getString("name") ?: ""
                        phoneNumber = it.getString("phone") ?: ""
                        address = it.getString("address") ?: ""
                        oldInfo = UserData(
                            name = it.getString("name") ?: "",
                            phone = it.getString("phone") ?: "",
                            address = it.getString("address") ?: ""
                        )
                    }
                }
        }
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(ip).padding(8.dp) , horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = Icons.Default.AccountCircle , tint = Blue, modifier = Modifier.size(120.dp) , contentDescription = "user")
            Text(text = user?.email.toString() , fontSize = 24.sp)
            Spacer(modifier = Modifier.height(12.dp))
             Row(modifier = Modifier.fillMaxWidth() ,  verticalAlignment = Alignment.CenterVertically) {
                 Text(text = "Name" , fontSize = 18.sp , modifier = Modifier.fillMaxWidth(0.25f))
                 Spacer(modifier = Modifier.width(4.dp))
                 OutlinedTextField(value = name , onValueChange = {
                     name = it
                 })
             }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth() , verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Phone" , fontSize = 18.sp , modifier = Modifier.fillMaxWidth(0.25f))
                Spacer(modifier = Modifier.width(4.dp))
                OutlinedTextField(value = phoneNumber , onValueChange = {
                    phoneNumber = it
                })
            }
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth() ,  verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Address" , fontSize = 18.sp , modifier = Modifier.fillMaxWidth(0.25f))
                Spacer(modifier = Modifier.width(4.dp))
                OutlinedTextField(value = address , onValueChange = {
                    address = it
                })
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth() , horizontalArrangement = Arrangement.SpaceAround) {
                Button(onClick = {
                    val updates = hashMapOf<String , Any>(
                        "name" to name , "phone" to phoneNumber , "address" to address
                    )
                    firestore.collection("users").whereEqualTo("id" , user?.uid).get().addOnSuccessListener { it ->
                        it.first().reference.update(updates).addOnSuccessListener {
                            Toast.makeText(context, "Data updated", Toast.LENGTH_SHORT).show()
                        }
                    }
                } , enabled =(name != oldInfo.name || phoneNumber != oldInfo.phone || address != oldInfo.address) && name.trim().isNotEmpty() && phoneNumber.trim().isNotEmpty()
                        && address.isNotEmpty() , colors = ButtonDefaults.buttonColors(Blue ,
                    Color.White)) {
                    Text(text = "Add")
                }
                Button(onClick = {
                    Firebase.auth.signOut()
                    navController.navigate(SignInScreen)
                }) {
                    Text(text = "SignOut")
                }
            }


        }

    }
}
data class UserData(val name : String , val phone : String , val address : String)