package com.example.alif.data.repository

import com.example.alif.data.local.dao.UserDao
import com.example.alif.data.local.entity.toDomain
import com.example.alif.data.local.entity.toEntity
import com.example.alif.domain.model.User
import com.example.alif.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {

    override suspend fun register(login: String, passwordHash: String): Result<User> {
        val existing = userDao.getUserByLogin(login)
        if (existing != null) {
            return Result.failure(Exception("User already exists"))
        }
        val user = User(login = login, passwordHash = passwordHash)
        val id = userDao.insertUser(user.toEntity())
        return Result.success(user.copy(id = id))
    }

    override suspend fun login(login: String, passwordHash: String): Result<User> {
        val userEntity = userDao.getUserByLogin(login)
        return if (userEntity != null && userEntity.passwordHash == passwordHash) {
            Result.success(userEntity.toDomain())
        } else {
            Result.failure(Exception("Invalid login or password"))
        }
    }

    override suspend fun getUserById(id: Long): User? {
        return userDao.getUserById(id)?.toDomain()
    }
}
