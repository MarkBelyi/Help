package com.example.tikvamarket

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tikvamarket.DataLayer.AppDatabase
import com.example.tikvamarket.DataLayer.Product
import com.example.tikvamarket.DomainLayer.CartRepository
import com.example.tikvamarket.DomainLayer.CartRepositoryImpl
import com.example.tikvamarket.DomainLayer.ProductRepository
import com.example.tikvamarket.DomainLayer.ProductRepositoryImpl
import com.example.tikvamarket.PresentationLayer.AppViewModel
import com.example.tikvamarket.ui.theme.TikvaMarketTheme
import com.example.tikvamarket.uiLayer.MainScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val productRepository by lazy { ProductRepositoryImpl(database.productDao()) }
    val cartRepository by lazy { CartRepositoryImpl(database.cartItemDao()) }

    override fun onCreate() {
        super.onCreate()
        initializeDatabase()
    }

    private fun initializeDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            val initialProducts = listOf(
                Product(id = 1, name = "Apple", price = 100.0, imageUrl = "https://images.unsplash.com/photo-1567306226416-28f0efdc88ce"),
                Product(id = 2, name = "Banana", price = 50.0, imageUrl = "https://images.unsplash.com/photo-1574226516831-e1dff420e8e2"),
                Product(id = 3, name = "Orange", price = 70.0, imageUrl = "https://images.unsplash.com/photo-1584036561566-baf8f5f1b144"),
                Product(id = 4, name = "Strawberry", price = 120.0, imageUrl = "https://images.unsplash.com/photo-1601004890684-d8cbf643f5f2"),
                Product(id = 5, name = "Watermelon", price = 300.0, imageUrl = "https://images.unsplash.com/photo-1567306226416-28f0efdc88ce")
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
                val viewModel: AppViewModel = viewModel(
                    factory = AppViewModelFactory(
                        (application as MyApplication).productRepository,
                        (application as MyApplication).cartRepository
                    )
                )
                MainScreen(viewModel)
            }
        }
    }
}

class AppViewModelFactory(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AppViewModel(productRepository, cartRepository) as T
    }
}
