package com.example.tikvamarket.DataLayer.Entitys

import androidx.annotation.DrawableRes
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey val id: Int,
    val name: String,
    val price: Double,
    @DrawableRes val imageRes: Int
)
