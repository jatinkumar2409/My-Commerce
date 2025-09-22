package com.example.mycommerce.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import com.example.mycommerce.ui.theme.Blue

@Composable
fun BottomBar(navController: NavController , bottomNavViewModel: BottomNavViewModel) {
    var selected by rememberSaveable {
        mutableStateOf(bottomNavViewModel.selectedTab)
    }
    val screens = listOf(BottomScreen("Home" , Icons.Outlined.Home , Icons.Filled.Home , HomeScreen) ,
        BottomScreen("Orders" , Icons.Outlined.ShoppingBag , Icons.Filled.ShoppingBag ,
            OrdersScreen) ,
        BottomScreen("Cart" , Icons.Outlined.ShoppingCart , Icons.Filled.ShoppingCart , CartScreen) ,
        BottomScreen("Account" , Icons.Outlined.AccountCircle , Icons.Filled.AccountCircle ,
            UserScreen))

    NavigationBar(windowInsets = NavigationBarDefaults.windowInsets , contentColor = Blue) {
        screens.forEachIndexed { i , item ->
            NavigationBarItem(selected = i==selected  , colors = NavigationBarItemDefaults.colors(unselectedTextColor = Blue , selectedTextColor = Blue), onClick = { if (selected != i){
                selected = i
                bottomNavViewModel.setTab(i)
                navController.navigate(item.screen){
                    popUpTo(0){
                        inclusive = true
                    }
                }
            }
                                                                 } , icon = { Icon(
                if (selected == i) item.selectedIcon else item.unselectedIcon , contentDescription = "icon" , tint = Blue
            ) } , label = { Text(text = item.name) })
        }
    }
}
data class BottomScreen(val name : String , val unselectedIcon : ImageVector , val selectedIcon : ImageVector , val screen : Any)