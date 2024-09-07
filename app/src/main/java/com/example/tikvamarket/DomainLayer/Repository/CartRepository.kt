package com.example.tikvamarket.DomainLayer.Repository

import com.example.tikvamarket.DataLayer.DAO.CartItemDao
import com.example.tikvamarket.DataLayer.Entitys.CartItem
import kotlinx.coroutines.flow.Flow

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