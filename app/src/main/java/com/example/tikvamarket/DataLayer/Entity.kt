package com.example.tikvamarket.DataLayer

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey val id: Int,
    val name: String,
    val price: Double,
    val imageUrl: String
)

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey val productId: Int,
    val quantity: Int
)