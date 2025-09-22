package com.example.mycommerce.presentation.Home

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ElectricalServices
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SignalWifiConnectedNoInternet4
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.mycommerce.R
import com.example.mycommerce.data.model.Product
import com.example.mycommerce.data.model.Rating
import com.example.mycommerce.presentation.CategoryScreen
import com.example.mycommerce.presentation.HomeScreen
import com.example.mycommerce.presentation.ProductScreen
import com.example.mycommerce.ui.theme.Blue
import kotlinx.coroutines.delay

@SuppressLint("SuspiciousIndentation")
@Composable
fun HomeScreen(viewModel: HomeScreenViewModel , navController: NavHostController) {
    val controller = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
  val products by viewModel.products.collectAsStateWithLifecycle()
  val isConnected by viewModel.isConnected.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    var search by remember {
        mutableStateOf("")
    }
    Scaffold(topBar = {
        AppBar(true)
    }) { ip ->
        Column(modifier = Modifier.fillMaxSize().padding(top = ip.calculateTopPadding())) {
            if (!isConnected && products.isEmpty()) {
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
            } else if (isConnected && (products.isEmpty() || categories.isEmpty())) {
                LaunchedEffect(Unit) {
                    viewModel.getProducts()
                    viewModel.getCategories()
                }
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ProgressIndicatorDefaults.circularColor)
                }
            } else {
                OutlinedTextField(
                    value = search,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp).padding(top = 6.dp, bottom = 2.dp),
                    onValueChange = {
                        search = it
                    },
                    placeholder = { Text(text = "Search Products") },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = "searchoff",
                            modifier = Modifier
                                .clickable {
                                    controller?.hide()
                                    focusManager.clearFocus(true)
                                }
                        )
                    })

                Spacer(modifier = Modifier.height(8.dp))
                val pager = rememberPagerState(pageCount = { categories.size })
                LaunchedEffect(Unit) {
                    while (true) {
                        delay(3000)
                        pager.animateScrollToPage((pager.currentPage + 1) % pager.pageCount)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn {
                    item {
                        Column {
                            HorizontalPager(state = pager) {
                                CategoryCard(
                                    category = categories[pager.currentPage],
                                    icon = when (categories[pager.currentPage]) {
                                        "electronics" -> Icons.Default.ElectricalServices
                                        "jewelery" -> Icons.Default.AttachMoney
                                        else -> Icons.Default.ShoppingBag
                                    }
                                ){ it ->
                                    navController.navigate(CategoryScreen(it))
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            ShowDots(total = pager.pageCount, current = pager.currentPage)
                        }
                    }
                    items(products) { item ->
                        if (search.trim().isEmpty() || item.title.contains(
                                search,
                                ignoreCase = true
                            )
                        ) {
                            ShowProducts(product = item, rating = item.rating) { it ->
                             navController.navigate(ProductScreen(id = it))
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(isMain : Boolean , onClick : () -> Unit = {}) {
    TopAppBar(title = {
        Text(text = "MyCommerce")
    } , navigationIcon = {
        if (!isMain){
            Icon(imageVector = Icons.Default.KeyboardArrowLeft , contentDescription = "back" , modifier = Modifier.clickable{
                onClick()
            })
        }
    } , colors = TopAppBarDefaults.topAppBarColors(actionIconContentColor = Color.White , titleContentColor = Color.White , containerColor = Blue , navigationIconContentColor = Color.White))
}

@Composable
fun ShowProducts(product: Product , rating: Rating , onClick : (Int) -> Unit) {
    Spacer(modifier = Modifier.height(6.dp))
  Box(modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 4.dp)
      .clickable {
          onClick(product.id)
      }){
      Card(modifier = Modifier.fillMaxWidth() , elevation = CardDefaults.cardElevation(4.dp) , colors = CardDefaults.cardColors(if (isSystemInDarkTheme()) Color.DarkGray else Color.White)) {
          Row(modifier = Modifier.fillMaxWidth()) {
              AsyncImage(model = product.image  , error = painterResource(R.drawable.outline_shopping_bag_speed_24) , contentDescription = "product image" , modifier = Modifier.size(120.dp))
              Column(modifier = Modifier.fillMaxWidth()) {
                  Text(text = product.title , fontSize = 18.sp , fontWeight = FontWeight.SemiBold , maxLines = 2 , overflow = TextOverflow.Clip)
                  Text(text = product.description , maxLines = 2 , overflow = TextOverflow.Ellipsis)
                  Text(text ="₹${product.price.toInt()*80}" , fontSize = 20.sp , fontWeight = FontWeight.Bold )
                  Text(text = " ${rating.rate}⭐" , fontWeight = FontWeight.SemiBold , fontSize = 20.sp)
              }
          }
      }
  }
}

@Composable
fun CategoryCard(category : String , icon : ImageVector , onCardClick : (String) -> Unit) {
  Box(modifier = Modifier.fillMaxWidth().clickable{
      onCardClick(category)
  }){
      Card(modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp) , colors = CardDefaults.cardColors(
          when(category){
             "electronics" -> Color.Cyan
              "jewelery" -> Blue
              "women's clothing" -> Color.Red
              else -> Color.Magenta
          }
      )) {
          Column(modifier = Modifier
              .fillMaxWidth()
              .padding(8.dp)) {
              Row(modifier = Modifier.fillMaxWidth() , horizontalArrangement = Arrangement.Start) {
              Text(text = category.capitalize() , fontWeight = FontWeight.Bold , fontSize = 24.sp , color = Color.White)
              }
              Spacer(modifier = Modifier.height(48.dp))
              Row(modifier = Modifier.fillMaxWidth() , horizontalArrangement = Arrangement.End) {
                  Icon(imageVector = icon , contentDescription = "icon" , modifier = Modifier.size(32.dp) , tint = Color.White)
              }
          }
      }
  }
}
@Preview
@Composable
fun ShowDots(total : Int = 5 , current : Int = 1) {
    Row(modifier = Modifier.fillMaxWidth() , horizontalArrangement = Arrangement.Center) {
        LazyRow{
            items(total){ it ->
             Box(modifier = Modifier.padding(2.dp).clip(shape = CircleShape).size(if (it==current) 10.dp else 8.dp).background(if (it==current) Color.DarkGray else Color.Gray))
            }
        }
    }
}