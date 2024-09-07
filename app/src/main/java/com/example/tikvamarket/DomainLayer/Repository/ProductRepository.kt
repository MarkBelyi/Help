package com.example.tikvamarket.DomainLayer.Repository

import com.example.tikvamarket.DataLayer.DAO.ProductDao
import com.example.tikvamarket.DataLayer.Entitys.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getAllProducts(): Flow<List<Product>>
    suspend fun insertAll(products: List<Product>)
}

class ProductRepositoryImpl(private val productDao: ProductDao) : ProductRepository {
    override fun getAllProducts(): Flow<List<Product>> = productDao.getAllProducts()
    override suspend fun insertAll(products: List<Product>) = productDao.insertAll(products)
}
