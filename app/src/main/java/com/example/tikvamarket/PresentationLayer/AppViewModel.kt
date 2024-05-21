package com.example.tikvamarket.PresentationLayer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private val _products = MutableLiveData<List<Product>>(emptyList())
    val products: LiveData<List<Product>> get() = _products

    private val _cartItems = MutableLiveData<List<CartItem>>(emptyList())
    val cartItems: LiveData<List<CartItem>> get() = _cartItems

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

    fun addToCart(product: Product) {
        viewModelScope.launch {
            val currentCartItem = cartRepository.getCartItem(product.id)
            val newQuantity = currentCartItem?.quantity?.plus(1) ?: 1
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
}
