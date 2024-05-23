package com.example.tikvamarket.DomainLayer

import com.example.tikvamarket.DataLayer.CartItem
import com.example.tikvamarket.DataLayer.CartItemDao
import com.example.tikvamarket.DataLayer.Product
import com.example.tikvamarket.DataLayer.ProductDao
import kotlinx.coroutines.flow.Flow
import com.example.tikvamarket.DataLayer.User
import com.example.tikvamarket.DataLayer.UserDao

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


interface UserRepository {
    suspend fun insertUser(user: User)
    suspend fun getUserByEmail(email: String): User?
}

class UserRepositoryImpl(private val userDao: UserDao) : UserRepository {
    override suspend fun insertUser(user: User) = userDao.insertUser(user)
    override suspend fun getUserByEmail(email: String): User? = userDao.getUserByEmail(email)
}