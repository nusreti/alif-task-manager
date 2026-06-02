package com.example.alif.domain.repository

import com.example.alif.domain.model.User

interface UserRepository {
    suspend fun register(login: String, passwordHash: String): Result<User>
    suspend fun login(login: String, passwordHash: String): Result<User>
    suspend fun getUserById(id: Long): User?
}
