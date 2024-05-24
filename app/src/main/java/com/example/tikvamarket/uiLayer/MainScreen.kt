package com.example.tikvamarket.uiLayer

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tikvamarket.DataLayer.CartItem
import com.example.tikvamarket.DataLayer.Product
import com.example.tikvamarket.PresentationLayer.AppViewModel
import com.example.tikvamarket.R

@Composable
fun currentRoute(navController: NavController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

@Composable
fun MainScreen(viewModel: AppViewModel = viewModel()) {
    val navController = rememberNavController()
    val cartItems by viewModel.cartItems.observeAsState(emptyList())
    val products by viewModel.products.observeAsState(emptyList())

    val totalPrice = cartItems.sumOf { cartItem ->
        val product = products.firstOrNull { it.id == cartItem.productId }
        (product?.price ?: 0.0) * cartItem.quantity
    }

    Scaffold(
        topBar = { TopAppBar(navController = navController, totalPrice = totalPrice) },
        bottomBar = { BottomNavigationBar(navController, cartItemCount = cartItems.sumOf { it.quantity }) },
        contentColor = MaterialTheme.colorScheme.onBackground,
        containerColor = Color.LightGray.copy(alpha = 0.6f),
    ) { padding ->
        NavHost(navController, startDestination = "home", modifier = Modifier.padding(padding)) {
            composable("home") { HomeScreen(viewModel) }
            composable("cart") { CartScreen(viewModel) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(navController: NavController, totalPrice: Double) {
    val currentRoute = currentRoute(navController)

    CenterAlignedTopAppBar(
        title = {
            Column {
                if (currentRoute == "cart") {
                    Text(text = stringResource(id = R.string.total_price, totalPrice), color = MaterialTheme.colorScheme.primary)
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@Composable
fun HomeScreen(viewModel: AppViewModel) {
    val products by viewModel.products.observeAsState(emptyList())

    LazyVerticalGrid(columns = GridCells.Adaptive(150.dp)) {
        items(products) { product ->
            ProductItem(
                product = product,
                onAddToCart = { prod, quantity -> viewModel.addToCart(prod, quantity) },
                onRemoveFromCart = { viewModel.removeFromCart(it) }
            )
        }
    }
}

@Composable
fun CartScreen(viewModel: AppViewModel) {
    val cartItems by viewModel.cartItems.observeAsState(emptyList())
    val products by viewModel.products.observeAsState(emptyList())

    Column(modifier = Modifier.padding(16.dp)) {
        if (cartItems.isNotEmpty()) {
            LazyColumn {
                items(cartItems) { cartItem ->
                    val product = products.firstOrNull { it.id == cartItem.productId }
                    if (product != null) {
                        CartItemView(cartItem = cartItem, product = product, onRemoveFromCart = { viewModel.removeFromCart(product) })
                    }
                }
            }
        } else {
            Text(stringResource(id = R.string.cart_empty), color = MaterialTheme.colorScheme.onBackground)
        }
    }
}

@Composable
fun CartItemView(cartItem: CartItem, product: Product, onRemoveFromCart: (Product) -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .border(
                width = 0.5.dp,
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primary
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, color = MaterialTheme.colorScheme.onSurface)
                Text("Цена: ${product.price}₸", color = MaterialTheme.colorScheme.onSurface)
                Text("Количество: ${cartItem.quantity}", color = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { onRemoveFromCart(product) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(stringResource(id = R.string.remove), color = MaterialTheme.colorScheme.onSecondary)
            }
        }
    }
}

@Composable
fun ProductItem(product: Product, onAddToCart: (Product, Int) -> Unit, onRemoveFromCart: (Product) -> Unit) {
    val quantityState = remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Image(
                painter = painterResource(id = product.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                product.name,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(bottom = 4.dp, start = 16.dp, end = 16.dp)
            )
            Text(
                "${product.price}₸",
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(bottom = 8.dp, start = 16.dp, end = 16.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    shape = RoundedCornerShape(16.dp),
                    value = quantityState.value,
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() }) {
                            quantityState.value = newValue
                        }
                    },
                    placeholder = { Text(text = "1") },
                    leadingIcon = {
                        IconButton(
                            onClick = {
                                val quantity = (quantityState.value.toIntOrNull() ?: 1)
                                if (quantity > 1) {
                                    onRemoveFromCart(product)
                                }
                            }
                        ) {
                            Text("-")
                        }
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                val quantity = (quantityState.value.toIntOrNull() ?: 1)
                                onAddToCart(product, quantity)
                            }
                        ) {
                            Text("+")
                        }
                    },
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    singleLine = true,
                    isError = quantityState.value == "0"
                )
            }
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigationBar(navController: NavController, cartItemCount: Int) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
        tonalElevation = 0.dp,
    ) {
        val currentRoute = currentRoute(navController)
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Filled.Home,
                    contentDescription = null,
                    tint = if (currentRoute == "home") MaterialTheme.colorScheme.primary else Color.LightGray,
                    modifier = Modifier.scale(1.4f)
                )
            },
            label = {
                Text(
                    text = stringResource(id = R.string.home),
                    color = if (currentRoute == "home") MaterialTheme.colorScheme.primary else Color.LightGray
                )
            },
            selected = currentRoute == "home",
            onClick = {
                navController.navigate("home") {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
        NavigationBarItem(
            icon = {
                BadgedBox(
                    badge = {
                        if (cartItemCount > 0) {
                            Badge { Text(cartItemCount.toString()) }
                        }
                    }
                ) {
                    Icon(
                        Icons.Filled.ShoppingCart,
                        contentDescription = null,
                        tint = if (currentRoute == "cart") MaterialTheme.colorScheme.primary else Color.LightGray,
                        modifier = Modifier.scale(1.4f)
                    )
                }
            },
            label = {
                Text(
                    text = stringResource(id = R.string.cart),
                    color = if (currentRoute == "cart") MaterialTheme.colorScheme.primary else Color.LightGray
                )
            },
            selected = currentRoute == "cart",
            onClick = {
                navController.navigate("cart") {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
}

