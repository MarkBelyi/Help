package com.example.tikvamarket.DomainLayer

import com.example.tikvamarket.DataLayer.CartItem
import com.example.tikvamarket.DataLayer.CartItemDao
import com.example.tikvamarket.DataLayer.Product
import com.example.tikvamarket.DataLayer.ProductDao
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getAllProducts(): Flow<List<Product>>
    suspend fun insertAll(products: List<Product>)
}

class ProductRepositoryImpl(private val productDao: ProductDao) : ProductRepository {
    override fun getAllProducts(): Flow<List<Product>> = productDao.getAllProducts()
    override suspend fun insertAll(products: List<Product>) = productDao.insertAll(products)
}

interface CartRepository {
    fun getAllCartItems(): Flow<List<CartItem>>
    suspend fun insert(cartItem: CartItem)
    suspend fun deleteByProductId(productId: Int)
    suspend fun getCartItem(productId: Int): CartItem?
}

class CartRepositoryImpl(private val cartItemDao: CartItemDao) : CartRepository {
    override fun getAllCartItems(): Flow<List<CartItem>> = cartItemDao.getAllCartItems()
    override suspend fun insert(cartItem: CartItem) = cartItemDao.insert(cartItem)
    override suspend fun deleteByProductId(productId: Int) = cartItemDao.deleteByProductId(productId)
    override suspend fun getCartItem(productId: Int): CartItem? = cartItemDao.getCartItem(productId)
}