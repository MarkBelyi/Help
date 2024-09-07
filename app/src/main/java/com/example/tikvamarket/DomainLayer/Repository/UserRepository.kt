package com.example.tikvamarket.DomainLayer.Repository

import com.example.tikvamarket.DataLayer.DAO.UserDao
import com.example.tikvamarket.DataLayer.Entitys.User

interface UserRepository {
    suspend fun insertUser(user: User)
    suspend fun getUserByEmail(email: String): User?
}

class UserRepositoryImpl(private val userDao: UserDao) : UserRepository {
    override suspend fun insertUser(user: User) = userDao.insertUser(user)
    override suspend fun getUserByEmail(email: String): User? = userDao.getUserByEmail(email)
}