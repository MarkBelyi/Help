package com.example.tikvamarket

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tikvamarket.DataLayer.AppDatabase
import com.example.tikvamarket.DataLayer.Entitys.Product
import com.example.tikvamarket.DomainLayer.AppViewModel
import com.example.tikvamarket.DomainLayer.Repository.CartRepository
import com.example.tikvamarket.DomainLayer.Repository.CartRepositoryImpl
import com.example.tikvamarket.DomainLayer.Repository.ProductRepository
import com.example.tikvamarket.DomainLayer.Repository.ProductRepositoryImpl
import com.example.tikvamarket.DomainLayer.Repository.UserRepository
import com.example.tikvamarket.DomainLayer.Repository.UserRepositoryImpl
import com.example.tikvamarket.PresentationLayer.uiLayer.LoginPage
import com.example.tikvamarket.PresentationLayer.uiLayer.MainScreen
import com.example.tikvamarket.PresentationLayer.uiLayer.RegistrationPage
import com.example.tikvamarket.ui.theme.TikvaMarketTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val productRepository by lazy { ProductRepositoryImpl(database.productDao()) }
    val cartRepository by lazy { CartRepositoryImpl(database.cartItemDao()) }
    val userRepository by lazy { UserRepositoryImpl(database.userDao()) }

    override fun onCreate() {
        super.onCreate()
        initializeDatabase()
    }

    private fun initializeDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            val initialProducts = listOf(
                Product(id = 1, name = "Apple", price = 100.0, imageRes = R.drawable.apple),
                Product(id = 2, name = "Banana", price = 50.0, imageRes = R.drawable.banana),
                Product(id = 3, name = "Orange", price = 70.0, imageRes = R.drawable.orange),
                Product(id = 4, name = "Strawberry", price = 120.0, imageRes = R.drawable.strawberry),
                Product(id = 5, name = "Watermelon", price = 300.0, imageRes = R.drawable.watermelon)
            )
            productRepository.insertAll(initialProducts)
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TikvaMarketTheme {
                val navController = rememberNavController()
                val viewModel: AppViewModel = viewModel(
                    factory = AppViewModelFactory(
                        (application as MyApplication).productRepository,
                        (application as MyApplication).cartRepository,
                        (application as MyApplication).userRepository
                    )
                )
                AppNavHost(navController, viewModel)
            }
        }
    }
}

class AppViewModelFactory(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AppViewModel(productRepository, cartRepository, userRepository) as T
    }
}
@Composable
fun AppNavHost(navController: NavHostController = rememberNavController(), viewModel: AppViewModel) {
    NavHost(navController, startDestination = "login") {
        composable("login") {
            LoginPage(viewModel = viewModel, onLoginSuccess = { navController.navigate("main") }, onRegister = { navController.navigate("register") })
        }
        composable("main") {
            MainScreen(viewModel = viewModel)
        }
        composable("register") {
            RegistrationPage(viewModel = viewModel, onRegisterSuccess = { navController.navigate("login") })
        }
    }
}
