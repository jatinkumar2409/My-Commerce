package com.example.mycommerce.presentation.SignIn

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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.text.isDigitsOnly
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.mycommerce.R
import com.example.mycommerce.presentation.HomeScreen
import com.example.mycommerce.ui.theme.Blue
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun SignInScreen(navController: NavHostController){
    var showDialog by remember {
        mutableStateOf(false)
    }
    val auth = Firebase.auth
    val firestore = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    var isCreate by rememberSaveable {
        mutableStateOf(true)
    }
    var email by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    var phone by remember {
        mutableStateOf("")
    }
    var address by remember {
        mutableStateOf("")
    }
    val gmail = remember {
        mutableStateOf("")
    }
    if (showDialog){
        DialogReset({showDialog = false} ,gmail)
    }
    Column(modifier = Modifier.fillMaxSize() ) {
        if (isCreate){
            Spacer(modifier = Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically , modifier = Modifier.padding(8.dp)) {
                Icon(painter = painterResource(R.drawable.outline_shopping_bag_speed_24) , contentDescription = "icon"
                 , tint = Blue)
                Text(text = "Create Account", fontSize = 24.sp, fontWeight = FontWeight.Bold , color = Blue)
            }
            Spacer(modifier = Modifier.height(8.dp))

            TextField(value = email , onValueChange = {
               email = it
            } , placeholder = {Text(text = "Email")} , leadingIcon = {
                Icon(imageVector = Icons.Default.Email , contentDescription = "email")
            } , modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp) , colors = TextFieldDefaults.colors(unfocusedContainerColor =
            if (isSystemInDarkTheme()) Color.DarkGray else Color.Transparent))
            Spacer(modifier = Modifier.height(16.dp))

            TextField(value = password , onValueChange = {
                password = it
            } , placeholder = {Text(text = "Password")} ,
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Password , contentDescription = "password")
                } , modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp) , colors = TextFieldDefaults.colors(unfocusedContainerColor =
                if (isSystemInDarkTheme()) Color.DarkGray else Color.Transparent))
            Spacer(modifier = Modifier.height(16.dp))

            TextField(value = phone , onValueChange = {
                phone = it
            }, placeholder = {Text(text = "Phone")} ,
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Phone , contentDescription = "phone")
                } , modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp) , colors = TextFieldDefaults.colors(unfocusedContainerColor =
                if (isSystemInDarkTheme()) Color.DarkGray else Color.Transparent))
            Spacer(modifier = Modifier.height(16.dp))
            TextField(value = address , onValueChange = {
               address = it
            } , placeholder = {Text(text = "Address")} ,
                leadingIcon = {
                    Icon(imageVector = Icons.Default.LocationOn , contentDescription = "Address")
                } , modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp) , colors = TextFieldDefaults.colors(unfocusedContainerColor =
                    if (isSystemInDarkTheme()) Color.DarkGray else Color.Transparent))
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth() , verticalAlignment = Alignment.CenterVertically , horizontalArrangement = Arrangement.Center) {
                  Button(onClick = {
                      if (email.endsWith("@gmail.com") && email.length>=10 && password.trim().length >= 8 && phone.trim().length == 10 && phone.isDigitsOnly() && address.trim().isNotEmpty() ){
                          auth.createUserWithEmailAndPassword(email , password).addOnSuccessListener { it ->
firestore.collection("users").add(User(id = it.user?.uid.toString() , name = email.split("@gmail")[0] , email = email ,
    password = password , phone = phone , address = address)).addOnSuccessListener {
    Toast.makeText(context, "Account Created", Toast.LENGTH_SHORT).show()
    navController.navigate(HomeScreen)
}
                          }
                      }
                      else{
                          Toast.makeText(context, "Enter correct credentials", Toast.LENGTH_SHORT).show()
                      }
                  }  , colors = ButtonDefaults.buttonColors(Blue) , shape = RectangleShape) {
                      Text(text = "SignIn")
                  }
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "LogIn" , fontSize = 18.sp , color = Blue , modifier = Modifier.clickable{
                    email = ""
                    password = ""
                    isCreate = false
                })
            }
        }
        else{
            Spacer(modifier = Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically , modifier = Modifier.padding(8.dp)) {
                Icon(painter = painterResource(R.drawable.outline_shopping_bag_speed_24) , contentDescription = "icon"
                    , tint = Blue)
                Text(text = "Welcome Back", fontSize = 24.sp, fontWeight = FontWeight.Bold , color = Blue)
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextField(value = email , onValueChange = {
                email = it
            } , placeholder = {Text(text = "Email")} , leadingIcon = {
                Icon(imageVector = Icons.Default.Email , contentDescription = "email")
            } , modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp) , colors = TextFieldDefaults.colors(unfocusedContainerColor =
                if (isSystemInDarkTheme()) Color.DarkGray else Color.Transparent))
            Spacer(modifier = Modifier.height(16.dp))

            TextField(value = password , onValueChange = {
                password = it
            } , placeholder = {Text(text = "Password")} ,
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Password , contentDescription = "password")
                } , modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp) , colors = TextFieldDefaults.colors(unfocusedContainerColor =
                    if (isSystemInDarkTheme()) Color.DarkGray else Color.Transparent))
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth() , verticalAlignment = Alignment.CenterVertically , horizontalArrangement = Arrangement.Center) {
                Button(onClick = {
                    if (email.trim().isNotEmpty() && password.trim().isNotEmpty()){
                    auth.signInWithEmailAndPassword(email , password).addOnSuccessListener {
                        Toast.makeText(context, "Logged in", Toast.LENGTH_SHORT).show()
                        navController.navigate(HomeScreen)
                    }.addOnFailureListener {
                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                    }
                    }
                    else{
                        Toast.makeText(context, "Enter correct credentials", Toast.LENGTH_SHORT).show()
                    }
                }  , colors = ButtonDefaults.buttonColors(Blue) , shape = RectangleShape) {
                    Text(text = "LogIn")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Forget Password?" , fontSize = 18.sp , color = Blue , modifier = Modifier.clickable{
                    email = ""
                    password = ""
                    showDialog = true

                })
            }
        }
    }
}

@Composable
fun DialogReset(onDismiss : () -> Unit , email : MutableState<String>) {
    val context = LocalContext.current
    Dialog(onDismissRequest = {
        onDismiss()
    }) {
        Box(modifier = Modifier.fillMaxWidth()){
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth() , horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Enter email for password reset")
                    OutlinedTextField(value = email.value , onValueChange = {
                        email.value = it
                    })
                    Spacer(modifier = Modifier.height(18.dp))
                    Button(onClick = {
                        Firebase.auth.sendPasswordResetEmail(email.value).addOnSuccessListener {
                            Toast.makeText(context, "Email Sent", Toast.LENGTH_SHORT).show()
                        }
                        onDismiss()
                    } , colors = ButtonDefaults.buttonColors(Blue , Color.White)) {
                        Text(text = "Send")
                    }
                }
            }
        }
    }
}
data class User(val id : String = "" , val name : String = "" , val email : String = "" , val password : String = "" , val phone : String = "" , val address : String = "")