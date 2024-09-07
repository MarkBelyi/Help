package com.example.tikvamarket.DataLayer.Entitys

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.mindrot.jbcrypt.BCrypt

@Entity(tableName = "users")
data class User(
    @PrimaryKey val email: String,
    val hashedPassword: String
) {
    companion object {
        fun create(email: String, password: String): User {
            val hashedPassword = hashPassword(password)
            return User(email, hashedPassword)
        }

        private fun hashPassword(password: String): String {
            return BCrypt.hashpw(password, BCrypt.gensalt())
        }

        fun checkPassword(password: String, hashedPassword: String): Boolean {
            return BCrypt.checkpw(password, hashedPassword)
        }
    }
}