package com.example.tikvamarket.uiLayer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
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

    Scaffold(
        topBar = { TopAppBar(navController = navController) },
        bottomBar = { BottomNavigationBar(navController) },
        contentColor = MaterialTheme.colorScheme.onBackground,
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        NavHost(navController, startDestination = "home", modifier = Modifier.padding(padding)) {
            composable("home") { HomeScreen(viewModel) }
            composable("cart") { CartScreen(viewModel) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(navController: NavController) {
    val currentRoute = currentRoute(navController)
    val title = when (currentRoute) {
        "home" -> stringResource(id = R.string.home)
        "cart" -> stringResource(id = R.string.cart)
        else -> stringResource(id = R.string.app_name)
    }

    CenterAlignedTopAppBar(
        title = { Text(text = title, color = MaterialTheme.colorScheme.onPrimary) },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun HomeScreen(viewModel: AppViewModel) {
    val products by viewModel.products.collectAsState()
    LazyVerticalGrid(columns = GridCells.Adaptive(150.dp)) {
        items(products) { product ->
            ProductItem(product = product, onAddToCart = { viewModel.addToCart(it) }, onRemoveFromCart = { viewModel.removeFromCart(it) })
        }
    }
}

@Composable
fun CartScreen(viewModel: AppViewModel) {
    val cartItems by viewModel.cartItems.collectAsState()
    val products by viewModel.products.collectAsState()

    Column {
        Text(stringResource(id = R.string.cart_title), color = MaterialTheme.colorScheme.onBackground)
        LazyColumn {
            items(cartItems) { cartItem ->
                val product = products.firstOrNull { it.id == cartItem.productId }
                if (product != null) {
                    CartItemView(cartItem = cartItem, product = product, onRemoveFromCart = { viewModel.removeFromCart(product) })
                }
            }
        }
        val totalPrice = cartItems.sumOf { cartItem ->
            val product = products.firstOrNull { it.id == cartItem.productId }
            (product?.price ?: 0.0) * cartItem.quantity
        }
        Text(stringResource(id = R.string.total_price, totalPrice), color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
fun CartItemView(cartItem: CartItem, product: Product, onRemoveFromCart: (Product) -> Unit) {
    Card(
        modifier = Modifier.padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row {
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, color = MaterialTheme.colorScheme.onSurface)
                Text("Цена: ${product.price}₸", color = MaterialTheme.colorScheme.onSurface)
                Text("Количество: ${cartItem.quantity}", color = MaterialTheme.colorScheme.onSurface)
            }
            Button(
                onClick = { onRemoveFromCart(product) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(stringResource(id = R.string.remove), color = MaterialTheme.colorScheme.onSecondary)
            }
        }
    }
}

@Composable
fun ProductItem(product: Product, onAddToCart: (Product) -> Unit, onRemoveFromCart: (Product) -> Unit) {
    Card(
        modifier = Modifier.padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            Image(
                painter = rememberImagePainter(data = product.imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth()
            )
            Text(product.name, color = MaterialTheme.colorScheme.onSurface)
            Text("${product.price}₸", color = MaterialTheme.colorScheme.onSurface)
            Row {
                Button(
                    onClick = { onAddToCart(product) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text(stringResource(id = R.string.add_to_cart), color = MaterialTheme.colorScheme.onSecondary)
                }
                Button(
                    onClick = { onRemoveFromCart(product) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text(stringResource(id = R.string.remove_from_cart), color = MaterialTheme.colorScheme.onSecondary)
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary) },
            label = { Text(text = stringResource(id = R.string.home), color = MaterialTheme.colorScheme.onPrimary) },
            selected = currentRoute(navController) == "home",
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
            icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary) },
            label = { Text(stringResource(id = R.string.cart), color = MaterialTheme.colorScheme.onPrimary) },
            selected = currentRoute(navController) == "cart",
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
