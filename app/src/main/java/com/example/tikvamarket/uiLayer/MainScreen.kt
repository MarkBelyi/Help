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

    // Calculate total price
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
    val title = when (currentRoute) {
        "home" -> stringResource(id = R.string.home)
        "cart" -> stringResource(id = R.string.cart)
        else -> stringResource(id = R.string.app_name)
    }

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
    val quantityState = remember { mutableStateOf(1) }

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
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = product.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            Text(
                product.name,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                "${product.price}₸",
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { onRemoveFromCart(product) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "-", color = MaterialTheme.colorScheme.background)
                }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = quantityState.value.toString(),
                    onValueChange = { newValue ->
                        val quantity = newValue.toIntOrNull()
                        if (quantity != null && quantity > 0) {
                            quantityState.value = quantity
                        }
                    },
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(4.dp))
                Button(
                    onClick = { onAddToCart(product, quantityState.value) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "+", color = MaterialTheme.colorScheme.background)
                }
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

