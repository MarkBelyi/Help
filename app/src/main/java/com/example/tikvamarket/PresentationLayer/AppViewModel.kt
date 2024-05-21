package com.example.tikvamarket.PresentationLayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tikvamarket.DataLayer.CartItem
import com.example.tikvamarket.DataLayer.Product
import com.example.tikvamarket.DomainLayer.CartRepository
import com.example.tikvamarket.DomainLayer.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class AppViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    init {
        viewModelScope.launch {
            productRepository.getAllProducts().collect { productList ->
                _products.value = productList
            }
            cartRepository.getAllCartItems().collect { cartItemList ->
                _cartItems.value = cartItemList
            }
        }
    }

    fun addToCart(product: Product) {
        viewModelScope.launch {
            val currentCartItem = cartRepository.getCartItem(product.id)
            val newQuantity = currentCartItem?.quantity?.plus(1) ?: 1
            cartRepository.insert(CartItem(product.id, newQuantity))
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
            }
        }
    }
}
