package com.example.tikvamarket.PresentationLayer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tikvamarket.DataLayer.CartItem
import com.example.tikvamarket.DataLayer.Product
import com.example.tikvamarket.DataLayer.User
import com.example.tikvamarket.DomainLayer.CartRepository
import com.example.tikvamarket.DomainLayer.ProductRepository
import com.example.tikvamarket.DomainLayer.UserRepository
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class AppViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _products = MutableLiveData<List<Product>>(emptyList())
    val products: LiveData<List<Product>> get() = _products

    private val _cartItems = MutableLiveData<List<CartItem>>(emptyList())
    val cartItems: LiveData<List<CartItem>> get() = _cartItems

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf("")

    init {
        loadProducts()
        loadCartItems()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            productRepository.getAllProducts().collect { productList ->
                _products.postValue(productList)
            }
        }
    }

    private fun loadCartItems() {
        viewModelScope.launch {
            cartRepository.getAllCartItems().collect { cartItemList ->
                _cartItems.postValue(cartItemList)
            }
        }
    }

    fun addToCart(product: Product, quantity: Int) {
        viewModelScope.launch {
            val currentCartItem = cartRepository.getCartItem(product.id)
            val newQuantity = (currentCartItem?.quantity ?: 0) + quantity
            cartRepository.insert(CartItem(product.id, newQuantity))
            loadCartItems()
        }
    }

    fun removeFromCart(product: Product) {
        viewModelScope.launch {
            val currentCartItem = cartRepository.getCartItem(product.id)
            if (currentCartItem != null) {
                val newQuantity = currentCartItem.quantity - 1
                if (newQuantity > 0) {
                    cartRepository.insert(CartItem(product.id, newQuantity))
                } else {
                    cartRepository.deleteByProductId(product.id)
                }
                loadCartItems()
            }
        }
    }

    fun login(onLoginSuccess: () -> Unit, onRegister: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = ""
            try {
                val user = userRepository.getUserByEmail(email)
                if (user != null && User.checkPassword(password, user.hashedPassword)) {
                    onLoginSuccess()
                } else {
                    errorMessage = "Invalid email or password"
                }
            } catch (e: Exception) {
                errorMessage = "An error occurred: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun register(onRegisterSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = ""
            try {
                if (!isValidEmail(email)) {
                    errorMessage = "Invalid email format"
                }
                else if (!isValidPassword(password)) {
                    errorMessage = "Password must be at least 8 characters long and contain at least one uppercase letter, one number, and one special character"
                }
                else {
                    val existingUser = userRepository.getUserByEmail(email)
                    if (existingUser != null) {
                        errorMessage = "Email already exists"
                    } else {
                        val user = User.create(email, password)
                        userRepository.insertUser(user)
                        onRegisterSuccess()
                    }
                }
            } catch (e: Exception) {
                errorMessage = "An error occurred: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordPattern = "^(?=.*[0-9])(?=.*[A-Z]).{8,}$"
        val passwordMatcher = Regex(passwordPattern)
        return passwordMatcher.matches(password)
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)\$")
        return emailPattern.matcher(email).matches()
    }
}

